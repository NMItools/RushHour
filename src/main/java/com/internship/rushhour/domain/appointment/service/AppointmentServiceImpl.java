package com.internship.rushhour.domain.appointment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.internship.rushhour.domain.GoogleCalendar.service.GoogleCalendarService;
import com.internship.rushhour.domain.MicrosoftCalendar.service.MicrosoftCalendarService;
import com.internship.rushhour.domain.appointment.entity.Appointment;
import com.internship.rushhour.domain.appointment.models.AppointmentDTO;
import com.internship.rushhour.domain.appointment.models.AppointmentResponseDTO;
import com.internship.rushhour.domain.appointment.repository.AppointmentRepository;
import com.internship.rushhour.domain.role.models.Roles;
import com.internship.rushhour.infrastructure.deserializers.CustomAppointmentDeserializer;
import com.internship.rushhour.infrastructure.exceptions.InvalidStartTimeException;
import com.internship.rushhour.infrastructure.exceptions.OutsideProviderWorkingHoursException;
import com.internship.rushhour.infrastructure.exceptions.ResourceNotFoundException;
import com.internship.rushhour.infrastructure.exceptions.RoleNotRecognizedException;
import com.internship.rushhour.infrastructure.exceptions.UserActionNeededException;
import com.internship.rushhour.infrastructure.mail.EmailService;
import com.internship.rushhour.infrastructure.mail.EmailTask;
import com.internship.rushhour.infrastructure.mappers.AppointmentMapper;
import com.internship.rushhour.infrastructure.security.AuthorizationService;
import com.internship.rushhour.infrastructure.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.Timer;

@Service
public class AppointmentServiceImpl implements AppointmentService{
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final EmailService emailService;
    private final GoogleCalendarService googleCalendarService;
    private final MicrosoftCalendarService microsoftCalendarService;
    private final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    @Autowired
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, AppointmentMapper appointmentMapper,
                                  GoogleCalendarService googleCalendarService, MicrosoftCalendarService microsoftCalendarService, EmailService emailService){
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
        this.googleCalendarService = googleCalendarService;
        this.microsoftCalendarService = microsoftCalendarService;
        this.emailService = emailService;
    }

    @Override
    public AppointmentResponseDTO create(AppointmentDTO appointmentDTO) throws GeneralSecurityException, IOException, ParseException {
        Appointment appointment = appointmentMapper.dtoToEntity(appointmentDTO);
        appointment.populateEndDate();
        appointment.populatePrice();

        if (!isAppointmentDuringBusinessHours(appointment)) throw new OutsideProviderWorkingHoursException();

        if (appointment.getStartTime().isBefore(LocalDateTime.now().plusMinutes(15))) {
            throw new InvalidStartTimeException();
        }

        if (checkEmployeeAvailability(appointment.getEmployee().getId(), appointment.getStartTime(),
                appointment.getEndDate())) throw new UserActionNeededException("This employee is busy during the time of " +
                "the appointment, please modify your request.");

        if(appointmentDTO.clientWantsGoogleCalendar() || appointmentDTO.employeeWantsGoogleCalendar()){
            String eventId = createEvent(appointment, appointmentDTO.employeeWantsGoogleCalendar(), appointmentDTO.clientWantsGoogleCalendar());
            appointment.setGoogleCalendarId(eventId);
        }
        if(appointmentDTO.clientWantsMicrosoftCalendar() || appointmentDTO.employeeWantsMicrosoftCalendar()){
            String eventId = microsoftCalendarService.createEvent(appointment, appointmentDTO.clientWantsMicrosoftCalendar(), appointmentDTO.employeeWantsMicrosoftCalendar());
            appointment.setMicrosoftCalendarId(eventId);
        }

        appointment = appointmentRepository.save(appointment);

        emailService.sendAppointmentCreated(appointment);
        Date reminderDate = Date.from(appointment.getStartTime().minusHours(1).atZone(ZoneId.systemDefault()).toInstant());
        new Timer().schedule(new EmailTask(appointment,emailService), reminderDate);

        return appointmentMapper.entityToResponseDto(appointment);
    }

    @Override
    public AppointmentResponseDTO get(Long id) {
        return appointmentMapper.entityToResponseDto(appointmentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(id, "id", Appointment.class.getSimpleName())));
    }

    @Override
    public Page<AppointmentResponseDTO> getPaginated(Pageable pageable, CustomUserDetails user) {
        String userRole = AuthorizationService.getCurrentUserRole();
        PageImpl<AppointmentResponseDTO> resultPage = new PageImpl<>(new ArrayList<>());
        Roles role = Roles.valueOf(userRole);

        switch (role){
            case ROLE_PROVIDER_ADMINISTRATOR:
                resultPage = new PageImpl<>(appointmentRepository.findByEmployee_Provider_BusinessDomain(user.getEmailDomain(), pageable).stream()
                        .map(appointmentMapper::entityToResponseDto).toList());
                break;
            case ROLE_EMPLOYEE:
                resultPage = new PageImpl<>(appointmentRepository.findByEmployee_AccountId(user.getId(), pageable).stream()
                        .map(appointmentMapper::entityToResponseDto).toList());
                break;
            case ROLE_CLIENT:
                resultPage = new PageImpl<>(appointmentRepository.findByClient_AccountId(user.getId(), pageable).stream()
                        .map(appointmentMapper::entityToResponseDto).toList());
                break;
            default:{
                logger.warn("User without recognized role attempting to access Appointments Page");
                throw new RoleNotRecognizedException();
            }
        }

        return resultPage;
    }

    @Override
    public void delete(Long id) throws GeneralSecurityException, IOException {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(id, "id", Appointment.class.getSimpleName()));

        if(appointment.getGoogleCalendarId() != null)
            googleCalendarService.deleteEvent(appointment.getGoogleCalendarId());

        if(appointment.getMicrosoftCalendarId() != null)
            microsoftCalendarService.deleteEvent(appointment);

        appointmentRepository.deleteById(id);
    }

    @Override
    public AppointmentResponseDTO update(JsonPatch patch, Long id) throws JsonPatchException, IOException, GeneralSecurityException, ParseException {
        Appointment toPatch = appointmentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(id, "id", Appointment.class.getSimpleName()));

        LocalDateTime startTime1 = toPatch.getStartTime();
        LocalDateTime endTime1 = toPatch.getEndDate();
        String employee1 = toPatch.getEmployee().getAccount().getEmail();

        Appointment appointment = applyPatchToAppointment(patch, toPatch);

        if (!isAppointmentDuringBusinessHours(appointment)) throw new OutsideProviderWorkingHoursException();

        if(appointment.getGoogleCalendarId() != null)
            sendUpdateRequestToGoogle(startTime1, endTime1, employee1, appointment);
        if(appointment.getMicrosoftCalendarId() != null)
            microsoftCalendarService.updateEvent(appointment, true, true); // change

        return appointmentMapper.entityToResponseDto(appointmentRepository.save(appointment));
    }

    private boolean isAppointmentDuringBusinessHours(Appointment appointment){
        LocalTime providerStart = appointment.getEmployee().getProvider().getBusinessHoursStart();
        LocalTime providerEnd = appointment.getEmployee().getProvider().getBusinessHoursEnd();
        Set<DayOfWeek> workingDays = appointment.getEmployee().getProvider().getWorkingDays();

        LocalDateTime appointmentStart = appointment.getStartTime();
        LocalDateTime appointmentEnd = appointment.getEndDate();

        boolean isDuringWorkingDay = workingDays.contains(appointmentStart.getDayOfWeek());
        boolean isDuringWorkingHours = (appointmentStart.toLocalTime().isAfter(providerStart)
                && appointmentEnd.toLocalTime().isBefore(providerEnd) && appointmentEnd.toLocalTime().isAfter(providerStart));

        logger.info("Provider start: " + providerStart + "\t" + "Provider end: " + providerEnd);
        logger.info("Appointment start " + appointmentStart.toLocalTime() + "\t" + " Appointment End: " + appointmentEnd.toLocalTime());
        logger.info("Is during working hours: " + isDuringWorkingHours);
        logger.info("Day: " + appointmentStart.getDayOfWeek() + "\t in working days: " + isDuringWorkingDay);

        return isDuringWorkingDay && isDuringWorkingHours;
    }

    private boolean checkEmployeeAvailability(Long id, LocalDateTime start, LocalDateTime end){
        return appointmentRepository.isEmployeeBusy(id, start, end);
    }

    private Appointment applyPatchToAppointment(JsonPatch patch, Appointment targetAppointment)
            throws JsonPatchException, JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        SimpleModule simpleModule = new SimpleModule();
        CustomAppointmentDeserializer customAppointmentDeserializer = new CustomAppointmentDeserializer();
        simpleModule.addDeserializer(Appointment.class, customAppointmentDeserializer);
        objectMapper.registerModule(simpleModule);

        Appointment appointment = new Appointment();
        appointment.setId(targetAppointment.getId());
        appointment.setActivities(targetAppointment.getActivities());

        JsonNode patched = patch.apply(objectMapper.convertValue(appointment, JsonNode.class));

        return objectMapper.treeToValue(patched, Appointment.class);
    }

    private String createEvent(Appointment appointment, boolean employeeWantsGoogleCalendar, boolean clientWantsGoogleCalendar)
            throws GeneralSecurityException, IOException, ParseException {
        ArrayList<String> attendees = new ArrayList<>();

        if(employeeWantsGoogleCalendar){
            attendees.add(appointment.getEmployee().getAccount().getEmail());
        }

        if(clientWantsGoogleCalendar){
            attendees.add(appointment.getClient().getAccount().getEmail());
        }

        return googleCalendarService.saveEvent(
                attendees,
                appointment.getEmployee().getProvider().getName(),
                appointment.getStartTime(),
                appointment.getEndDate());
    }

    private void sendUpdateRequestToGoogle(LocalDateTime startTime1, LocalDateTime endTime1, String employee1, Appointment appointment)
            throws GeneralSecurityException, IOException, ParseException {
        LocalDateTime startTime = startTime1.equals(appointment.getStartTime()) ? null : appointment.getStartTime();
        LocalDateTime endTime = endTime1.equals(appointment.getEndDate()) ? null : appointment.getEndDate();
        String employee = employee1.equals(appointment.getEmployee().getAccount().getEmail())
                ? null
                : appointment.getEmployee().getAccount().getEmail();

        googleCalendarService.updateEvent(appointment.getGoogleCalendarId(), startTime, endTime, employee);
    }
}
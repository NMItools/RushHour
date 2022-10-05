package com.internship.rushhour.domain.appointment;

import com.internship.rushhour.domain.GoogleCalendar.service.GoogleCalendarService;
import com.internship.rushhour.domain.TestObjectFactory;
import com.internship.rushhour.domain.appointment.entity.Appointment;
import com.internship.rushhour.domain.appointment.models.AppointmentDTO;
import com.internship.rushhour.domain.appointment.models.AppointmentResponseDTO;
import com.internship.rushhour.domain.appointment.repository.AppointmentRepository;
import com.internship.rushhour.domain.appointment.service.AppointmentServiceImpl;
import com.internship.rushhour.infrastructure.exceptions.InvalidStartTimeException;
import com.internship.rushhour.infrastructure.exceptions.OutsideProviderWorkingHoursException;
import com.internship.rushhour.infrastructure.exceptions.ResourceNotFoundException;
import com.internship.rushhour.infrastructure.exceptions.UserActionNeededException;
import com.internship.rushhour.infrastructure.mail.EmailService;
import com.internship.rushhour.infrastructure.mappers.AppointmentMapper;
import com.internship.rushhour.infrastructure.security.AuthorizationService;
import com.internship.rushhour.infrastructure.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {
    @Mock
    AppointmentMapper appointmentMapper;

    @Mock
    AppointmentRepository appointmentRepository;

    @Mock
    GoogleCalendarService googleCalendarService;

    @Mock
    EmailService emailService;

    @InjectMocks
    AppointmentServiceImpl appointmentService;

    @BeforeEach
    void beforeEach(){
        lenient().when(appointmentMapper.entityToResponseDto(isA(Appointment.class))).thenReturn(
                TestObjectFactory.generateAppointmentResponseDTO()
        );
        lenient().when(appointmentMapper.dtoToEntity(isA(AppointmentDTO.class))).thenReturn(
                TestObjectFactory.generateAppointment()
        );
    }

    @Test
    void canRetrieveWhenIdExists(){
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(TestObjectFactory.generateAppointment()));
        appointmentService.get(1239L);
    }

    @Test
    void canNotRetrieveWhenIdNotExists(){
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> appointmentService.get(400L));
    }

    @Test
    void deleteWhenResourceNotExists(){
        when(appointmentRepository.findById(anyLong())).thenThrow(new ResourceNotFoundException(1l, "", ""));
        assertThrows(ResourceNotFoundException.class, () -> {
            appointmentService.delete(20L);
        });
    }

    @Test
    void canNotCreateWhenEmployeeBusy(){
        when(appointmentRepository.isEmployeeBusy(any(), any(),any())).thenReturn(true);

        assertThrows(UserActionNeededException.class, () ->
                appointmentService.create(TestObjectFactory.generateAppointmentDto()));
    }

    @Test
    void canNotCreateWhenDateInPast(){
        lenient().when(appointmentRepository.isEmployeeBusy(any(), any(),any())).thenReturn(false);

        Appointment a = TestObjectFactory.generateAppointment();
        a.setStartTime(LocalDateTime.of(2022, 6,22, 10, 0).minusHours(1L));
        AppointmentDTO adto = new AppointmentDTO(a.getStartTime(), a.getEmployee().getId(),a.getClient().getId(),
                TestObjectFactory.generateAppointmentDto().activities(), false, false, false ,false);

        lenient().when(appointmentMapper.dtoToEntity(any())).thenReturn(a);

        assertThrows(InvalidStartTimeException.class, () ->
                appointmentService.create(adto));
    }

    @Test
    void canNotCreateWhenOutsideWorkingHours(){
        lenient().when(appointmentRepository.isEmployeeBusy(any(), any(),any())).thenReturn(false);

        Appointment a = TestObjectFactory.generateAppointment();
        LocalTime providerEndTime = a.getEmployee().getProvider().getBusinessHoursEnd().plusHours(1);
        a.setStartTime(LocalDateTime.now().with(LocalTime.of(providerEndTime.getHour(), 0)));
        AppointmentDTO adto = new AppointmentDTO(a.getStartTime(), a.getEmployee().getId(),a.getClient().getId(),
                TestObjectFactory.generateAppointmentDto().activities(), false, false, false, false);

        lenient().when(appointmentMapper.dtoToEntity(any())).thenReturn(a);

        assertThrows(OutsideProviderWorkingHoursException.class, () ->
                appointmentService.create(adto));
    }

    @Test
    void canCreateWhenNoDateTimeErrors() throws GeneralSecurityException, IOException, ParseException {
        lenient().when(appointmentRepository.isEmployeeBusy(any(), any(),any())).thenReturn(false);

        Appointment a = TestObjectFactory.generateAppointment();
        a.setStartTime(LocalDateTime.of(9999, 6,22, 10, 0).plusHours(1L));
        AppointmentDTO adto = new AppointmentDTO(a.getStartTime(), a.getEmployee().getId(),a.getClient().getId(),
                TestObjectFactory.generateAppointmentDto().activities(), false, false, false ,false);

        lenient().when(appointmentMapper.dtoToEntity(any())).thenReturn(a);
        lenient().when(appointmentRepository.save(any())).thenReturn(a);

        appointmentService.create(adto);
    }

    @Test
    void getPaginatedTest(){
        List<Appointment> appointments = new ArrayList<>();
        appointments.add(TestObjectFactory.generateAppointment());
        appointments.add(TestObjectFactory.generateAppointment());
        appointments.add(TestObjectFactory.generateAppointment());
        appointments.add(TestObjectFactory.generateAppointment());

        when (appointmentRepository.findByClient_AccountId(isA(Long.class), isA(Pageable.class))).thenAnswer(i ->{
            Pageable pageable = (Pageable) i.getArguments()[1];
            return appointments.subList(0, pageable.getPageSize());
        });

        MockedStatic<AuthorizationService> mockedStatic = Mockito.mockStatic(AuthorizationService.class);
        mockedStatic.when(AuthorizationService::getCurrentUserRole).thenReturn("ROLE_CLIENT");

        int pageSize = 2;

        Pageable pageable = Pageable.ofSize(pageSize);
        Page<AppointmentResponseDTO> appointmentResponseDTOPage = appointmentService.getPaginated(pageable, new CustomUserDetails(1L, "testuser@test.com",
                "password123", List.of(new SimpleGrantedAuthority("ROLE_CLIENT"))));
        List<AppointmentResponseDTO> appointmentResponseDTOS = appointmentResponseDTOPage.stream().toList();

        assertThat(appointmentResponseDTOS.size()).isEqualTo(pageSize);
        assertThat(appointmentResponseDTOS).isNotEmpty();
    }



}

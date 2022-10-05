package com.internship.rushhour.domain.MicrosoftCalendar.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.rushhour.domain.MicrosoftCalendar.models.EventPayload;
import com.internship.rushhour.domain.MicrosoftCalendar.repository.MicrosoftCalendarRepository;
import com.internship.rushhour.domain.appointment.entity.Appointment;
import com.internship.rushhour.domain.appointment.service.AppointmentServiceImpl;
import com.internship.rushhour.infrastructure.encryptors.EncryptionUtil;
import com.microsoft.graph.models.Attendee;
import com.microsoft.graph.models.AttendeeType;
import com.microsoft.graph.models.DateTimeTimeZone;
import com.microsoft.graph.models.EmailAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class MicrosoftCalendarServiceImpl implements MicrosoftCalendarService{
    private final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);
    private final MicrosoftCalendarRepository microsoftCalendarRepository;

    private final EncryptionUtil encryptionUtil;
    private final String timeZone;
    private final MicrosoftApiRequestService microsoftApiRequestService;

    private final String apiPath;

    @Autowired
    public MicrosoftCalendarServiceImpl(EncryptionUtil encryptionUtil, MicrosoftCalendarRepository microsoftCalendarRepository,
                                        MicrosoftApiRequestService microsoftApiRequestService,
                                        @Value("${microsoft.calendar.service.timezone}") String timezone,
                                        @Value("${microsoft.calendar.service.api.path}")String apiPath) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, JsonProcessingException {
        this.encryptionUtil = encryptionUtil;
        this.microsoftCalendarRepository = microsoftCalendarRepository;
        this.timeZone = timezone;
        this.microsoftApiRequestService = microsoftApiRequestService;
        this.apiPath = apiPath;
    }

    @Override
    public String createEvent(Appointment a, boolean clientWantsCalendarEvent, boolean employeeWantsCalendarEvent) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, JsonProcessingException {
        EventPayload payload = new EventPayload();
        payload.setSubject("Appointment at " + a.getEmployee().getProvider().getName());

        DateTimeTimeZone start = new DateTimeTimeZone();
        start.dateTime = a.getStartTime().toString().concat(":00.0000000");
        start.timeZone = timeZone;
        payload.setStart(start);
        DateTimeTimeZone end = new DateTimeTimeZone();
        end.dateTime = a.getEndDate().toString().concat(":00.0000000");;
        end.timeZone = timeZone;
        payload.setEnd(end);

        payload.setAttendees(getAttendeeListFromAppointment(a, clientWantsCalendarEvent, employeeWantsCalendarEvent));

        JsonNode node = new ObjectMapper().convertValue(payload, JsonNode.class);
        stripNulls(node);
        String requestBody = node.toString();

        String eventId = microsoftApiRequestService.genericMicrosoftHttpCaller(HttpMethod.POST, requestBody, apiPath);

        logger.info("Microsoft calendar event created.");
        return eventId;
    }

    @Override
    public void updateEvent(Appointment appointment, boolean clientWants, boolean employeeWants) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, JsonProcessingException {
        EventPayload payload = new EventPayload();

        DateTimeTimeZone start = new DateTimeTimeZone();
        start.dateTime = appointment.getStartTime().toString();
        start.timeZone = timeZone;
        payload.setStart(start);
        DateTimeTimeZone end = new DateTimeTimeZone();
        end.dateTime = appointment.getEndDate().toString();
        end.timeZone = timeZone;
        payload.setEnd(end);

        payload.setAttendees(getAttendeeListFromAppointment(appointment, clientWants, employeeWants));

        JsonNode node = new ObjectMapper().convertValue(payload, JsonNode.class);
        stripNulls(node);
        String requestBody = node.toString();
        microsoftApiRequestService.genericMicrosoftHttpCaller(HttpMethod.PATCH, requestBody,
                apiPath + "/" + appointment.getMicrosoftCalendarId());
        logger.info("Microsoft calendar event update.");
    }

    @Override
    public void deleteEvent(Appointment a) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, JsonProcessingException {
        microsoftApiRequestService.genericMicrosoftHttpCaller(HttpMethod.DELETE, null,
                apiPath + "/" + a.getMicrosoftCalendarId());
        logger.info("Microsoft calendar deleted.");
    }

    private void stripNulls(JsonNode node) {
        Iterator<JsonNode> it = node.iterator();
        while (it.hasNext()) {
            JsonNode child = it.next();
            if (child.isNull())
                it.remove();
            else
                stripNulls(child);
        }
    }

    private List<Attendee> getAttendeeListFromAppointment(Appointment appointment, boolean clientWantsCalendar, boolean employeeWantsCalendar){
        List<Attendee> attendees = new ArrayList<>();

        if (employeeWantsCalendar) {
            Attendee attendee = new Attendee();
            EmailAddress emailAddress = new EmailAddress();
            emailAddress.address = appointment.getEmployee().getAccount().getEmail();
            emailAddress.name = appointment.getEmployee().getAccount().getName();
            attendee.emailAddress = emailAddress;
            attendee.type = AttendeeType.REQUIRED;
            attendees.add(attendee);
        }

        if ( clientWantsCalendar ) {
            Attendee attendee2 = new Attendee();
            EmailAddress emailAddress2 = new EmailAddress();
            emailAddress2.address = appointment.getClient().getAccount().getEmail();
            emailAddress2.name = appointment.getClient().getAccount().getName();
            attendee2.emailAddress = emailAddress2;
            attendee2.type = AttendeeType.REQUIRED;
            attendees.add(attendee2);
        }

        return attendees;
    }
}

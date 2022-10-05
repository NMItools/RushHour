package com.internship.rushhour.domain.GoogleCalendar.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Collection;

public interface GoogleCalendarService {

    public String saveEvent(Collection<String> attendees, String location, LocalDateTime startTime,
                            LocalDateTime endTime) throws GeneralSecurityException, IOException, ParseException;

    public void updateEvent(String eventId, LocalDateTime startTime, LocalDateTime endTime, String employee)
            throws GeneralSecurityException, IOException, ParseException;

    public void deleteEvent(String eventId) throws GeneralSecurityException, IOException;

}

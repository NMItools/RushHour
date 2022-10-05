package com.internship.rushhour.domain.MicrosoftCalendar.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.internship.rushhour.domain.appointment.entity.Appointment;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface MicrosoftCalendarService {
    String createEvent(Appointment a, boolean clientWantsCalendarEvent, boolean employeeWantsCalendarEvent) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, JsonProcessingException;

    void updateEvent(Appointment appointment, boolean clientWants, boolean employeeWants) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, JsonProcessingException;

    void deleteEvent(Appointment a) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, JsonProcessingException;
}

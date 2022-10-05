package com.internship.rushhour.infrastructure.mail;

import com.internship.rushhour.domain.appointment.entity.Appointment;

public interface EmailService {
    void sendAppointmentCreated(Appointment appointment);
    void sendAccountCreated(String email, String name);
    void sendAppointmentReminder(Appointment appointment);
    void sendAnniversaryMessage(String email);
}

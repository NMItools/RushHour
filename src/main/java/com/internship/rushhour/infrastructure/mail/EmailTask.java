package com.internship.rushhour.infrastructure.mail;

import com.internship.rushhour.domain.appointment.entity.Appointment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class EmailTask extends TimerTask {
    private final Appointment appointment;
    private final EmailService emailService;
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailTask.class);

    public EmailTask(Appointment appointment, EmailService emailService) {
        this.appointment = appointment;
        this.emailService = emailService;
    }

    @Override
    public void run() {
       emailService.sendAppointmentReminder(appointment);
       LOGGER.info("Sending email reminder");
    }
}
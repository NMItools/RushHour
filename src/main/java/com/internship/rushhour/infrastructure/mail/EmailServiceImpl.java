package com.internship.rushhour.infrastructure.mail;

import com.internship.rushhour.domain.activity.entity.Activity;
import com.internship.rushhour.domain.appointment.entity.Appointment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final String baseEmployeeStringActivities = "Client %s made a new appointment with activities %s at %s";
    private final String baseClientStringActivities = "Your appointment with activities %s is scheduled at %s with employee %s";
    private final String baseEmployeeStringActivity = "Client %s made a new appointment with activity %s at %s";
    private final String baseClientStringActivity = "Your appointment with activity %s is scheduled at %s with employee %s";
    private final String subjectAppointment = "New appointment";
    private final String subjectAccount = "Welcome ";
    private final String subjectAppointmentReminder = "Appointment reminder";
    private final String appointmentReminder = "You have an appointment in 1H in %s";
    private final String subjectAnniversary = "Anniversary!";
    private final String anniversary = "Hello, it's your anniversary on the job today! Thank you for being an essential part of our success. We are proud to have you with us.";
    private final String messageAppointmentCreated = "Hello, we would like to inform you that there have been new changes to your calendar.  ";
    private final String messageAppointmentReminder = "Hello, we would like to remind you about your appointment.  ";
    private final TemplateEngine templateEngine;
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendAppointmentCreated(Appointment appointment) {
        String baseEmployeeString;
        String baseClientString;
        String activityString;

        if (appointment.getActivities().size() > 1) {
            baseEmployeeString = baseEmployeeStringActivities;
            baseClientString = baseClientStringActivities;
            List<String> activityList = appointment.getActivities().stream().map(Activity::getName).toList();
            activityString = String.join(", ", activityList);
        } else {
            baseEmployeeString = baseEmployeeStringActivity;
            baseClientString = baseClientStringActivity;
            activityString = appointment.getActivities().iterator().next().getName();
        }

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        String textEmployee = String.format(baseEmployeeString, appointment.getClient().getAccount().getName(),
                activityString, dateFormat.format(appointment.getStartTime()));
        String textClient = String.format(baseClientString, activityString, dateFormat.format(appointment.getStartTime()),
                appointment.getEmployee().getAccount().getName());

        sendNewAppointmentMessage(appointment.getEmployee().getAccount().getEmail(), subjectAppointment,
                textEmployee, true);
        sendNewAppointmentMessage(appointment.getClient().getAccount().getEmail(), subjectAppointment,
                textClient, true);
    }

    @Override
    public void sendAccountCreated(String email, String name) {
        sendWelcomeMessage(email, subjectAccount, name);
    }

    @Override
    public void sendAppointmentReminder(Appointment appointment) {
        sendNewAppointmentMessage(appointment.getClient().getAccount().getEmail(), subjectAppointmentReminder,
                String.format(appointmentReminder, appointment.getEmployee().getProvider().getName()), false);
    }

    private void sendTemplateMessage(String recipient, String subject, String process) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setSubject(subject);
            helper.setText(process, true);
            helper.setTo(recipient);
            mailSender.send(mimeMessage);
        }
        catch (Exception e) {
            LOGGER.error("Could not send mail: %s", e.getMessage());
        }
    }

    private void sendWelcomeMessage(String recipient, String subject, String name) {
        Context context = new Context();
        context.setVariable("message", subject + name);
        String process= templateEngine.process("welcome", context);
        sendTemplateMessage(recipient, subject, process);
    }

    private void sendNewAppointmentMessage(String recipient, String subject, String message, boolean newAppointment) {
        Context context = new Context();
        context.setVariable("subject", subject);
        if (newAppointment) {
            context.setVariable("message", messageAppointmentCreated + message);
        }
        else {
            context.setVariable("message", messageAppointmentReminder + message);
        }
        String process= templateEngine.process("appointment", context);
        sendTemplateMessage(recipient, subject, process);
    }

    @Override
    public void sendAnniversaryMessage(String email) {
        Context context = new Context();
        context.setVariable("subject", subjectAnniversary);
        context.setVariable("message", anniversary);
        String process= templateEngine.process("anniversary", context);
        sendTemplateMessage(email, subjectAnniversary, process);
    }
}
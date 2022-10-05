package com.internship.rushhour.domain.scheduling;

import com.internship.rushhour.domain.employee.service.EmployeeService;
import com.internship.rushhour.infrastructure.mail.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private final EmployeeService employeeService;
    private final EmailService emailService;

    public ScheduledTasks(EmployeeService employeeService, EmailService emailService){
        this.employeeService = employeeService;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void reportCurrentTime() {
        List<String> list = employeeService.findAllByHireDate(LocalDateTime.now().getMonthValue(), LocalDateTime.now().getDayOfMonth());
        log.info(String.format("Today, %d employees have anniversary!", list.size()));
        for(String employee : list){
            emailService.sendAnniversaryMessage(employee);
        }
    }
}

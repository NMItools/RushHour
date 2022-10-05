package com.internship.rushhour.domain.report.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalTime;

/**
 * The entity is used as an additional table for creating the availability report. It contains the daily working hours
 * (08:00 - 16:00) which are used in a CROSS JOIN with APPOINTMENT table. For MySQL v8.x and later this entity is not
 * needed since we could use CTE to generate it as a temporary table within SQL query. MySQL versions prior to 8. x
 * does not support CTE (Common Table Expressions).
 */
@Entity
public class WorkingHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    LocalTime hour;

    public WorkingHours() {
    }

    public WorkingHours(Long id, LocalTime hour) {
        this.id = id;
        this.hour = hour;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalTime getHour() {
        return hour;
    }

    public void setHour(LocalTime hour) {
        this.hour = hour;
    }
}

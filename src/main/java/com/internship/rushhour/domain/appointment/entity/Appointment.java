package com.internship.rushhour.domain.appointment.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.internship.rushhour.domain.activity.entity.Activity;
import com.internship.rushhour.domain.client.entity.Client;
import com.internship.rushhour.domain.employee.entity.Employee;
import com.internship.rushhour.infrastructure.serializers.AppointmentActivitySerializer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull(message = "Start date required")
    @JsonFormat(pattern = "yyyy-MM-dd@HH:mm")
    LocalDateTime startTime;

    LocalDateTime endDate;

    @NotNull(message = "An employee must be selected")
    @ManyToOne
    @JoinColumn(name = "employee")
    Employee employee;

    String googleCalendarId;
    String microsoftCalendarId;

    @NotNull(message = "A client must be selected")
    @ManyToOne
    @JoinColumn(name = "client")
    Client client;

    @NotNull(message = "At least one activity required for the appointment")
    @ManyToMany
    @JoinTable(name = "appointment_activities",
            joinColumns = @JoinColumn(name="appointment_id"),
            inverseJoinColumns = @JoinColumn(name="activity_id"))
    @JsonSerialize(contentUsing = AppointmentActivitySerializer.class)
    Set<Activity> activities;

    float price;

    public Appointment(){}

    public Appointment(Long id, LocalDateTime startDate, LocalDateTime endDate, Employee employee, Client client, Set<Activity> activities, float price, String googleCalendarId) {
        this.id = id;
        this.startTime = startDate;
        this.endDate = endDate;
        this.employee = employee;
        this.client = client;
        this.activities = activities;
        this.price = price;
        this.googleCalendarId = googleCalendarId;
    }

    public void populateEndDate(){
        Duration total =  activities.stream().map(Activity::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        this.endDate = startTime.plus(total);
    }

    public void populatePrice(){
        for ( Activity activity : activities ){
            this.price += activity.getPrice();
        }
        this.price = activities.stream().map(Activity::getPrice).reduce(0.0f, Float::sum);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Set<Activity> getActivities() {
        return activities;
    }

    public void setActivities(Set<Activity> activities) {
        this.activities = activities;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getGoogleCalendarId() {
        return googleCalendarId;
    }

    public void setGoogleCalendarId(String googleCalendarId) {
        this.googleCalendarId = googleCalendarId;
    }

    public String getMicrosoftCalendarId() {
        return microsoftCalendarId;
    }

    public void setMicrosoftCalendarId(String microsoftCalendarId) {
        this.microsoftCalendarId = microsoftCalendarId;
    }
}

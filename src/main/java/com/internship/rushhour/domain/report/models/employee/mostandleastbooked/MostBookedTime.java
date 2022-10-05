package com.internship.rushhour.domain.report.models.employee.mostandleastbooked;

public class MostBookedTime {

    private String time;
    private Long numberOfAppointments;

    public MostBookedTime(String time, Long numberOfAppointments) {
        this.time = time;
        this.numberOfAppointments = numberOfAppointments;
    }

    public MostBookedTime() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Long getNumberOfAppointments() {
        return numberOfAppointments;
    }

    public void setNumberOfAppointments(Long numberOfAppointments) {
        this.numberOfAppointments = numberOfAppointments;
    }
}

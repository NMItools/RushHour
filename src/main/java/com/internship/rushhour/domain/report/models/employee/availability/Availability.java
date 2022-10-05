package com.internship.rushhour.domain.report.models.employee.availability;

public class Availability {

    private String time;
    private String availableOrBooked;

    public Availability() {
    }

    public Availability(String time, String availableOrBooked) {
        this.time = time;
        this.availableOrBooked = availableOrBooked;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAvailableOrBooked() {
        return availableOrBooked;
    }

    public void setAvailableOrBooked(String availableOrBooked) {
        this.availableOrBooked = availableOrBooked;
    }
}

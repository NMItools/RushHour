package com.internship.rushhour.domain.report.models.employee.availability;

public class AvailabilityPerWeek {

    private String hour;
    private String monday;
    private String tuesday;
    private String wednesday;
    private String thursday;
    private String friday;

    public AvailabilityPerWeek() {
    }

    public AvailabilityPerWeek(String hour, String monday, String tuesday, String wednesday, String thursday, String friday) {
        this.hour = hour;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMonday() {
        return monday;
    }

    public void setMonday(String monday) {
        this.monday = monday;
    }

    public String getTuesday() {
        return tuesday;
    }

    public void setTuesday(String tuesday) {
        this.tuesday = tuesday;
    }

    public String getWednesday() {
        return wednesday;
    }

    public void setWednesday(String wednesday) {
        this.wednesday = wednesday;
    }

    public String getThursday() {
        return thursday;
    }

    public void setThursday(String thursday) {
        this.thursday = thursday;
    }

    public String getFriday() {
        return friday;
    }

    public void setFriday(String friday) {
        this.friday = friday;
    }
}

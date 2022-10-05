package com.internship.rushhour.domain.report.models.provider;

import com.internship.rushhour.domain.report.models.Report;

public class AvailabilityReport implements Report {

    private String employee;
    private String hour;
    private Long monday;
    private Long tuesday;
    private Long wednesday;
    private Long thursday;
    private Long friday;
    private Long saturday;
    private Long sunday;

    public AvailabilityReport() {
    }

    public AvailabilityReport(String employee, String hour, Long monday, Long tuesday, Long wednesday, Long thursday, Long friday, Long saturday, Long sunday) {
        this.employee = employee;
        this.hour = hour;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public Long getMonday() {
        return monday;
    }

    public void setMonday(Long monday) {
        this.monday = monday;
    }

    public Long getTuesday() {
        return tuesday;
    }

    public void setTuesday(Long tuesday) {
        this.tuesday = tuesday;
    }

    public Long getWednesday() {
        return wednesday;
    }

    public void setWednesday(Long wednesday) {
        this.wednesday = wednesday;
    }

    public Long getThursday() {
        return thursday;
    }

    public void setThursday(Long thursday) {
        this.thursday = thursday;
    }

    public Long getFriday() {
        return friday;
    }

    public void setFriday(Long friday) {
        this.friday = friday;
    }

    public Long getSaturday() {
        return saturday;
    }

    public void setSaturday(Long saturday) {
        this.saturday = saturday;
    }

    public Long getSunday() {
        return sunday;
    }

    public void setSunday(Long sunday) {
        this.sunday = sunday;
    }
}

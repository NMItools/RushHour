package com.internship.rushhour.domain.report.models.provider;

import com.internship.rushhour.domain.report.models.Report;

public class ProductivityReport implements Report {

    private String employee;
    private Long appointments;
    private Double hours;
    private Double productivity;

    public ProductivityReport() {
    }

    public ProductivityReport(String employee, Long appointments, Double hours, Double productivity) {
        this.employee = employee;
        this.appointments = appointments;
        this.hours = hours;
        this.productivity = productivity;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public Long getAppointments() {
        return appointments;
    }

    public void setAppointments(Long appointments) {
        this.appointments = appointments;
    }

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }

    public Double getProductivity() {
        return productivity;
    }

    public void setProductivity(Double productivity) {
        this.productivity = productivity;
    }
}

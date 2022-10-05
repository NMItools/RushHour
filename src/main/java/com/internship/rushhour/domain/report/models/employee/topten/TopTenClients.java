package com.internship.rushhour.domain.report.models.employee.topten;

public class TopTenClients {

    private Long id;
    private String clientName;
    private Double totalPrice;
    private Double totalTime;

    public TopTenClients(Long id, String clientName, Double totalPrice, Double totalTime) {
        this.id = id;
        this.clientName = clientName;
        this.totalPrice = totalPrice;
        this.totalTime = totalTime;
    }

    public TopTenClients() {
    }

    public Double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Double totalTime) {
        this.totalTime = totalTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}

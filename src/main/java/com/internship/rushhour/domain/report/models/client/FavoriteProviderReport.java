package com.internship.rushhour.domain.report.models.client;


public class FavoriteProviderReport implements Report{

    String provider;

    Double totalSpent;

    Long numberOfAppointments;


    public FavoriteProviderReport(String provider, Double totalSpent, Long numberOfAppointments) {
        this.provider = provider;
        this.totalSpent = totalSpent;
        this.numberOfAppointments = numberOfAppointments;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(Double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public Long getNumberOfAppointments() {
        return numberOfAppointments;
    }

    public void setNumberOfAppointments(Long numberOfAppointments) {
        this.numberOfAppointments = numberOfAppointments;
    }
}

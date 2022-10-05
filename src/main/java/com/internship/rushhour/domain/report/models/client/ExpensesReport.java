package com.internship.rushhour.domain.report.models.client;

public class ExpensesReport implements Report{

    String name;

    Double totalCost;

    public ExpensesReport(String provider_activity, Double totalCost) {
        this.name = provider_activity;
        this.totalCost = totalCost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }
}

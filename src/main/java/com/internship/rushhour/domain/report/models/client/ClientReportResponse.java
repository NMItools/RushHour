package com.internship.rushhour.domain.report.models.client;

import java.util.List;

public class ClientReportResponse {

    private String reportInformation;

    private List<? extends Report> reports;

    public ClientReportResponse() {
    }

    public ClientReportResponse(String report, List<? extends Report> reports) {
        this.reportInformation = report;
        this.reports = reports;
    }

    public String getReportInformation() {
        return reportInformation;
    }

    public void setReportInformation(String reportInformation) {
        this.reportInformation = reportInformation;
    }

    public List<? extends Report> getReports() {
        return reports;
    }

    public void setReports(List<? extends Report> reports) {
        this.reports = reports;
    }
}


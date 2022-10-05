package com.internship.rushhour.domain.report.models.employee.availability;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class AvailabilityRequest {

    @NotNull(message = "You must enter a date.")
    @Pattern(regexp = "^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$", message = "You must enter a date in format: yyyy-mm-dd.")
    private String date;

    public AvailabilityRequest(String date) {
        this.date = date;
    }

    public AvailabilityRequest() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

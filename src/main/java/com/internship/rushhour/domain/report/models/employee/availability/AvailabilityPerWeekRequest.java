package com.internship.rushhour.domain.report.models.employee.availability;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class AvailabilityPerWeekRequest {

    @NotNull(message = "You must enter a year.")
    @Pattern(regexp="(?:19|20)[0-9]{2}", message = "The year must contain only digits and valid years are between 1900 and 2099.")
    private String year;

    @Pattern(regexp="([1-9]|1[0-9]|2[0-9]|3[0-9]|4[0-9]|5[0-2])", message = "The week number must be between 1 and 52.")
    @NotNull(message = "You must enter a week of the year.")
    @Size(max = 2, message="Week need to be in range 1—52")
    private String week;

    public AvailabilityPerWeekRequest() {
    }

    public AvailabilityPerWeekRequest(String year, String week) {
        this.year = year;
        this.week = week;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }
}

package com.internship.rushhour.domain.report.models.employee.mostandleastbooked;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class MostBookedRequest {

    @NotNull(message = "You must choose between week or month.")
    @Pattern(regexp = "\\b(week|month)\\b", message = "You must choose between week and month.")
    private String perWeekOrMonth;

    @Pattern(regexp = "^([1-9]|1[012])$", message = "You must choose between 1 and 12 for month.")
    private String month;

    @Pattern(regexp="([1-9]|1[0-9]|2[0-9]|3[0-9]|4[0-9]|5[0-2])", message = "The week number must be between 1 and 52.")
    @Size(max = 2, message="Week need to be in range 1—52")
    private String week;

    @NotNull(message = "You must enter a year.")
    @Pattern(regexp="(?:19|20)[0-9]{2}", message = "The year must contain only digits and valid years are between 1900 and 2099.")
    private String year;

    public MostBookedRequest(String per_week_or_month, String month, String week, String year) {
        this.perWeekOrMonth = per_week_or_month;
        this.month = month;
        this.week = week;
        this.year = year;
    }

    public MostBookedRequest() {
    }

    public String getPerWeekOrMonth() {
        return perWeekOrMonth;
    }

    public void setPerWeekOrMonth(String perWeekOrMonth) {
        this.perWeekOrMonth = perWeekOrMonth;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
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

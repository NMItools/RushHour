package com.internship.rushhour.domain.report.models.employee.topten;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class TopTenRequest {

    @NotNull(message = "You must enter a year.")
    @Pattern(regexp="(?:19|20)[0-9]{2}", message = "The year must contain only digits and valid years are between 1900 and 2099.")
    private String year;

    @Pattern(regexp = "\\b(1|2|3|4)\\b", message = "You must choose between 1, 2, 3 and 4 for quarter.")
    private String quarter;

    @Pattern(regexp = "^([1-9]|1[012])$", message = "You must choose between 1 and 12 for month.")
    private String month;

    public TopTenRequest(String year) {
        this.year = year;
    }

    public TopTenRequest() {
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}

package com.internship.rushhour.domain.report.models.client;

import com.internship.rushhour.infrastructure.exceptions.UserActionNeededException;

import javax.validation.constraints.NotNull;

public class ExpensesReportRequest {

    @NotNull(message = "Name must be defined!")
    private String name;

    private Period reportFor ;

    private Long weekMonthQuarterValue;

    @NotNull(message = "Year must be defined!")
    private Long ofYear;

    public ExpensesReportRequest() {
    }

    public ExpensesReportRequest(String name, Long ofYear) {
        this.name = name;
        this.ofYear = ofYear;
    }

    public ExpensesReportRequest(String name, Period reportFor, Long weekMonthQuarterValue, Long ofYear) {
        this.name = name;
        this.reportFor = reportFor;
        this.weekMonthQuarterValue = weekMonthQuarterValue;
        this.ofYear = ofYear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Period getReportFor() {
        return reportFor;
    }

    public void setReportFor(Period reportFor) {
        this.reportFor = reportFor;
    }

    public Long getWeekMonthQuarterValue() {
        return weekMonthQuarterValue;
    }

    public void setWeekMonthQuarterValue(Long weekMonthQuarterValue) {
        this.weekMonthQuarterValue = weekMonthQuarterValue;
    }

    public Long getOfYear() {
        return ofYear;
    }

    public void setOfYear(Long ofYear) {
        this.ofYear = ofYear;
    }

    public String getMessage() {
        return switch (reportFor) {
            case YEAR -> " for year " + ofYear;
            case MONTH -> " for month " + weekMonthQuarterValue + " in year " + ofYear;
            case WEEK -> " for week " + weekMonthQuarterValue + " in year " + ofYear;
            case QUARTER -> " for quarter " + weekMonthQuarterValue + " in year " + ofYear;
            default -> throw new UserActionNeededException("Wrong condition, use: year, month, week or quarter!");
        };
    }
}

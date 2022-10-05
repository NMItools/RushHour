package com.internship.rushhour.domain.report.models.provider;

import com.internship.rushhour.domain.report.models.Report;

public class IncomeReport implements Report {

    String provider;
    Double january;
    Double february;
    Double march;
    Double q1;
    Double april;
    Double may;
    Double june;
    Double q2;
    Double july;
    Double august;
    Double september;
    Double q3;
    Double october;
    Double november;
    Double december;
    Double q4;
    Double yearTotal;

    public IncomeReport() {
    }

    public IncomeReport(String provider, Double january, Double february, Double march, Double q1, Double april, Double may, Double june, Double q2, Double july, Double august, Double september, Double q3, Double october, Double november, Double december, Double q4, Double yearTotal) {
        this.provider = provider;
        this.january = january;
        this.february = february;
        this.march = march;
        this.q1 = q1;
        this.april = april;
        this.may = may;
        this.june = june;
        this.q2 = q2;
        this.july = july;
        this.august = august;
        this.september = september;
        this.q3 = q3;
        this.october = october;
        this.november = november;
        this.december = december;
        this.q4 = q4;
        this.yearTotal = yearTotal;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Double getJanuary() {
        return january;
    }

    public void setJanuary(Double january) {
        this.january = january;
    }

    public Double getFebruary() {
        return february;
    }

    public void setFebruary(Double february) {
        this.february = february;
    }

    public Double getMarch() {
        return march;
    }

    public void setMarch(Double march) {
        this.march = march;
    }

    public Double getQ1() {
        return q1;
    }

    public void setQ1(Double q1) {
        this.q1 = q1;
    }

    public Double getApril() {
        return april;
    }

    public void setApril(Double april) {
        this.april = april;
    }

    public Double getMay() {
        return may;
    }

    public void setMay(Double may) {
        this.may = may;
    }

    public Double getJune() {
        return june;
    }

    public void setJune(Double june) {
        this.june = june;
    }

    public Double getQ2() {
        return q2;
    }

    public void setQ2(Double q2) {
        this.q2 = q2;
    }

    public Double getJuly() {
        return july;
    }

    public void setJuly(Double july) {
        this.july = july;
    }

    public Double getAugust() {
        return august;
    }

    public void setAugust(Double august) {
        this.august = august;
    }

    public Double getSeptember() {
        return september;
    }

    public void setSeptember(Double september) {
        this.september = september;
    }

    public Double getQ3() {
        return q3;
    }

    public void setQ3(Double q3) {
        this.q3 = q3;
    }

    public Double getOctober() {
        return october;
    }

    public void setOctober(Double october) {
        this.october = october;
    }

    public Double getNovember() {
        return november;
    }

    public void setNovember(Double november) {
        this.november = november;
    }

    public Double getDecember() {
        return december;
    }

    public void setDecember(Double december) {
        this.december = december;
    }

    public Double getQ4() {
        return q4;
    }

    public void setQ4(Double q4) {
        this.q4 = q4;
    }

    public Double getYearTotal() {
        return yearTotal;
    }

    public void setYearTotal(Double yearTotal) {
        this.yearTotal = yearTotal;
    }
}
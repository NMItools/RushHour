package com.internship.rushhour.domain.report.service;

import com.internship.rushhour.domain.report.models.client.ExpensesReport;
import com.internship.rushhour.domain.report.models.client.ExpensesReportRequest;
import com.internship.rushhour.domain.report.models.client.FavoriteActivitiesReport;
import com.internship.rushhour.domain.report.models.client.FavoriteProviderReport;
import com.internship.rushhour.domain.report.models.employee.availability.Availability;
import com.internship.rushhour.domain.report.models.employee.availability.AvailabilityPerWeek;
import com.internship.rushhour.domain.report.models.employee.availability.AvailabilityPerWeekRequest;
import com.internship.rushhour.domain.report.models.employee.availability.AvailabilityRequest;
import com.internship.rushhour.domain.report.models.employee.mostandleastbooked.MostBookedRequest;
import com.internship.rushhour.domain.report.models.employee.mostandleastbooked.MostBookedTime;
import com.internship.rushhour.domain.report.models.employee.topten.TopTenClients;
import com.internship.rushhour.domain.report.models.employee.topten.TopTenRequest;
import com.internship.rushhour.domain.report.models.provider.AvailabilityReport;
import com.internship.rushhour.domain.report.models.provider.IncomeReport;
import com.internship.rushhour.domain.report.models.provider.ProductivityReport;

import java.util.List;

public interface ReportService {

    // PROVIDER Reports

    List<ProductivityReport> getProductivityReport(int year);

    List<AvailabilityReport> getAvailabilityReport(int year, int weekNumber);

    List<IncomeReport> getIncomeReport(int year);

    // EMPLOYEE Reports

    List<TopTenClients> getTopTenClientsPerMoneyOrTimeByYear(String sortBy, TopTenRequest topTenRequest);

    List<MostBookedTime> getMostAndLeastBookedTime(MostBookedRequest mostBookedRequest);

    List<Availability> getAvailability(AvailabilityRequest availabilityRequest);

    List<AvailabilityPerWeek> getAvailabilityPerWeek(AvailabilityPerWeekRequest availabilityPerWeekRequest);

    //CLIENT Reports

    List<FavoriteProviderReport> getFavoriteProviders(String forLast);

    List<FavoriteActivitiesReport> getFavoriteActivities(String forLast);

    List<ExpensesReport> getExpensesReport(ExpensesReportRequest expensesReportRequest, String reportPer);


}


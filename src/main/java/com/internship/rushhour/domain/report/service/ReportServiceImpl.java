package com.internship.rushhour.domain.report.service;

import com.internship.rushhour.domain.activity.service.ActivityService;
import com.internship.rushhour.domain.client.entity.Client;
import com.internship.rushhour.domain.client.service.ClientService;
import com.internship.rushhour.domain.employee.entity.Employee;
import com.internship.rushhour.domain.employee.service.EmployeeService;
import com.internship.rushhour.domain.provider.entity.Provider;
import com.internship.rushhour.domain.provider.service.ProviderService;
import com.internship.rushhour.domain.report.models.client.*;
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
import com.internship.rushhour.domain.report.repository.ReportRepository;
import com.internship.rushhour.infrastructure.exceptions.NotValidData;
import com.internship.rushhour.infrastructure.exceptions.ResourceNotFoundException;
import com.internship.rushhour.infrastructure.exceptions.UserActionNeededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

import static java.util.Objects.nonNull;

@Service
public class ReportServiceImpl implements ReportService{

    private final ReportRepository reportRepository;
    private final EmployeeService employeeService;
    private final ClientService clientService;
    private final ProviderService providerService;
    private final ActivityService activityService;

    @Autowired
    public ReportServiceImpl(ReportRepository reportRepository, EmployeeService employeeService,
                             ClientService clientService, ProviderService providerService,
                             ActivityService activityService) {
        this.reportRepository = reportRepository;
        this.employeeService = employeeService;
        this.clientService = clientService;
        this.providerService = providerService;
        this.activityService = activityService;
    }

    // PROVIDER Reports

    @Override
    public List<ProductivityReport> getProductivityReport(int year) {
        return reportRepository.getProductivityReport(getProviderId(), year);
    }

    @Override
    public List<AvailabilityReport> getAvailabilityReport(int year, int weekNumber) {
        return reportRepository.getAvailabilityReport(getProviderId(), weekNumber);
    }

    @Override
    public List<IncomeReport> getIncomeReport(int year) {
        return reportRepository.getIncomeReport(getProviderId(), year);
    }

    private Long getProviderId(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long providerId = employeeService.findByAccountEmail(email).orElseThrow(() ->
                new ResourceNotFoundException(email, "Employee", Employee.class.getSimpleName())).getProvider().getId();
        if(!nonNull(providerId)) throw new ResourceNotFoundException(providerId, "Provider", Provider.class.getSimpleName());
        return providerId;
    }

    public static LocalDate getLocalDate(int weekNumber, DayOfWeek dayOfWeek, int year) {
        return LocalDate.of(year, LocalDateTime.now().getMonth(), LocalDateTime.now().getDayOfMonth())
                .with(dayOfWeek)
                .with(WeekFields.of(Locale.UK).weekOfWeekBasedYear(), weekNumber);
    }

    // EMPLOYEE Reports

    @Override
    public List<TopTenClients> getTopTenClientsPerMoneyOrTimeByYear(String sortBy, TopTenRequest topTenRequest) {
        return reportRepository.getTopTenClientsPerMoneyOrTimeByYear(getCurrentEmployeeId(),
                sortBy(sortBy),
                topTenRequest,
                addToQuery(topTenRequest));
    }

    @Override
    public List<MostBookedTime> getMostAndLeastBookedTime(MostBookedRequest mostBookedRequest) {
        return reportRepository.getMostAndLeastBookedTime(getCurrentEmployeeId(),
                weekOrMonth(mostBookedRequest.getPerWeekOrMonth()),
                addNumToQuery(mostBookedRequest),
                mostBookedRequest.getYear());
    }

    @Override
    public List<Availability> getAvailability(AvailabilityRequest availabilityRequest) {
        return reportRepository.getAvailability(getCurrentEmployeeId(), availabilityRequest);
    }

    @Override
    public List<AvailabilityPerWeek> getAvailabilityPerWeek(AvailabilityPerWeekRequest availabilityPerWeekRequest) {
        return reportRepository.getAvailabilityPerWeek(getCurrentEmployeeId(), availabilityPerWeekRequest);
    }

    private Long getCurrentEmployeeId(){
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee currentEmployee = employeeService.findByEmail(currentEmail);

        return currentEmployee.getId();
    }

    private String sortBy(String money_time) {
        if(money_time.equals("money")) {
            return " \nORDER BY SUM(appointment.price) desc \n ";
        }
        if (money_time.equals("time")) {
            return "\n ORDER BY SUM(TIMESTAMPDIFF(MINUTE, start_time, end_date)/60) DESC \n ";
        }
        throw new NotFoundException("You need to choose between money and time.");
    }

    private String weekOrMonth(String weekOrMonth) {
        if(weekOrMonth.equals("week")){
            return "week(start_time) = ";
        }
        if (weekOrMonth.equals(("month"))) {
            return "month(start_time) = ";
        }
        throw new NotValidData("You need to choose between week and month.");
    }

    private String addToQuery(TopTenRequest topTenRequest) {

        if ((topTenRequest.getMonth() == null) && (topTenRequest.getQuarter() == null))
            return " ";

        if (topTenRequest.getQuarter() == null)
            return " AND MONTH(start_time) = " + topTenRequest.getMonth();

        if (topTenRequest.getMonth() == null)
            return " AND QUARTER(start_time) = " + topTenRequest.getQuarter();

        throw new NotValidData(" You must choose between month or quarter.");
    }

    private String addNumToQuery(MostBookedRequest mostBookedRequest) {

        if ((mostBookedRequest.getMonth() == null) && (mostBookedRequest.getWeek() == null))
            throw new NotValidData(" You must insert a number for week or month.");

        if (mostBookedRequest.getMonth() == null)
            return mostBookedRequest.getWeek();

        if (mostBookedRequest.getWeek() == null)
            return mostBookedRequest.getMonth();

        throw new NotValidData("You must choose between week or month.");
    }

    //CLIENT Report

    @Override
    public List<FavoriteProviderReport> getFavoriteProviders(String forLast) {
        return  reportRepository.getFavoriteProviders(getCurrentClient(), sqlForLast(forLast));
    }

    @Override
    public List<FavoriteActivitiesReport> getFavoriteActivities(String forLast) {
        return reportRepository.getFavoriteActivities(getCurrentClient(), sqlForLast(forLast));
    }

    @Override
    public List<ExpensesReport> getExpensesReport(ExpensesReportRequest reportRequest,
                                                  String reportPer) {
        if("provider".equals(reportPer)) {

            if(!providerService.existsByName(reportRequest.getName()))
                throw new ResourceNotFoundException
                        (reportRequest.getName(),"Provider ", "provider name ");

            String sql = providerSql(reportRequest.getName(),
                    sqlForMonthWeekQuarter(reportRequest.getReportFor(), reportRequest.getWeekMonthQuarterValue()));

            return reportRepository.getExpensesReports(getCurrentClient(),
                    reportRequest.getOfYear(),
                    sql);
        }
        if("activity".equals(reportPer)) {

            if(!activityService.existsByName(reportRequest.getName()))
                throw new ResourceNotFoundException
                        (reportRequest.getName(), "Activity ", "activity name ");

            String sql = activitySql(reportRequest.getName(),
                    sqlForMonthWeekQuarter(reportRequest.getReportFor(), reportRequest.getWeekMonthQuarterValue()));

            return reportRepository.getExpensesReports(getCurrentClient(),
                    reportRequest.getOfYear(),
                    sql);
        }
        throw new UserActionNeededException("Wrong endpoint, use: provider or activity!");
    }

    private Client getCurrentClient(){
        String clientName = SecurityContextHolder.getContext().getAuthentication().getName();
        return clientService.getByAccountEmail(clientName);
    }

    private String sqlForLast(String monthQuarterYear) {
        return switch (monthQuarterYear){
            case "year" -> "AND YEAR(CURDATE())-1 = YEAR(start_time) ";
            case "month" -> "AND MONTH(CURDATE())-1 = MONTH(start_time) ";
            case "quarter" -> "AND QUARTER(CURDATE())-1 = QUARTER(start_time) ";
            default -> throw new UserActionNeededException("Wrong endpoint, use: month, quarter or year!");
        };
    }

    private String providerSql(String providerName, String sqlWeekMonthQuarter){
        return """
                SELECT 
                provider.name AS "providerName",
                SUM(appointment.price) AS "totalCost"
                FROM appointment
                INNER JOIN employee ON appointment.employee = employee.id
                INNER JOIN provider ON employee.provider = provider.id
                WHERE appointment.client = ?1 AND year(start_time) = ?2 
                """ +
                "AND provider.name = \"" + providerName +"\"" +
                sqlWeekMonthQuarter+
                " GROUP BY provider.name";
    }

    private String activitySql(String activityName, String sqlWeekMonthQuarter){
        return """
                SELECT 
                activity.name AS "providerName",
                SUM(appointment.price) AS "totalCost"
                FROM appointment_activities
                INNER JOIN appointment ON appointment_activities.appointment_id = appointment.id
                INNER JOIN activity ON appointment_activities.activity_id = activity.id
                INNER JOIN provider ON activity.provider = provider.id
                WHERE appointment.client = ?1 AND YEAR(start_time) = ?2 
                """ +
                "AND activity.name = \"" + activityName +"\"" +
                sqlWeekMonthQuarter+
                " GROUP BY activity.name";
    }

    private String sqlForMonthWeekQuarter(Period weekMonthQuarter, Long whichMonthWeekQuarter) {
        return switch (weekMonthQuarter){
            case MONTH -> " AND MONTH(start_time) = " + whichMonthWeekQuarter;
            case WEEK -> " AND WEEKOFYEAR(start_time) = " + whichMonthWeekQuarter;
            case QUARTER -> " AND QUARTER(start_time) = " + whichMonthWeekQuarter;
            default -> " ";
        };
    }



}
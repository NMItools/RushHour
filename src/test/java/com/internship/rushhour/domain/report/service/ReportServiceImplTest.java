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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.webjars.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    ReportRepository reportRepository;

    @Mock
    EmployeeService employeeService;

    @InjectMocks
    ReportServiceImpl reportService;

    @Mock
    Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    List<Availability> availabilities;
    TopTenClients topTenClients;

    private List<MostBookedTime> mostBookedTimes;
    private MostBookedRequest mostBookedRequest;

    String weekOrMonth;

    AvailabilityRequest availabilityRequest;
    AvailabilityPerWeek availabilityPerWeek;
    AvailabilityPerWeekRequest availabilityPerWeekRequest;

    List<AvailabilityPerWeek> availabilityPerWeeks;

    @Mock
    ClientService clientService;

    @Mock
    ProviderService providerService;

    @Mock
    ActivityService activityService;


    Provider provider;
    Employee employee;
    Client client;
    TopTenRequest topTenRequest;


    List<TopTenClients> topTenClientsList;

    List<ProductivityReport> productivityReport;
    List<AvailabilityReport> availabilityReport;
    List<IncomeReport> incomeReport;
    List<FavoriteActivitiesReport> favoriteActivitiesReports;
    List<FavoriteProviderReport> favoriteProviderReports;
    List<ExpensesReport> expensesReports;
    ExpensesReportRequest reportRequest;
    ExpensesReportRequest expensesReportRequest;
    ExpensesReportRequest requestActivity;

    @BeforeEach
    void setUp(){
        SecurityContextHolder.getContext().setAuthentication(authentication);

        provider = new Provider();
        provider.setId(1L);

        employee = new Employee();
        employee.setId(1L);
        employee.setProvider(provider);


        ProductivityReport productivityReport1 = new ProductivityReport("test employee", 1L, 20.00, 100.00);
        productivityReport = new ArrayList<>();
        availabilityReport = new ArrayList<>();
        productivityReport.add(productivityReport1);

        topTenClients = new TopTenClients();
        topTenClientsList = new ArrayList<>();
        topTenClientsList.add(topTenClients);

        topTenRequest = new TopTenRequest();
        topTenRequest.setYear("2022");
        weekOrMonth = "week";

        mostBookedTimes = new ArrayList<>();
        mostBookedRequest = new MostBookedRequest();

        availabilityRequest = new AvailabilityRequest();
        availabilities = new ArrayList<>();

        availabilityPerWeek = new AvailabilityPerWeek();
        availabilityPerWeeks = new ArrayList<>();

        client = new Client();

        FavoriteActivitiesReport favoriteActivitiesReport =
                new FavoriteActivitiesReport("TestProviderName",
                        "testActivityName", 1L);
        favoriteActivitiesReports = new ArrayList<>();
        favoriteActivitiesReports.add(favoriteActivitiesReport);

        FavoriteProviderReport favoriteProviderReport =
                new FavoriteProviderReport("TestProvider", 20D, 1L);
        favoriteProviderReports = new ArrayList<>();
        favoriteProviderReports.add(favoriteProviderReport);

        ExpensesReport expensesReport = new ExpensesReport("test", 200D);
        expensesReports = new ArrayList<>();
        expensesReports.add(expensesReport);

        reportRequest = new ExpensesReportRequest
                ("Nail Studio", Period.YEAR, 0L, 2022L);
        expensesReportRequest = new ExpensesReportRequest
                ("Nail Studio", Period.MONTH, 6L, 2022L);
        requestActivity = new ExpensesReportRequest
                ("Haircut", Period.MONTH, 6L, 2022L);

    }

    //PROVIDER Reports

    @Test
    void getProductivityReport() {
        when(employeeService.findByAccountEmail(SecurityContextHolder.getContext().getAuthentication().getName())).thenReturn(Optional.of(employee));
        when(reportRepository.getProductivityReport(1L, 2022)).thenReturn(productivityReport);

        List<ProductivityReport> productivityReportList = reportService.getProductivityReport(2022);

        assertThat(productivityReport).isEqualTo(productivityReportList);
    }

    @Test
    void getAvailabilityReport() {
        when(employeeService.findByAccountEmail(SecurityContextHolder.getContext().getAuthentication().getName())).thenReturn(Optional.of(employee));
        when(reportRepository.getAvailabilityReport(1L, 25)).thenReturn(availabilityReport);

        List<AvailabilityReport> availabilityReportList = reportService.getAvailabilityReport(2022, 25);

        assertThat(availabilityReport).isEqualTo(availabilityReportList);
    }

    @Test
    void getIncomeReport() {
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("testuser");
        when(employeeService.findByAccountEmail("testuser")).thenReturn(Optional.of(employee));
        when(reportRepository.getIncomeReport(1L, 2022)).thenReturn(incomeReport);

        List<IncomeReport> incomeReportList = reportService.getIncomeReport(2022);

        assertThat(incomeReport).isEqualTo(incomeReportList);
    }

    //EMPLOYEE Reports

    @Test
    void shouldGetTopTenClientsPerMoneyOrTimeByYear() {

        topTenRequest.setQuarter("");

        when(reportRepository.getTopTenClientsPerMoneyOrTimeByYear(1L, " \nORDER BY SUM(appointment.price) desc \n ", topTenRequest, " AND QUARTER(start_time) = "))
                .thenReturn(topTenClientsList);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("name@mail.com");
        when(employeeService.findByEmail("name@mail.com")).thenReturn(employee);

        assertThat(reportService.getTopTenClientsPerMoneyOrTimeByYear("money", topTenRequest)).isEqualTo(topTenClientsList);
    }

    @Test
    void shouldGetTopTenClientsPerMoneyOrTimeByYear2() {

        topTenRequest.setMonth("");

        when(reportRepository.getTopTenClientsPerMoneyOrTimeByYear(1L, "\n ORDER BY SUM(TIMESTAMPDIFF(MINUTE, start_time, end_date)/60) DESC \n ", topTenRequest, " AND MONTH(start_time) = "))
                .thenReturn(topTenClientsList);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("name@mail.com");
        when(employeeService.findByEmail("name@mail.com")).thenReturn(employee);

        assertThat(reportService.getTopTenClientsPerMoneyOrTimeByYear("time", topTenRequest)).isEqualTo(topTenClientsList);
    }

    @Test
    void shouldGetTopTenClientsPerMoneyOrTimeByYear3() {

        when(reportRepository.getTopTenClientsPerMoneyOrTimeByYear(1L, "\n ORDER BY SUM(TIMESTAMPDIFF(MINUTE, start_time, end_date)/60) DESC \n ", topTenRequest, " "))
                .thenReturn(topTenClientsList);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("name@mail.com");
        when(employeeService.findByEmail("name@mail.com")).thenReturn(employee);

        assertThat(reportService.getTopTenClientsPerMoneyOrTimeByYear("time", topTenRequest)).isEqualTo(topTenClientsList);
    }

    @Test
    void shouldGetTopTenClientsPerMoneyOrTimeByYearEx() {

        topTenRequest.setMonth("");
        topTenRequest.setQuarter("");
        weekOrMonth = "";

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("name@mail.com");
        when(employeeService.findByEmail("name@mail.com")).thenReturn(employee);

        assertThatThrownBy(() -> reportService.getTopTenClientsPerMoneyOrTimeByYear("\n ORDER BY SUM(TIMESTAMPDIFF(MINUTE, start_time, end_date)/60) DESC \n ", topTenRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldGetMostAndLeastBookedTimeEx() {

        mostBookedRequest.setPerWeekOrMonth("week");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("name@mail.com");
        when(employeeService.findByEmail("name@mail.com")).thenReturn(employee);

        assertThatThrownBy(() -> reportService.getMostAndLeastBookedTime(mostBookedRequest))
                .isInstanceOf(NotValidData.class);
    }

    @Test
    void shouldGetMostAndLeastBookedTimeEx2() {

        mostBookedRequest.setPerWeekOrMonth("month");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("name@mail.com");
        when(employeeService.findByEmail("name@mail.com")).thenReturn(employee);

        assertThatThrownBy(() -> reportService.getMostAndLeastBookedTime(mostBookedRequest))
                .isInstanceOf(NotValidData.class);
    }

    @Test
    void shouldGetMostAndLeastBookedTimeEx3() {

        mostBookedRequest.setPerWeekOrMonth("notValid");
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("name@mail.com");
        when(employeeService.findByEmail("name@mail.com")).thenReturn(employee);

        assertThatThrownBy(() -> reportService.getMostAndLeastBookedTime(mostBookedRequest))
                .isInstanceOf(NotValidData.class);
    }

    @Test
    void shouldGetMostAndLeastBookedTimeEx4() {

        mostBookedRequest.setPerWeekOrMonth("week");
        mostBookedRequest.setWeek("1");
        mostBookedRequest.setYear("2022");
        mostBookedRequest.setMonth("1");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("name@mail.com");
        when(employeeService.findByEmail("name@mail.com")).thenReturn(employee);

        assertThatThrownBy(() -> reportService.getMostAndLeastBookedTime(mostBookedRequest))
                .isInstanceOf(NotValidData.class);
    }

    @Test
    void shouldGetAvailability() {

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("name@mail.com");
        when(employeeService.findByEmail("name@mail.com")).thenReturn(employee);
        when(reportRepository.getAvailability(1L, availabilityRequest )).thenReturn(availabilities);

        assertThat(reportService.getAvailability(availabilityRequest)).isEqualTo(availabilities);
    }

    @Test
    void shouldGetWeeklyAvailability() {

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("name@mail.com");
        when(employeeService.findByEmail("name@mail.com")).thenReturn(employee);
        when(reportRepository.getAvailabilityPerWeek(1L, availabilityPerWeekRequest )).thenReturn(availabilityPerWeeks);

        assertThat(reportService.getAvailabilityPerWeek(availabilityPerWeekRequest)).isEqualTo(availabilityPerWeeks);
    }


    //CLIENT Reports

    @Test
    void getFavoriteProviders() {
        when(clientService.getByAccountEmail(SecurityContextHolder.getContext().getAuthentication().getName()))
                .thenReturn(client);
        when(reportRepository.getFavoriteProviders(client, "AND MONTH(CURDATE())-1 = MONTH(start_time) "))
                .thenReturn(favoriteProviderReports);

        List<FavoriteProviderReport> reports = reportService.getFavoriteProviders("month");

        assertThat(reports).isEqualTo(favoriteProviderReports);
    }

    @Test
    void getFavoriteActivities() {
        when(clientService.getByAccountEmail(SecurityContextHolder.getContext().getAuthentication().getName()))
                .thenReturn(client);
        when(reportRepository.getFavoriteActivities(client, "AND MONTH(CURDATE())-1 = MONTH(start_time) "))
                .thenReturn(favoriteActivitiesReports);

        List<FavoriteActivitiesReport> reports = reportService.getFavoriteActivities("month");

        assertThat(reports).isEqualTo(favoriteActivitiesReports);
    }

    @Test
    void getFavoriteActivitiesByYear() {
        when(clientService.getByAccountEmail(SecurityContextHolder.getContext().getAuthentication().getName()))
                .thenReturn(client);
        when(reportRepository.getFavoriteActivities(client, "AND YEAR(CURDATE())-1 = YEAR(start_time) "))
                .thenReturn(favoriteActivitiesReports);

        List<FavoriteActivitiesReport> reports = reportService.getFavoriteActivities("year");

        assertThat(reports).isEqualTo(favoriteActivitiesReports);
    }

    @Test
    void getFavoriteActivitiesByQuarter() {
        when(clientService.getByAccountEmail(SecurityContextHolder.getContext().getAuthentication().getName()))
                .thenReturn(client);
        when(reportRepository.getFavoriteActivities(client, "AND QUARTER(CURDATE())-1 = QUARTER(start_time) "))
                .thenReturn(favoriteActivitiesReports);

        List<FavoriteActivitiesReport> reports = reportService.getFavoriteActivities("quarter");

        assertThat(reports).isEqualTo(favoriteActivitiesReports);
    }

    @Test
    void getFavoriteActivitiesWhenWrongEndpoint() {
        assertThatThrownBy(() -> reportService.getFavoriteActivities("test"))
                .hasMessageContaining("Wrong endpoint, use: month, quarter or year!")
                .isInstanceOf(UserActionNeededException.class);
    }

    @Test
    void getExpensesReport() {
        when(clientService.getByAccountEmail(SecurityContextHolder.getContext().getAuthentication().getName()))
                .thenReturn(client);
        when(providerService.existsByName("Nail Studio")).thenReturn(true);
        when(reportRepository.getExpensesReports(client, 2022L, "SELECT\n" +
                "provider.name AS \"providerName\",\n" +
                "SUM(appointment.price) AS \"totalCost\"\n" +
                "FROM appointment\n" +
                "INNER JOIN employee ON appointment.employee = employee.id\n" +
                "INNER JOIN provider ON employee.provider = provider.id\n" +
                "WHERE appointment.client = ?1 AND year(start_time) = ?2\n" +
                "AND provider.name = \"Nail Studio\" AND MONTH(start_time) = 6 GROUP BY provider.name")).thenReturn(expensesReports);

        List<ExpensesReport> reports = reportService.getExpensesReport(expensesReportRequest,"provider");

        assertThat(reports).isEqualTo(expensesReports);
    }

    @Test
    void getExpensesReportByWeek() {
        when(clientService.getByAccountEmail(SecurityContextHolder.getContext().getAuthentication().getName()))
                .thenReturn(client);
        expensesReportRequest.setReportFor(Period.WEEK);
        when(providerService.existsByName("Nail Studio")).thenReturn(true);
        when(reportRepository.getExpensesReports(client, 2022L, "SELECT\n" +
                "provider.name AS \"providerName\",\n" +
                "SUM(appointment.price) AS \"totalCost\"\n" +
                "FROM appointment\n" +
                "INNER JOIN employee ON appointment.employee = employee.id\n" +
                "INNER JOIN provider ON employee.provider = provider.id\n" +
                "WHERE appointment.client = ?1 AND year(start_time) = ?2\n" +
                "AND provider.name = \"Nail Studio\" AND WEEKOFYEAR(start_time) = 6 GROUP BY provider.name"))
                .thenReturn(expensesReports);

        List<ExpensesReport> reports = reportService.getExpensesReport(expensesReportRequest,"provider");

        assertThat(reports).isEqualTo(expensesReports);
    }

    @Test
    void getExpensesReportByQuarter() {
        when(clientService.getByAccountEmail(SecurityContextHolder.getContext().getAuthentication().getName()))
                .thenReturn(client);
        expensesReportRequest.setReportFor(Period.QUARTER);
        when(providerService.existsByName("Nail Studio")).thenReturn(true);
        when(reportRepository.getExpensesReports(client, 2022L, "SELECT\n" +
                "provider.name AS \"providerName\",\n" +
                "SUM(appointment.price) AS \"totalCost\"\n" +
                "FROM appointment\n" +
                "INNER JOIN employee ON appointment.employee = employee.id\n" +
                "INNER JOIN provider ON employee.provider = provider.id\n" +
                "WHERE appointment.client = ?1 AND year(start_time) = ?2\n" +
                "AND provider.name = \"Nail Studio\" AND QUARTER(start_time) = 6 GROUP BY provider.name"))
                .thenReturn(expensesReports);

        List<ExpensesReport> reports = reportService.getExpensesReport(expensesReportRequest,"provider");

        assertThat(reports).isEqualTo(expensesReports);
    }

    @Test
    void getExpensesReportByYear() {
        when(clientService.getByAccountEmail(SecurityContextHolder.getContext().getAuthentication().getName()))
                .thenReturn(client);
        when(providerService.existsByName("Nail Studio")).thenReturn(true);
        when(reportRepository.getExpensesReports(client, 2022L, "SELECT\n" +
                "provider.name AS \"providerName\",\n" +
                "SUM(appointment.price) AS \"totalCost\"\n" +
                "FROM appointment\n" +
                "INNER JOIN employee ON appointment.employee = employee.id\n" +
                "INNER JOIN provider ON employee.provider = provider.id\n" +
                "WHERE appointment.client = ?1 AND year(start_time) = ?2\n" +
                "AND provider.name = \"Nail Studio\"  GROUP BY provider.name"))
                .thenReturn(expensesReports);

        List<ExpensesReport> reports = reportService.getExpensesReport(reportRequest,"provider");

        assertThat(reports).isEqualTo(expensesReports);
    }

    @Test
    void getExpensesReportWhenProviderNotFound(){
        when(providerService.existsByName("Nail Studio")).thenReturn(false);

        assertThatThrownBy(() -> reportService.getExpensesReport(reportRequest,"provider"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getExpensesReportWhenWrongEndpoint(){
        assertThatThrownBy(() -> reportService.getExpensesReport(reportRequest,"test"))
                .isInstanceOf(UserActionNeededException.class);
    }

    @Test
    void getExpensesReportPerActivity() {
        when(clientService.getByAccountEmail(SecurityContextHolder.getContext().getAuthentication().getName()))
                .thenReturn(client);
        when(activityService.existsByName("Haircut")).thenReturn(true);
        when(reportRepository.getExpensesReports(client, 2022L, "SELECT\n" +
                "activity.name AS \"providerName\",\n" +
                "SUM(appointment.price) AS \"totalCost\"\n" +
                "FROM appointment_activities\n" +
                "INNER JOIN appointment ON appointment_activities.appointment_id = appointment.id\n" +
                "INNER JOIN activity ON appointment_activities.activity_id = activity.id\n" +
                "INNER JOIN provider ON activity.provider = provider.id\n" +
                "WHERE appointment.client = ?1 AND YEAR(start_time) = ?2\n" +
                "AND activity.name = \"Haircut\" AND MONTH(start_time) = 6 GROUP BY activity.name"))
                .thenReturn(expensesReports);

        List<ExpensesReport> reports = reportService.getExpensesReport(requestActivity,"activity");

        assertThat(reports).isEqualTo(expensesReports);
    }

    @Test
    void getExpensesReportWhenActivityNotFound(){
        when(activityService.existsByName("Haircut")).thenReturn(false);

        assertThatThrownBy(() -> reportService.getExpensesReport(requestActivity,"activity"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

}
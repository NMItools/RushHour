package com.internship.rushhour.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import com.internship.rushhour.domain.report.service.ReportService;
import com.internship.rushhour.infrastructure.exceptions.ControllerAdvisor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    private MockMvc mvc;

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    List<ProductivityReport> productivityReport;
    List<AvailabilityReport> availabilityReport;
    List<IncomeReport> incomeReport;
    ClientReportResponse clientReport;
    List<FavoriteProviderReport> reportList;
    List<FavoriteActivitiesReport> activitiesReports;
    List<ExpensesReport> expensesReports;

    TopTenRequest topTenRequest;
    MostBookedRequest mostBookedRequest;
    AvailabilityRequest availabilityRequest;
    AvailabilityPerWeekRequest availabilityPerWeekRequest;
    List<TopTenClients> topTenClients;
    List<MostBookedTime> mostBookedTime;
    List<Availability> availability;
    List<AvailabilityPerWeek> availabilityPerWeek;


    private JacksonTester<List<ProductivityReport>> jacksonTesterProductivity;
    private JacksonTester<List<AvailabilityReport>> jacksonTesterAvailability;
    private JacksonTester<List<IncomeReport>> jacksonTesterIncome;


    @BeforeEach
    void setUp(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        JacksonTester.initFields(this, objectMapper);

        ProductivityReport productivityReport1 = new ProductivityReport(
                "test employee",
                1L,
                20.00,
                100.00);
        productivityReport = new ArrayList<>();
        productivityReport.add(productivityReport1);

        AvailabilityReport availabilityReport1 = new AvailabilityReport(
                "test employee",
                "08:00",
                1L, 1L,0L,1L,0L,0L,0L);
        availabilityReport = new ArrayList<>();
        availabilityReport.add(availabilityReport1);

        IncomeReport incomeReport1 = new IncomeReport(
                "NmiTools",
                100.00, 100.00, 100.00, 300.00,
                100.00, 100.00, 100.00, 300.00,
                100.00, 100.00, 100.00, 300.00,
                100.00, 100.00, 100.00, 300.00,
                1200.00 );
        incomeReport = new ArrayList<>();
        incomeReport.add(incomeReport1);

        topTenRequest = new TopTenRequest();
        mostBookedRequest = new MostBookedRequest();
        availabilityRequest = new AvailabilityRequest();

        topTenClients = new ArrayList<>();
        mostBookedTime = new ArrayList<>();
        availability = new ArrayList<>();


        clientReport = new ClientReportResponse();

        FavoriteProviderReport favoriteProviderReport =
                new FavoriteProviderReport("name", 20D, 3L);
        reportList = new ArrayList<>();
        reportList.add(favoriteProviderReport);

        FavoriteActivitiesReport favoriteActivitiesReport =
                new FavoriteActivitiesReport("name", "test", 2L);
        activitiesReports = new ArrayList<>();
        activitiesReports.add(favoriteActivitiesReport);

        ExpensesReport expensesReport = new ExpensesReport("provider", 20D);
        expensesReports = new ArrayList<>();
        expensesReports.add(expensesReport);


        mvc = MockMvcBuilders.standaloneSetup(reportController)
                .setControllerAdvice(new ControllerAdvisor())
                .build();
    }

    @Test
    void canRetrieveProductivityReportByYearWhenExists() throws Exception {
        when(reportService.getProductivityReport(anyInt())).thenReturn(productivityReport);

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/report/provider/productivity/2022")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jacksonTesterProductivity.write(productivityReport).getJson());
    }

    @Test
    void canRetrieveAvailabilityReportByWeekWhenExists() throws Exception {
        when(reportService.getAvailabilityReport(anyInt(), anyInt())).thenReturn(availabilityReport);

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/report/provider/availability/2022/25")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jacksonTesterAvailability.write(availabilityReport).getJson());
    }
    @Test
    void canRetrieveIncomeReportByYearWhenExists() throws Exception {
        when(reportService.getIncomeReport(anyInt())).thenReturn(incomeReport);

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/report/provider/income/2022")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jacksonTesterIncome.write(incomeReport).getJson());
    }

    @Test
    void getHighestSpendingCustomers() throws Exception {
        when(reportService.getTopTenClientsPerMoneyOrTimeByYear("money", topTenRequest)).thenReturn(topTenClients);

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                .get("/report/highest-spending-customers/money")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        ResponseEntity<List<TopTenClients>> responseEntity = reportController.getHighestSpendingCustomers(topTenRequest, "money");
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);

    }

    @Test
    void getMostAndLeastBookedTime() {
        when(reportService.getMostAndLeastBookedTime(mostBookedRequest)).thenReturn(mostBookedTime);
        ResponseEntity<List<MostBookedTime>> responseEntity = reportController.getMostAndLeastBookedTime(mostBookedRequest);
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void getAvailability() {
        when(reportService.getAvailability(availabilityRequest)).thenReturn(availability);
        ResponseEntity<List<Availability>> responseEntity = reportController.getDailyAvailability(availabilityRequest);
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void getAvailabilityPerWeek() {
        when(reportService.getAvailabilityPerWeek(availabilityPerWeekRequest)).thenReturn(availabilityPerWeek);
        ResponseEntity<List<AvailabilityPerWeek>> responseEntity = reportController.getWeeklyAvailability(availabilityPerWeekRequest);
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
    }


    @Test
    void getFavoriteProvidersReport() throws Exception {
        when(reportService.getFavoriteProviders("year")).thenReturn(reportList);

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                .get("/report/clients/favorite/providers-activities/provider/year")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void getFavoriteActivitiesReport() throws Exception {
        when(reportService.getFavoriteActivities("year")).thenReturn(activitiesReports);

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                .get("/report/clients/favorite/providers-activities/activity/year")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void getFavoriteProviderActivityWhenWrongEndpoint() throws Exception {
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                .get("/report/clients/favorite/providers-activities/activit/year")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(400);
    }

    @Test
    void getExpensesReport() throws Exception {
        ExpensesReportRequest request = new ExpensesReportRequest
                ("providerName",Period.YEAR,0L, 2022L);

        when(reportService.getExpensesReport(request, "provider")).thenReturn(expensesReports);

        ResponseEntity<ClientReportResponse> response = reportController.getExpensesReport(request,"provider");

        assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
    }

}
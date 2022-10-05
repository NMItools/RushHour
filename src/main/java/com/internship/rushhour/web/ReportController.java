package com.internship.rushhour.web;

import com.internship.rushhour.domain.report.models.client.ClientReportResponse;
import com.internship.rushhour.domain.report.models.client.ExpensesReport;
import com.internship.rushhour.domain.report.models.client.ExpensesReportRequest;
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
import com.internship.rushhour.infrastructure.exceptions.UserActionNeededException;
import com.internship.rushhour.infrastructure.export.ExportService;
import com.internship.rushhour.infrastructure.export.FileType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;
    private final ExportService exportService;

    @Autowired
    public ReportController(ReportService reportService, ExportService exportService) {
        this.reportService = reportService;
        this.exportService = exportService;
    }

    // PROVIDER Reports

    @Operation(summary = "Get productivity report for all provider employee's by year")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Provider productivity report by year",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductivityReport.class)) }),
            @ApiResponse(responseCode = "404", description = "Report for a given year not found",
                    content = @Content) })
    @GetMapping(path = "/provider/productivity/{year}")
    @PreAuthorize("hasRole('ROLE_PROVIDER_ADMINISTRATOR')")
    public ResponseEntity<List<ProductivityReport>> getProductivityReport(@PathVariable Integer year){
        return ResponseEntity.ok(reportService.getProductivityReport(year));
    }

    @Operation(summary = "Get availability report for all provider employee's by number of week")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Provider availability report for a week",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AvailabilityReport.class)) }),
            @ApiResponse(responseCode = "404", description = "Report for a given week not found",
                    content = @Content) })
    @GetMapping(path = "/provider/availability/{year}/{weekNumber}")
    @PreAuthorize("hasRole('ROLE_PROVIDER_ADMINISTRATOR')")
    public ResponseEntity<List<AvailabilityReport>> getAvailabilityReport(@PathVariable Integer year, @PathVariable Integer weekNumber){
        return ResponseEntity.ok(reportService.getAvailabilityReport(year, weekNumber));
    }

    @Operation(summary = "Get provider income report per month/quarter/year totals")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Provider income report for a year",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IncomeReport.class)) }),
            @ApiResponse(responseCode = "404", description = "Report for a given year not found",
                    content = @Content) })
    @GetMapping(path = "/provider/income/{year}")
    @PreAuthorize("hasRole('ROLE_PROVIDER_ADMINISTRATOR')")
    public ResponseEntity<List<IncomeReport>> getIncomeReport(@PathVariable Integer year){
        return ResponseEntity.ok(reportService.getIncomeReport(year));
    }

    // PROVIDER exports

    @GetMapping(path = "/provider/productivity/{year}/export/{fileType}", produces={"application/vnd.ms-excel", "text/csv", "application/pdf"})
    @PreAuthorize("hasRole('ROLE_PROVIDER_ADMINISTRATOR')")
    public ResponseEntity<InputStreamResource> exportProductivityReport(@PathVariable int year, @PathVariable FileType fileType) throws ClassNotFoundException, IllegalAccessException {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=provider_productivity_" + year + ".xlsx")
                .body(exportService.exportProductivityReport(year, fileType));
    }

    @GetMapping(path = "/provider/availability/{year}/{weekNumber}/export/{fileType}", produces={"application/vnd.ms-excel", "text/csv", "application/pdf"})
    @PreAuthorize("hasRole('ROLE_PROVIDER_ADMINISTRATOR')")
    public ResponseEntity<InputStreamResource> exportAvailabilityReport(@PathVariable int year, @PathVariable Integer weekNumber, @PathVariable FileType fileType) throws ClassNotFoundException, IllegalAccessException {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=provider_availability_" + year + "_"+weekNumber+".xlsx")
                .body(exportService.exportAvailabilityReport(year, weekNumber, fileType));
    }

    @GetMapping(path = "/provider/income/{year}/export/{fileType}", produces={"application/vnd.ms-excel", "text/csv", "application/pdf"})
    @PreAuthorize("hasRole('ROLE_PROVIDER_ADMINISTRATOR')")
    public ResponseEntity<InputStreamResource> exportIncomeReport(@PathVariable int year, @PathVariable FileType fileType) throws ClassNotFoundException, IllegalAccessException {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=provider_income_" + year + ".xlsx")
                .body(exportService.exportIncomeReport(year, fileType));
    }

    // EMPLOYEE Reports

    @GetMapping(path = "/employee/highest-spending-customers/{sortBy}")
    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    public ResponseEntity<List<TopTenClients>> getHighestSpendingCustomers(@Valid @RequestBody TopTenRequest topTenRequest,
                                                                           @PathVariable String sortBy) {
        return ResponseEntity.ok(reportService.getTopTenClientsPerMoneyOrTimeByYear(sortBy, topTenRequest));
    }

    @GetMapping(path = "/employee/most-least-booked-time")
    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    public ResponseEntity<List<MostBookedTime>> getMostAndLeastBookedTime(@Valid @RequestBody MostBookedRequest mostBookedRequest) {
        return ResponseEntity.ok(reportService.getMostAndLeastBookedTime(mostBookedRequest));
    }

    @GetMapping(path = "/employee/availability")
    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    public ResponseEntity<List<Availability>> getDailyAvailability(@Valid @RequestBody AvailabilityRequest availabilityRequest) {
        return ResponseEntity.ok(reportService.getAvailability(availabilityRequest));
    }

    @GetMapping(path = "/employee/weekly-availability")
    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    public ResponseEntity<List<AvailabilityPerWeek>> getWeeklyAvailability(@Valid @RequestBody AvailabilityPerWeekRequest availabilityPerWeekRequest) {
        return ResponseEntity.ok(reportService.getAvailabilityPerWeek(availabilityPerWeekRequest));
    }


    //CLIENT

    @GetMapping(path = "/clients/favorite/providers-activities/{provider_activity}/{forLast}")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ResponseEntity<ClientReportResponse> getFavoriteProvidersReport(@PathVariable String provider_activity,
                                                                           @PathVariable String forLast) throws Exception {
        if("provider".equals(provider_activity))
        return ResponseEntity.ok(new ClientReportResponse("Top 3 favorite providers for last " + forLast,
                reportService.getFavoriteProviders(forLast)));

        if("activity".equals(provider_activity))
            return ResponseEntity.ok(new ClientReportResponse("Top 5 favorite activities for last " + forLast,
                    reportService.getFavoriteActivities(forLast)));

        throw new UserActionNeededException("Wrong endpoint, use: activity or provider");

    }

    @GetMapping(path = "/clients/expenses/{reportPer}")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ResponseEntity<ClientReportResponse> getExpensesReport(@Valid @RequestBody ExpensesReportRequest reportRequest,
                                                                  @PathVariable String reportPer) throws Exception {

        List<ExpensesReport> expensesReport = reportService.getExpensesReport(reportRequest, reportPer);

        return ResponseEntity.ok(new ClientReportResponse("Expenses report per " + reportPer +reportRequest.getMessage(),
                expensesReport));
    }

}


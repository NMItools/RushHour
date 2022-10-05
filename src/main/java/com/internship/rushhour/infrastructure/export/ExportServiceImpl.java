package com.internship.rushhour.infrastructure.export;

import com.internship.rushhour.domain.report.service.ReportService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

@Service
public class ExportServiceImpl implements ExportService{

    private static final String PROVIDER_PRODUCTIVITY = "Provider_productivity_report_";
    private static final String PROVIDER_AVAILABILITY = "Provider_availability_report_from_";
    private static final String PROVIDER_INCOME = "Provider_income_report_for_";

    private final ReportService reportService;

    public ExportServiceImpl(ReportService reportService) {
        this.reportService = reportService;
    }

    private String reportName(String title, String p1){
        return String.join("_", title, p1);
    }

    @Override
    public InputStreamResource exportProductivityReport(int year, FileType fileType) throws ClassNotFoundException, IllegalAccessException {

        return new InputStreamResource(
                FileGenerator.export(reportService.getProductivityReport(year),
                        fileType,
                        reportName(PROVIDER_PRODUCTIVITY, String.valueOf(year))));
    }

    @Override
    public InputStreamResource exportAvailabilityReport(int year, int weekNumber, FileType fileType) throws ClassNotFoundException, IllegalAccessException {
        return new InputStreamResource(
                FileGenerator.export(reportService.getAvailabilityReport(year, weekNumber),
                        fileType,
                        reportName(PROVIDER_AVAILABILITY, String.valueOf(year))));
    }

    @Override
    public InputStreamResource exportIncomeReport(int year, FileType fileType) throws ClassNotFoundException, IllegalAccessException {
        return new InputStreamResource(
                FileGenerator.export(reportService.getIncomeReport(year),
                        fileType,
                        reportName(PROVIDER_INCOME, String.valueOf(year))));
    }
}

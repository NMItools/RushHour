package com.internship.rushhour.infrastructure.export;

import org.springframework.core.io.InputStreamResource;

public interface ExportService {

    InputStreamResource exportProductivityReport(int year, FileType fileType) throws ClassNotFoundException, IllegalAccessException;

    InputStreamResource exportAvailabilityReport(int year, int weekNumber, FileType fileType) throws ClassNotFoundException, IllegalAccessException;

    InputStreamResource exportIncomeReport(int year, FileType fileType) throws ClassNotFoundException, IllegalAccessException;
}

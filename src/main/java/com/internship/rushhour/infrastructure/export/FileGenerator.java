package com.internship.rushhour.infrastructure.export;

import com.internship.rushhour.infrastructure.exceptions.UserActionNeededException;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Stream;

import static com.internship.rushhour.infrastructure.export.Csv.createCSV;
import static com.internship.rushhour.infrastructure.export.Excel.createXLSX;
import static com.internship.rushhour.infrastructure.export.Pdf.createPDF;

public class FileGenerator {

    private final String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static <T> ByteArrayInputStream export(List<T> data, FileType fileType, String title) throws ClassNotFoundException, IllegalAccessException {

        List<FileType> listFileType = Stream.of(FileType.values()).toList();

        return switch (fileType) {
            case CSV -> createCSV(data, title);
            case XLSX -> createXLSX(data, title);
            case PDF -> createPDF(data, title);
            default -> throw new UserActionNeededException("Wrong file type, use: " + listFileType);
        };
    }


}
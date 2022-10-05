package com.internship.rushhour.infrastructure.export;

import com.internship.rushhour.infrastructure.exceptions.UserActionNeededException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Excel {
    public static <T> ByteArrayInputStream createXLSX(List<T> data, String title) throws ClassNotFoundException, IllegalAccessException {

        Class<?> c = Class.forName(data.get(0).getClass().getName());
        Field[] fields = c.getDeclaredFields();

        List<String> header = new ArrayList<>();
        for (Field field : fields) {
            field.setAccessible(true);
            header.add(field.getName());
        }

        List<String> values = null;
        List<List<String>> records = new ArrayList<>();
        for (Object object : data) {
            int m = 0;
            for (Field field : fields) {
                if (m % header.size() == 0) {
                    values = new ArrayList<>();
                }
                field.setAccessible(true);
                values.add(field.get(object).toString());
                m++;
                if(m % header.size() == header.size()-1)
                    records.add(values);
            }
        }

        try (
                Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()
        )
        {
            Sheet sheet = workbook.createSheet(title);

            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < header.size(); col++) {
                Cell dataCell = headerRow.createCell(col);
                dataCell.setCellValue(header.get(col));
            }

            int row = 1;
            int col = 0;
            for (List<String> record : records) {
                Row dataRow = sheet.createRow(row);
                for (String value : record) {
                    Cell dataCell = dataRow.createCell(col);
                    dataCell.setCellValue(value);
                    col++;
                    if(col==record.size()) {
                        col=0;
                    }
                }
                row++;
            }

            workbook.write(out);

            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new UserActionNeededException("Fail to export data to Excel file: " + e.getMessage());
        }
    }
}

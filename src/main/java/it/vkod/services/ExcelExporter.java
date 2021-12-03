package it.vkod.services;


import it.vkod.data.dto.ChecksGridData;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class ExcelExporter {

    private final XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public ExcelExporter() {
        workbook = new XSSFWorkbook();
    }


    private void writeHeaderLine() {

        sheet = workbook.createSheet("Users");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "User ID", style);
        createCell(row, 1, "E-mail", style);
        createCell(row, 2, "Full Name", style);
        createCell(row, 3, "Roles", style);
        createCell(row, 4, "Enabled", style);

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {

        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines(List<ChecksGridData> data) {

        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);

        style.setFont(font);

        for (ChecksGridData cd : data) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, cd.getFirstName(), style);
            createCell(row, columnCount++, cd.getLastName(), style);
            createCell(row, columnCount++, cd.getEmail(), style);
            createCell(row, columnCount++, cd.getCheckedOn(), style);
            createCell(row, columnCount++, cd.getCheckedInAt(), style);
            createCell(row, columnCount++, cd.getCheckedOutAt(), style);

        }
    }

    public void export(HttpServletResponse response, List<ChecksGridData> data) throws IOException {
        writeHeaderLine();
        writeDataLines(data);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }
}
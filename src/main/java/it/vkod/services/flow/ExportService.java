package it.vkod.services.flow;


import com.google.zxing.WriterException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import it.vkod.models.entities.Check;
import it.vkod.services.mappers.CSVExporter;
import it.vkod.services.mappers.ExcelExporter;
import it.vkod.services.mappers.PDFExporter;
import it.vkod.services.mappers.QRExporter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExportService {

    private static final String FILE_NAME = "Aanwezigheidslijst " + LocalDate.now();


    private final ExcelExporter excelExporter;
    private final PDFExporter pdfExporter;
    private final CSVExporter csvExporter;
    private final QRExporter qrExporter;

    public void toCSV(HttpServletResponse response, List< Check > checks) throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException {

        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + FILE_NAME + ".csv" + "\"");

        csvExporter.export(response, checks);

    }

    public void toPDF(HttpServletResponse response, List<Check> data) throws IOException {

        response.setContentType("application/pdf");

        final var headerKey = "Content-Disposition";
        final var headerValue = "attachment; filename=" + FILE_NAME + ".pdf";
        response.setHeader(headerKey, headerValue);

        pdfExporter.export(response, data);

    }

    public void toPDF(HttpServletResponse response, Set<String> usernames) throws IOException, WriterException {

        response.setContentType("application/pdf");

        final var headerKey = "Content-Disposition";
        final var headerValue = "attachment; filename=" + FILE_NAME + ".pdf";
        response.setHeader(headerKey, headerValue);

        qrExporter.export(response, usernames);

    }

    public void toExcel(HttpServletResponse response, List<Check> data) throws IOException {
        response.setContentType("application/octet-stream");

        final var headerKey = "Content-Disposition";
        final var headerValue = "attachment; filename=" + FILE_NAME + ".pdf";
        response.setHeader(headerKey, headerValue);

        excelExporter.export(response, data);
    }
}

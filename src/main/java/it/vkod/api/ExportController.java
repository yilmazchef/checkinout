package it.vkod.api;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import it.vkod.data.dto.ChecksGridData;
import it.vkod.repositories.CheckRepository;
import it.vkod.repositories.EventRepository;
import it.vkod.repositories.UserRepository;
import it.vkod.security.AuthenticatedUser;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

import static com.opencsv.CSVWriter.DEFAULT_SEPARATOR;
import static com.opencsv.CSVWriter.NO_QUOTE_CHARACTER;

@Controller
public class ExportController {

    public static final String EXPORT_CHECKS_CSV_URI = "/checks/csv";
    public static final String EXPORT_CHECKS_PDF_URI = "/checks/pdf";

    private final AuthenticatedUser authenticatedUser;
    private final UserRepository userRepository;
    private final CheckRepository checkRepository;
    private final EventRepository eventRepository;

    private final String filename = "Aanwezigheidslijst " + LocalDate.now();


    public ExportController(@Autowired AuthenticatedUser authenticatedUser,
                            @Autowired UserRepository userRepository,
                            @Autowired CheckRepository checkRepository,
                            @Autowired EventRepository eventRepository) {
        this.authenticatedUser = authenticatedUser;
        this.userRepository = userRepository;
        this.checkRepository = checkRepository;
        this.eventRepository = eventRepository;
    }

    @SneakyThrows
    @GetMapping(EXPORT_CHECKS_CSV_URI)
    public void exportCheckDataCSV(HttpServletResponse response) {

        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

        //create a csv writer
        final var writer = new StatefulBeanToCsvBuilder<ChecksGridData>(response.getWriter())
                .withQuotechar(NO_QUOTE_CHARACTER)
                .withSeparator(DEFAULT_SEPARATOR)
                .withOrderedResults(true)
                .build();

        //write all data to csv file
        writer.write(checkRepository.findAllChecksOfToday());

    }

    @SneakyThrows
    @GetMapping("/checks/pdf")
    public void exportToPDF(HttpServletResponse response) {

        response.setContentType("application/pdf");

        final var headerKey = "Content-Disposition";
        final var headerValue = "attachment; filename=" + filename + ".pdf";
        response.setHeader(headerKey, headerValue);

        final var data = checkRepository.findAllChecksOfToday();
        export(response, data);

    }

    private void writeTableHeader(PdfPTable table) {
        final var cell = new PdfPCell();
        cell.setBackgroundColor(Color.BLUE);
        cell.setPadding(5);

        final var font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("First Name", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Last Name", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("E-mail", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Checked On", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Checked-in At", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Checked-out At", font));
        table.addCell(cell);
    }

    private void writeTableData(PdfPTable table, List<ChecksGridData> data) {

        for (ChecksGridData check : data) {
            table.addCell(check.getEmail());
            table.addCell(check.getFirstName());
            table.addCell(check.getLastName());
            table.addCell(check.getCheckedOn().toString());
            table.addCell(check.getCheckedInAt().toString());
            table.addCell(check.getCheckedOutAt().toString());
        }
    }

    @SneakyThrows
    public void export(HttpServletResponse response, List<ChecksGridData> data) {

        final var document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        final var font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        font.setColor(Color.BLUE);

        final var p = new Paragraph("List of Users", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(p);

        final var table = new PdfPTable(5);
        table.setWidthPercentage(100f);
        table.setWidths(new float[]{1.5f, 3.5f, 3.0f, 3.0f, 1.5f});
        table.setSpacingBefore(10);

        writeTableHeader(table);
        writeTableData(table, data);

        document.add(table);

        document.close();

    }

}

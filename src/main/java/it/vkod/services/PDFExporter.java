package it.vkod.services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import it.vkod.data.dto.CheckDTO;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Component
public class PDFExporter {

    public void export(HttpServletResponse response, List<CheckDTO> data) throws IOException {

        final var document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        final var font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(10);
        font.setColor(Color.BLUE);

        final var p = new Paragraph("Aanmelden van " + LocalDate.now(), font);
        p.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(p);

        final var table = new PdfPTable(6);
        table.setWidthPercentage(100f);
        table.setWidths(new float[]{2f, 2f, 4f, 2.5f, 2.5f, 2.5f});
        table.setSpacingBefore(40);

        writeTableHeader(table);
        writeTableData(table, data);

        document.add(table);

        document.close();

    }

    private void writeTableHeader(PdfPTable table) {
        final var cell = new PdfPCell();
        cell.setBackgroundColor(Color.BLUE);
        cell.setPadding(2);

        final var font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("Voornaam", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Familienaam", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("E-mail", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Datum", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Inchecken", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Uitchecken", font));
        table.addCell(cell);
    }

    private void writeTableData(PdfPTable table, List<CheckDTO> data) {

        for (CheckDTO check : data) {
            table.addCell(check.getFirstName());
            table.addCell(check.getLastName());
            table.addCell(check.getEmail());
            table.addCell(check.getCheckedOn().toString());
            table.addCell(check.getCheckedInAt().toString());
            table.addCell(check.getCheckedOutAt().toString());
        }
    }
}

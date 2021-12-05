package it.vkod.services;

import com.lowagie.text.Image;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import it.vkod.data.dto.CheckDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Component
public class PDFExporter {

    private final AuthenticationService authenticationService;

    public void export(HttpServletResponse response, List<CheckDTO> data) throws IOException {

        final var document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        final var font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(9);
        font.setColor(Color.BLUE);

        final var hi = "Aanmelden van " + LocalDate.now();
        final var h = new Header("Intec Brussel", hi);

        document.add(h);

        var imageFile = new ClassPathResource("META-INF/resources/images/logo-for-pdf.png");
        var imgB = StreamUtils.copyToByteArray(imageFile.getInputStream());
        final var img = Image.getInstance(imgB);
        img.setWidthPercentage(25F);

        document.add(img);

        final var table = new PdfPTable(6);
        table.setWidthPercentage(100f);
        table.setWidths(new float[]{2.5f, 2.5f, 4f, 2.5f, 2f, 2f});
        table.setSpacingBefore(50);

        writeTableHeader(table);
        writeTableData(table, data);

        document.add(table);

        final var oUser = authenticationService.get();
        if (oUser.isPresent()) {
            final var user = oUser.get();
            final var name = user.getFirstName() + " " + user.getLastName();
            final var email = user.getEmail();

            final var pi = "Met vriendelijke groeten,\n" +
                    "\n" +
                    name + "\n" +
                    email + " \n" +
                    "\n" +
                    "\n" +
                    "INTEC BRUSSEL vzw\n" +
                    "\n" +
                    "Rouppeplein 16 te 1000 Brussel\n" +
                    "Tel: 02 411 29 07\n" +
                    "\n" +
                    "Ondernemersnummer: 0475.319.893\n" +
                    "\n" +
                    "RPR Brussel\n" +
                    "\n" +
                    "www.intecbrussel.be  - meer kansen op werk!";

            final var closingFont = FontFactory.getFont(FontFactory.TIMES_ROMAN);
            closingFont.setSize(9);
            closingFont.setColor(Color.BLACK);

            final var p = new Paragraph(pi, font);
            p.setAlignment(Paragraph.ALIGN_LEFT);

            p.setSpacingBefore(200);

            document.add(p);
        }

        document.close();

    }

    private void writeTableHeader(PdfPTable table) {
        final var cell = new PdfPCell();
        cell.setBackgroundColor(Color.BLUE);
        cell.setPadding(2);

        final var font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("Familienaam", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Voornaam", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("E-mail", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Datum", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("In", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Uit", font));
        table.addCell(cell);
    }

    private void writeTableData(PdfPTable table, List<CheckDTO> data) {

        for (CheckDTO check : data) {
            table.addCell(check.getLastName());
            table.addCell(check.getFirstName());
            table.addCell(check.getEmail().replaceAll("@intecbrussel.be", "@intec.."));
            table.addCell(check.getCheckedOn().toString());
            table.addCell(check.getCheckedInAt().toString());
            table.addCell(check.getCheckedOutAt().toString());
        }
    }
}

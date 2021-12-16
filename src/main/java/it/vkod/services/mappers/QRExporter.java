package it.vkod.services.mappers;

import com.google.zxing.WriterException;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfWriter;
import it.vkod.services.flow.AuthenticationService;
import it.vkod.utils.QRUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class QRExporter {

    private final AuthenticationService authenticationService;

    public void export(HttpServletResponse response, String... usernames) throws IOException, WriterException {

        final var oOrganizer = authenticationService.get();

        if (oOrganizer.isPresent()) {

            final var organizer = oOrganizer.get();

            final var document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, response.getOutputStream());

            document.open();

            final var organizerInfo = organizer.getFirstName() + " " + organizer.getLastName();
            document.addAuthor(organizerInfo);
            document.addCreator(organizerInfo);
            document.addCreationDate();

            for (String username : usernames) {

                final var header = new Phrase(organizerInfo);
                document.add(header);

                final var qrData = QRUtils.generateQR(username, 128, 128);
                final var img = Image.getInstance(qrData);
                img.scaleAbsolute(128f, 128f);
                document.add(img);
            }

            document.close();

        }

    }

}

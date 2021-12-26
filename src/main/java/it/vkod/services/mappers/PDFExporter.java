package it.vkod.services.mappers;


import com.lowagie.text.Image;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import it.vkod.models.entities.Check;
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

	public void export( HttpServletResponse response, List< Check > checks ) throws IOException {

		final var document = new Document( PageSize.A4 );
		PdfWriter.getInstance( document, response.getOutputStream() );

		document.open();
		final var hFont = FontFactory.getFont( FontFactory.HELVETICA_BOLD );
		hFont.setSize( 14 );
		hFont.setColor( Color.BLUE );

		final var hi = "Aanmelden van " + LocalDate.now();
		final var h = new Paragraph( hi, hFont );
		h.setSpacingBefore( 120F );
		h.setSpacingAfter( 40F );

		document.add( h );

		var imageFile = new ClassPathResource( "META-INF/resources/images/logo-for-pdf.png" );
		var imgB = StreamUtils.copyToByteArray( imageFile.getInputStream() );
		final var img = Image.getInstance( imgB );
		img.setSpacingBefore( 40F );
		img.setAlignment( 50 );

		img.scaleAbsolute( 64f, 64f );
		document.add( img );

		final var table = new PdfPTable( 6 );
		table.setWidthPercentage( 100f );
		table.setWidths( new float[]{ 2.5f, 2.5f, 4.5f, 2f, 1.5f, 1.5f } );
		table.setSpacingBefore( 50F );
		table.setSpacingAfter( 100F );

		writeTableHeader( table );
		writeTableData( table, checks );

		document.add( table );

		final var organizer = checks.get( 0 ).getOrganizer();
		final var name = organizer.getFirstName() + " " + organizer.getLastName();
		final var email = organizer.getEmail();

		final var pi = "Met vriendelijke groeten,\n" +
				"\n" +
				"\n" +
				name + "\n" +
				email + " \n" +
				"\n" +
				"\n" +
				"INTEC BRUSSEL vzw\n" +
				"Rouppeplein 16 te 1000 Brussel\n" +
				"Tel: 02 411 29 07\n" +
				"\n" +
				"\n" +
				"Ondernemersnummer: 0475.319.893\n" +
				"RPR Brussel\n" +
				"www.intecbrussel.be  - meer kansen op werk!";

		final var fFont = FontFactory.getFont( FontFactory.TIMES_ROMAN );
		fFont.setSize( 10F );
		fFont.setColor( Color.BLACK );

		final var p = new Paragraph( pi, fFont );
		p.setAlignment( Paragraph.ALIGN_LEFT );

		document.add( p );

		document.close();

	}


	private void writeTableHeader( PdfPTable table ) {

		final var cell = new PdfPCell();
		cell.setBackgroundColor( Color.BLUE );
		cell.setPadding( 2 );

		final var font = FontFactory.getFont( FontFactory.HELVETICA );
		font.setSize( 10F );
		font.setColor( Color.WHITE );

		cell.setPhrase( new Phrase( "Familienaam", font ) );
		table.addCell( cell );

		cell.setPhrase( new Phrase( "Voornaam", font ) );
		table.addCell( cell );

		cell.setPhrase( new Phrase( "E-mail", font ) );
		table.addCell( cell );

		cell.setPhrase( new Phrase( "Datum", font ) );
		table.addCell( cell );

		cell.setPhrase( new Phrase( "In", font ) );
		table.addCell( cell );

		cell.setPhrase( new Phrase( "Uit", font ) );
		table.addCell( cell );
	}


	private void writeTableData( PdfPTable table, List< Check > checks ) {

		final var cFont = FontFactory.getFont( FontFactory.TIMES_ROMAN );
		cFont.setSize( 10F );

		for ( Check check : checks ) {
			table.addCell( new Phrase( check.getAttendee().getLastName(), cFont ) );
			table.addCell( new Phrase( check.getAttendee().getFirstName(), cFont ) );
			table.addCell( new Phrase( check.getAttendee().getEmail(), cFont ) );
			table.addCell( new Phrase( check.getType().toString(), cFont ) );
			table.addCell( new Phrase( check.getCreated().toString(), cFont ) );
			table.addCell( new Phrase( check.getUpdated().toString(), cFont ) );
		}
	}

}

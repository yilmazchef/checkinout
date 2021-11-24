package it.vkod.data.dto;


import it.vkod.data.entity.Check;

import java.time.Duration;
import java.util.List;

public class ExportToPDF {

	public static String html( List< Check > checks ) {

		final var contentBuilder = new StringBuilder();


		contentBuilder.append( "<html lang=\"en\">\n" +
				"<head>\n" +
				"    <title>Intec Brussel Attendance Report</title>\n" +
				"</head>\n" +
				"<body>\n" +
				"<div>\n" +
				"   <h2>Students checked in Today</h2>\n" +
				"    <table>\n" +
				"        <tr><th>Student</th><th>Checked-ON" +
				"</th><th>Check-IN</th><th>Check-OUT</th><th>Duration</th></tr>\n" );

		// FOR EACH STUDENT THERE WILL BE ROWS ADDED

		for ( final Check check : checks ) {
			final var row = String.format(
					"        <tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
					check.getQrcode(),
					check.getCheckedOn(),
					check.getCheckedInAt().toString(),
					check.getCheckedOutAt().toString(),
					( Duration.between( check.getCheckedInAt().toLocalTime(), check.getCheckedOutAt().toLocalTime() ) ).toString() );

			contentBuilder.append( row );
			contentBuilder.append( "\n" );
		}

		contentBuilder.append( "    </table>\n" +
				"</div>\n" +
				"</body>\n" +
				"</html>" );

		return contentBuilder.toString();
	}

}

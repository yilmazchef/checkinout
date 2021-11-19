package it.vkod.views;


import com.google.zxing.WriterException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import it.vkod.security.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static it.vkod.utils.QRUtils.generateQR;

@PageTitle( "Generate" )
@Route( value = "gen" )
@PermitAll
public class GenerateView extends VerticalLayout {

	public GenerateView( @Autowired AuthenticatedUser authenticatedUser ) {

		addClassNames( "flex", "flex-col", "h-full" );

		final var oUser = authenticatedUser.get();

		oUser.ifPresent( user -> {

			final var generateLayout = new VerticalLayout();
			final var qrField = new TextField( user.getUsername() );
			final var generateButton = new Button( "Generate", onClick -> {
				try {
					generateLayout.add( convertToImage( generateQR( user.getUsername(), 512, 512 ), user.getUsername() ) );
					Notification.show( ( "Generated a QR Code for " + user.getUsername() ), 8000,
							Notification.Position.BOTTOM_CENTER ).open();

				} catch ( WriterException | IOException fileEx ) {
					Notification.show( fileEx.getMessage(), 3000, Notification.Position.BOTTOM_CENTER ).open();
				}
			} );

			generateLayout.setMargin( false );
			generateLayout.setPadding( false );
			generateLayout.setJustifyContentMode( JustifyContentMode.CENTER );
			generateLayout.setAlignItems( Alignment.CENTER );
			generateLayout.add( qrField, generateButton );

			setJustifyContentMode( JustifyContentMode.CENTER );
			setHorizontalComponentAlignment( Alignment.CENTER );
			setAlignItems( Alignment.CENTER );
			add( generateLayout );

		} );


	}


	private Image convertToImage( byte[] imageData, String imageInfo ) {

		return new Image( new StreamResource( "isr", ( InputStreamFactory ) () -> new ByteArrayInputStream( imageData ) ), imageInfo );
	}

}

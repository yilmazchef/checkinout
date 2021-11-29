package it.vkod.views;


import com.google.zxing.WriterException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import it.vkod.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static it.vkod.utils.QRUtils.generateQR;

@PageTitle( "Generate" )
@Route( value = "gen" )
@AnonymousAllowed
public class GenerateView extends VerticalLayout {

	private final UserRepository userRepository;


	public GenerateView( @Autowired UserRepository userRepository ) {

		this.userRepository = userRepository;

		addClassNames( "flex", "flex-col", "h-full" );


		final var generateLayout = new VerticalLayout();
		final var usernameField = new TextField( "Username / Email / Phone" );

		final var generateButton = new Button( "Generate", onClick -> {

			final var keyword = usernameField.getValue();
			final var oUser = this.userRepository.findByUsernameOrEmailOrPhone( keyword.toLowerCase(), keyword, keyword.trim() );

			if ( oUser.isPresent() ) {
				final var user = oUser.get();
				try {

					final var userJson = String.format( "{ \"username\" : \"%s\", \"hashedPassword\" : \"%s\" }", user.getUsername(), user.getHashedPassword() );

					generateLayout.add( convertToImage( generateQR( userJson, 512, 512 ), user.getUsername() ) );
					Notification.show( ( "Generated a QR Code for " + user.getUsername() ), 8000, Notification.Position.BOTTOM_CENTER ).open();

				} catch ( WriterException | IOException fileEx ) {
					Notification.show( fileEx.getMessage(), 3000, Notification.Position.BOTTOM_CENTER ).open();
				}
			} else {
				Notification.show( "USER DOES NOT EXIST !! ", 3000, Notification.Position.BOTTOM_CENTER ).open();
			}

		} );

		generateLayout.setMargin( false );
		generateLayout.setPadding( false );
		generateLayout.setJustifyContentMode( JustifyContentMode.CENTER );
		generateLayout.setAlignItems( Alignment.CENTER );
		generateLayout.add( usernameField, generateButton );

		setJustifyContentMode( JustifyContentMode.CENTER );
		setHorizontalComponentAlignment( Alignment.CENTER );
		setAlignItems( Alignment.CENTER );
		add( generateLayout );


	}


	private Image convertToImage( final byte[] imageData, final String username ) {

		return new Image( new StreamResource( username.concat( "_QR.png" ),
				( InputStreamFactory ) () -> new ByteArrayInputStream( imageData ) ), username );
	}

}

package it.vkod.views;


import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.wontlost.zxing.Constants;
import com.wontlost.zxing.ZXingVaadinReader;
import it.vkod.repositories.UserRepository;
import it.vkod.security.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.security.PermitAll;

@PageTitle( "Check-in" )
@Route( "val" )
@RouteAlias( "validate" )
@PermitAll
public class ValidateView extends VerticalLayout {

	private final AuthenticatedUser authenticatedUser;
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;


	public ValidateView( @Autowired AuthenticatedUser authenticatedUser,
	                     @Autowired UserRepository userRepository,
	                     @Autowired BCryptPasswordEncoder passwordEncoder ) {

		this.authenticatedUser = authenticatedUser;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;

		initStyle();


		final var user = authenticatedUser.get();

		user.ifPresent( organizer -> {

			final var scanLayout = new VerticalLayout();
			final var reader = new ZXingVaadinReader();

			reader.setFrom( Constants.From.camera );
			reader.setId( "video" ); //id needs to be 'video' if From.camera.
			reader.setStyle( "object-fit: cover; width:100; height:100vh; max-height:100" );

			reader.addValueChangeListener( scannedQRCode -> validateUser( scannedQRCode.getValue() ) );

			scanLayout.setMargin( false );
			scanLayout.setPadding( false );
			scanLayout.setJustifyContentMode( JustifyContentMode.CENTER );
			scanLayout.setAlignItems( Alignment.CENTER );
			scanLayout.add( reader );

			add( scanLayout );

		} );


	}


	private void validateUser( final String scannedQRCode ) {

		final var oAttendee = userRepository.findByUsername( scannedQRCode );
		if ( oAttendee.isPresent() ) {
			Notification.show( ( "QR Code is valid !" ),
					4000,
					Notification.Position.BOTTOM_CENTER ).open();
		} else {
			Notification.show( ( "Invalid or Unregistered QR ! ->  " + scannedQRCode ),
					4000,
					Notification.Position.BOTTOM_CENTER ).open();
		}
	}


	private void initStyle() {

		addClassNames( "flex", "flex-col", "h-full" );
		setMargin( false );
		setPadding( false );
		setSpacing( false );
		setWidthFull();

		setJustifyContentMode( JustifyContentMode.CENTER );
		setHorizontalComponentAlignment( Alignment.CENTER );
		setAlignItems( Alignment.CENTER );
	}

}

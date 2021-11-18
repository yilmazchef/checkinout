package it.vkod.views;


import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import com.wontlost.zxing.Constants;
import com.wontlost.zxing.ZXingVaadinReader;
import it.vkod.data.entity.Check;
import it.vkod.data.entity.Event;
import it.vkod.repositories.CheckRepository;
import it.vkod.repositories.EventRepository;
import it.vkod.repositories.UserRepository;
import it.vkod.security.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@PageTitle( "Check-in" )
@Route( "in" )
@RouteAlias( "" )
@PermitAll
public class CheckinView extends VerticalLayout {

	public CheckinView( @Autowired AuthenticatedUser authenticatedUser,
	                    @Autowired UserRepository userRepository,
	                    @Autowired CheckRepository checkRepository,
	                    @Autowired EventRepository eventRepository ) {

		initStyle();

		final var user = authenticatedUser.get();

		user.ifPresent( organizer -> {

			final var scanLayout = new VerticalLayout();
			final var reader = new ZXingVaadinReader();

			reader.setFrom( Constants.From.camera );
			reader.setId( "video" ); //id needs to be 'video' if From.camera.
			reader.setStyle( "object-fit: cover; width:100; height:100vh; max-height:100" );

			reader.addValueChangeListener( scannedQRCode -> {

				final var oAttendee = userRepository.findByUsername( scannedQRCode.getValue() );
				if ( oAttendee.isPresent() ) {
					final var checkEntity = new Check()
							.withActive( true )
							.withCurrentSession( VaadinSession.getCurrent().getSession().getId() )
							.withPincode( 111111 )
							.withCheckedInAt( Time.valueOf( LocalTime.now() ) )
							.withCheckedOutAt( Time.valueOf( LocalTime.now() ) )
							.withQrcode( scannedQRCode.getValue() )
							.withLat( 10.00F )
							.withLon( 10.00F )
							.withCheckedOn( Date.valueOf( LocalDate.now() ) );
					final var check = checkRepository.save( checkEntity );

					final var eventEntity = new Event()
							.withCheckId( check.getId() )
							.withAttendeeId( oAttendee.get().getId() )
							.withOrganizerId( organizer.getId() );

					final var event = eventRepository.save( eventEntity );

					Notification.show( "Granted (V): Welcome " + organizer.getFirstName() + " " + organizer.getLastName() + "!",
							4000,
							Notification.Position.BOTTOM_CENTER ).open();
				} else {
					Notification.show( ( "Rejected (X): " + scannedQRCode.getValue() ),
							4000,
							Notification.Position.BOTTOM_CENTER ).open();
				}


			} );

			scanLayout.setMargin( false );
			scanLayout.setPadding( false );
			scanLayout.setJustifyContentMode( JustifyContentMode.CENTER );
			scanLayout.setAlignItems( Alignment.CENTER );

			scanLayout.add( reader );
			add( scanLayout );

		} );


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

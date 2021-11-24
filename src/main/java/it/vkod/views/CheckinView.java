package it.vkod.views;


import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import com.wontlost.zxing.Constants;
import com.wontlost.zxing.ZXingVaadinReader;
import it.vkod.data.entity.Check;
import it.vkod.data.entity.Event;
import it.vkod.data.entity.User;
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
import java.util.ArrayList;

@PageTitle( "Check-in" )
@Route( "in" )
@RouteAlias( "checkin" )
@RouteAlias( "" )
@PermitAll
public class CheckinView extends VerticalLayout {

	public CheckinView( @Autowired AuthenticatedUser authenticatedUser,
	                    @Autowired UserRepository userRepository,
	                    @Autowired CheckRepository checkRepository,
	                    @Autowired EventRepository eventRepository ) {

		initStyle();


		SplitLayout splitLayout = new SplitLayout();

		final var user = authenticatedUser.get();

		user.ifPresent( organizer -> {

			final var attendeesGrid = new Grid< User >();
			attendeesGrid.addColumn( User::getFirstName ).setHeader( "First Name" );
			attendeesGrid.addColumn( User::getLastName ).setHeader( "LastName" );
			attendeesGrid.addColumn( User::getUsername ).setHeader( "Username" );
			attendeesGrid.addThemeVariants( GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES );

			final var userList = new ArrayList< User >();
			final var checkList = checkRepository.findByCheckedOnToday();
			for ( final Check check : checkList ) {
				final var foundUser = userRepository.findByUsername( check.getQrcode() );
				foundUser.ifPresent( userList::add );
			}
			attendeesGrid.setItems( userList );

			final var scanLayout = new VerticalLayout();
			final var reader = new ZXingVaadinReader();

			reader.setFrom( Constants.From.camera );
			reader.setId( "video" ); //id needs to be 'video' if From.camera.
			reader.setStyle( "object-fit: cover; width:100; height:100vh; max-height:100" );

			reader.addValueChangeListener( scannedQRCode -> {

				final var oAttendee = userRepository.findByUsername( scannedQRCode.getValue() );
				if ( oAttendee.isPresent() ) {
					final var attendee = oAttendee.get();

					final var hasCheckedInBefore = checkRepository.findByCheckedOnAndQrcode( Date.valueOf( LocalDate.now() ), scannedQRCode.getValue() );

					if ( hasCheckedInBefore.isEmpty() ) {
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
								.withAttendeeId( attendee.getId() )
								.withOrganizerId( organizer.getId() )
								.withCheckType( "IN" );

						final var event = eventRepository.save( eventEntity );

						final var foundUser = userRepository.findById( event.getAttendeeId() );
						foundUser.ifPresent( userList::add );
						attendeesGrid.setItems( userList );

						Notification.show( attendee.getFirstName() + " " + attendee.getLastName() + " has checked IN" +
										"." + "Organized by " + organizer.getFirstName() + " " + organizer.getLastName(),
								4000,
								Notification.Position.BOTTOM_CENTER ).open();
					} else {
						Notification.show( ( "Already Checked Before (?): " + scannedQRCode.getValue() ),
								4000,
								Notification.Position.BOTTOM_CENTER ).open();
					}

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

			splitLayout.addToPrimary( scanLayout );
			splitLayout.addToSecondary( attendeesGrid );

		} );

		splitLayout.setSplitterPosition( 80 );
		add( splitLayout );


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

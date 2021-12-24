package it.vkod.views.pwa;


import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import com.wontlost.zxing.Constants;
import com.wontlost.zxing.ZXingVaadinReader;
import it.vkod.models.entities.Check;
import it.vkod.models.entities.CheckType;
import it.vkod.models.entities.User;
import it.vkod.models.entities.UserRole;
import it.vkod.services.flow.AuthenticationService;
import it.vkod.services.flow.CheckService;
import it.vkod.services.flow.UserService;
import it.vkod.views.components.CheckedUserLayout;
import it.vkod.views.components.NotificationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.elmot.flow.sensors.GeoLocation;

import javax.annotation.security.PermitAll;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.stream.Collectors;

@PageTitle( "Inchecken/Uitchecken" )
@Route( value = "", layout = DesktopLayout.class )
@RouteAlias( value = "m", layout = MobileLayout.class )
@RouteAlias( value = "check", layout = DesktopLayout.class )
@RouteAlias( value = "mobile/check", layout = MobileLayout.class )
@PermitAll
public class CheckView extends VerticalLayout {

	public CheckView( @Autowired AuthenticationService authService, @Autowired UserService userService,
	                  @Autowired CheckService checkService ) {

		final var location = new GeoLocation();
		location.setWatch( true );
		location.setHighAccuracy( true );
		location.setTimeout( 100000 );
		location.setMaxAge( 200000 );
		add( location );

		final var scanner = new ZXingVaadinReader();
		scanner.setFrom( Constants.From.camera );
		scanner.setId( "video" ); // id needs to be 'video' if From.camera.
		scanner.setStyle( "object-fit: cover; width:auto; height: 60vh; max-width:96vw;" );
		add( scanner );

		final var authUser = authService.get();
		if ( authUser.isPresent() ) {

			final var organizer = authUser.get();
			final var userRoles = organizer.getRoles();
			final var userAuthorized = ( userRoles.stream().anyMatch( role -> ( role == UserRole.MANAGER || role == UserRole.TEACHER ) ) );

			if ( !userAuthorized ) {
				NotificationUtils
						.error( "The " + userRoles.stream().map( Enum::name ).collect(
								Collectors.joining( " " ) ) + " is not authorized to view this page!" ).open();
			} else {

				final var type = new RadioButtonGroup< CheckType >();
				type.setItems( DataProvider.ofItems( CheckType.values() ) );
				add( type );

				scanner.addValueChangeListener( onScan -> {
					final var check = checkService.create( check( organizer, userService.getByUsername( onScan.getValue() ), location, type.getValue() ) );

					final var checkLayout = new CheckedUserLayout( check );
					add( checkLayout );

					NotificationUtils.success(
							check.getAttendee().getFirstName()
									+ " "
									+ check.getAttendee().getLastName()
									+ ( check.getType() == CheckType.PHYSICAL_IN ? " heeft ingecheckt " : " heeft uitgecheckt" )
									+ "."
					).open();

				} );
			}

		} else {
			NotificationUtils.error( "The user NOT found!" ).open();
		}

	}


	public Check check( User organizer, User attendee, GeoLocation location, CheckType type ) {

		return new Check()
				.setOrganizer( organizer )
				.setAttendee( attendee )
				.setIn( ZonedDateTime.now() )
				.setOut( ZonedDateTime.now() )
				.setCourse( organizer.getCourse() )
				.setLat( location.getValue().getLatitude() )
				.setLon( location.getValue().getLongitude() )
				.setValidation( new Random().nextInt( 8999 ) + 1000 )
				.setSession( VaadinSession.getCurrent().getSession().getId() )
				.setType( type );
	}

}

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

@PageTitle( "Remote Inchecken/Uitchecken" )
@Route( value = "r", layout = DesktopLayout.class )
@RouteAlias( value = "m/r", layout = MobileLayout.class )
@RouteAlias( value = "remote/check", layout = DesktopLayout.class )
@RouteAlias( value = "mobile/remote/check", layout = MobileLayout.class )
@PermitAll
public class CheckRemoteView extends VerticalLayout {

	public CheckRemoteView( @Autowired AuthenticationService authService, @Autowired UserService userService, @Autowired CheckService checkService ) {

		authService.get().ifPresent( attendee -> {

			final var location = new GeoLocation();
			location.setWatch( true );
			location.setHighAccuracy( true );
			location.setTimeout( 100000 );
			location.setMaxAge( 200000 );
			add( location );

			final var scanner = new ZXingVaadinReader();
			scanner.setFrom( Constants.From.camera );
			scanner.setId( "video" ); // id needs to be 'video' if From.camera.
			scanner.setStyle( "object-fit: cover; width:95vw; height: 95vh; max-width:400px;" );

			final var checks = checkService.fromCourse( attendee.getCourse() );

			scanner.addValueChangeListener( onScan -> {
				{
					final var type = new RadioButtonGroup< CheckType >();
					type.setItems( DataProvider.ofItems( CheckType.values() ) );
					add(type);

					final var check = checkService.create( check( userService.getByUsername( onScan.getValue() ), attendee, location, type.getValue() ) );
					checks.add( check );

					final var checkLayout = new CheckedUserLayout( check );
					add( checkLayout );

					NotificationUtils.success(
							check.getAttendee().getFirstName()
									+ " "
									+ check.getAttendee().getLastName()
									+ ( check.getType() == CheckType.REMOTE_IN ? " heeft remote ingecheckt " : " remote heeft uitgecheckt" )
									+ "."
					).open();
				}
			} );


			add( scanner );

		} );

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

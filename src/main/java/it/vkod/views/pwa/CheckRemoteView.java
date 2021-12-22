package it.vkod.views.pwa;


import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import com.wontlost.zxing.Constants;
import com.wontlost.zxing.ZXingVaadinReader;
import it.vkod.models.entities.Check;
import it.vkod.models.entities.CheckType;
import it.vkod.services.flow.AuthenticationService;
import it.vkod.services.flow.CheckService;
import it.vkod.services.flow.UserService;
import it.vkod.views.components.CheckedUserLayout;
import it.vkod.views.components.NotificationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.elmot.flow.sensors.GeoLocation;

import javax.annotation.security.PermitAll;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
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
					final var check = checkService.create( new Check()
							.setOrganizer( userService.getByUsername( onScan.getValue() ) )
							.setAttendee( attendee )
							.setActive( true )
							.setCheckedOn( Date.valueOf( LocalDate.now() ) )
							.setCheckedInAt( Time.valueOf( LocalTime.now() ) )
							.setCheckedOutAt( Time.valueOf( LocalTime.now() ) )
							.setCourse( attendee.getCourse() )
							.setLat( location.getValue().getLatitude() )
							.setLon( location.getValue().getLongitude() )
							.setPin( new Random().nextInt( 8999 ) + 1000 )
							.setSession( VaadinSession.getCurrent().getSession().getId() )
							.setValidLocation( true )
							.setType( CheckType.REMOTE_IN )
					);

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

}

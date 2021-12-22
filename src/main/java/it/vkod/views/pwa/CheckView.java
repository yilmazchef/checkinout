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
import it.vkod.services.flow.SessionService;
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

@PageTitle( "Inchecken/Uitchecken" )
@Route( value = "", layout = DesktopLayout.class )
@RouteAlias( value = "m", layout = MobileLayout.class )
@RouteAlias( value = "check", layout = DesktopLayout.class )
@RouteAlias( value = "mobile/check", layout = MobileLayout.class )
@PermitAll
public class CheckView extends VerticalLayout {

	public CheckView( @Autowired AuthenticationService authService, @Autowired UserService userService,
	                  @Autowired CheckService checkService, @Autowired SessionService sessionService ) {

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
		add( scanner );

		authService.get()
				.ifPresent( organizer -> scanner.addValueChangeListener( onScan -> {
					final var check = checkService.create( new Check()
							.setOrganizer( organizer )
							.setAttendee( userService.getByUsername( onScan.getValue() ) )
							.setActive( true )
							.setCheckedOn( Date.valueOf( LocalDate.now() ) )
							.setCheckedInAt( Time.valueOf( LocalTime.now() ) )
							.setCheckedOutAt( Time.valueOf( LocalTime.now() ) )
							.setCourse( organizer.getCourse() )
							.setLat( location.getValue().getLatitude() )
							.setLon( location.getValue().getLongitude() )
							.setPin( new Random().nextInt( 8999 ) + 1000 )
							.setSession( VaadinSession.getCurrent().getSession().getId() )
							.setValidLocation( true )
							.setType( CheckType.PHYSICAL_IN )
					);

					final var checkLayout = new CheckedUserLayout( check );
					add( checkLayout );

					NotificationUtils.success(
							check.getAttendee().getFirstName()
									+ " "
									+ check.getAttendee().getLastName()
									+ ( check.getType() == CheckType.PHYSICAL_IN ? " heeft ingecheckt " : " heeft uitgecheckt" )
									+ "."
					).open();

				} ) );

	}

}

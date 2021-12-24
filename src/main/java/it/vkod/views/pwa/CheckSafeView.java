package it.vkod.views.pwa;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
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

import javax.annotation.security.RolesAllowed;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Random;

@PageTitle( "Inchecken/Uitchecken" )
@Route( value = "m/safe", layout = MobileLayout.class )
@RouteAlias( value = "mobile/failsafe", layout = MobileLayout.class )
@RolesAllowed( { "ADMIN", "MANAGER", "LEADER" } )
public class CheckSafeView extends VerticalLayout {

	public CheckSafeView( @Autowired AuthenticationService authService, @Autowired UserService userService,
	                      @Autowired CheckService checkService ) {

		authService.get().ifPresent( organizer -> {

			final var location = new GeoLocation();
			location.setWatch( true );
			location.setHighAccuracy( true );
			location.setTimeout( 100000 );
			location.setMaxAge( 200000 );
			add( location );

			final var failSafeForm = new FormLayout();

			final var dateTimePicker = new DateTimePicker();
			dateTimePicker.setLabel( "Het datum van de check" );
			dateTimePicker.setHelperText( "Het datum moet tussen de laatste 60 dagen zijn." );
			dateTimePicker.setAutoOpen( true );
			dateTimePicker.setMin( LocalDateTime.now().minusDays( 1 ) );
			dateTimePicker.setMax( LocalDateTime.now().plusDays( 7 ) );
			dateTimePicker.setValue( LocalDateTime.now() );

			final var username = new TextField( "Gebruikersnaam:" );
			final var checks = checkService.fromCourse( organizer.getCourse() );

			final var type = new RadioButtonGroup< CheckType >();
			type.setItems( DataProvider.ofItems( CheckType.values() ) );
			add( type );

			final var checkButton = new Button( VaadinIcon.SIGN_IN_ALT.create() );
			checkButton.addClickListener( onScan -> {

				final var check = checkService.create( check( organizer, userService.getByUsername( username.getValue() ), location, type.getValue() ) );

				checks.add( check );

				final var checkLayout = new CheckedUserLayout( check );
				add( checkLayout );

				NotificationUtils.success(
								check.getAttendee().getFirstName()
										+ " "
										+ check.getAttendee().getLastName()
										+ ( check.getType() == CheckType.PHYSICAL_IN ? " heeft ingecheckt "
										: " heeft uitgecheckt" )
										+ "." )
						.open();
			} );

			failSafeForm.add( username, dateTimePicker, checkButton );
			add( failSafeForm );

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

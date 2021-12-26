package it.vkod.views.pwa;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
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
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;

@PageTitle( "Inchecken/Uitchecken" )
@Route( value = "", layout = BaseLayout.class )
@RouteAlias( value = "check", layout = BaseLayout.class )
@PermitAll
public class CheckView extends VerticalLayout {

	private final HorizontalLayout usersLayout = new HorizontalLayout();


	public CheckView( @Autowired AuthenticationService authService, @Autowired UserService userService, @Autowired CheckService checkService ) {

		setAlignItems( Alignment.CENTER );
		setJustifyContentMode( JustifyContentMode.CENTER );

		usersLayout.setAlignItems( Alignment.CENTER );
		usersLayout.getStyle().set( "line-height", "var(--lumo-line-height-m)" );

		final var oUser = authService.get();

		// IF PRESENT
		oUser.ifPresent( user -> {

			final var type = new RadioButtonGroup< CheckType >();
			type.setItems( DataProvider.ofItems( CheckType.values() ) );
			add( type );

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

			final var failSafeForm = new FormLayout();

			scanner.addValueChangeListener( onScan -> {

				if ( type.getValue() == CheckType.OTHER && user.getUsername().equalsIgnoreCase( onScan.getValue() )
						&& ( isAdmin( user.getRoles() ) || isManager( user.getRoles() ) || isTeacher( user.getRoles() ) ) ) {

					final var safeCheck = new Select<>( CheckType.values() );
					failSafeForm.add( safeCheck );

					final var safeDate = new DateTimePicker();
					safeDate.setLabel( "Het datum van de check" );
					safeDate.setHelperText( "Het datum moet tussen de laatste 60 dagen zijn." );
					safeDate.setAutoOpen( true );
					safeDate.setMin( LocalDateTime.now().minusDays( 1 ) );
					safeDate.setMax( LocalDateTime.now().plusDays( 7 ) );
					safeDate.setValue( LocalDateTime.now() );

					final var safeUsername = new TextField( "Gebruikersnaam:" );

					final var safeSubmit = new Button( "Safe Submit", VaadinIcon.SIGN_IN_ALT.create() );
					safeSubmit.addClickListener( onSafeSubmit -> {

						final var check = checkService.create( check( user, userService.getByUsername( safeUsername.getValue() ), location, safeCheck.getValue() ) );
						processCheckedData( check );

					} );

					failSafeForm.add( safeUsername, safeDate, safeSubmit );
					add( failSafeForm );

				} else if ( ( type.getValue() == CheckType.REMOTE_IN || type.getValue() == CheckType.REMOTE_OUT )
						&& isStudent( user.getRoles() ) ) {

					final var check = checkService.create( check( userService.getByUsername( onScan.getValue() ), user, location, type.getValue() ) );
					processCheckedData( check );

				} else {

					getUI().ifPresent( ui -> ui.remove( failSafeForm ) );

					final var check = checkService.create( check( user, userService.getByUsername( onScan.getValue() ), location, type.getValue() ) );
					processCheckedData( check );
				}


			} );
		} );

		add( usersLayout );

	}


	public void processCheckedData( Check check ) {

		final var checkLayout = new CheckedUserLayout( check );
		usersLayout.add( checkLayout );

		NotificationUtils.success( check.getAttendee().toString() + ": " + check.getType().name() ).open();
	}


	public Check check( User organizer, User attendee, GeoLocation location, CheckType type ) {

		return new Check()
				.setOrganizer( organizer )
				.setAttendee( attendee )
				.setCourse( attendee.getCourse() )
				.setLat( location.getValue().getLatitude() )
				.setLon( location.getValue().getLongitude() )
				.setValidation( new Random().nextInt( 8999 ) + 1000 )
				.setSession( VaadinSession.getCurrent().getSession().getId() )
				.setType( type );
	}


	public boolean isAdmin( Set< UserRole > roles ) {

		return roles.parallelStream().anyMatch( role -> role == UserRole.ADMIN );
	}


	public boolean isManager( Set< UserRole > roles ) {

		return roles.parallelStream().anyMatch( role -> role == UserRole.MANAGER );
	}


	public boolean isTeacher( Set< UserRole > roles ) {

		return roles.parallelStream().anyMatch( role -> role == UserRole.TEACHER );
	}


	public boolean isStudent( Set< UserRole > roles ) {

		final var student = roles.parallelStream().anyMatch( role -> role == UserRole.STUDENT );

		return student && !isAdmin( roles ) && !isManager( roles ) && !isTeacher( roles );
	}

}

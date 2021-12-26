package it.vkod.views.pwa;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
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
import org.vaadin.elmot.flow.sensors.GeoLocation;

import javax.annotation.security.PermitAll;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static it.vkod.models.entities.CheckType.*;

@PageTitle( "Inchecken/Uitchecken" )
@Route( value = "" )
@RouteAlias( value = "check" )
@PermitAll
public class CheckView extends VerticalLayout {

	private final AuthenticationService authService;
	private final UserService userService;
	private final CheckService checkService;

	private final HorizontalLayout events;
	private final VerticalLayout content;

	private final SplitLayout splitter;

	private final Tabs tabs;
	private final Tab physical;
	private final Tab remote;
	private final Tab failsafe;
	private final Tab statistics;
	private final Tab admin;

	private final GeoLocation location;


	public CheckView( final AuthenticationService authService, final UserService userService, final CheckService checkService ) {

		this.authService = authService;
		this.userService = userService;
		this.checkService = checkService;

		setAlignItems( Alignment.CENTER );
		setJustifyContentMode( JustifyContentMode.CENTER );

		physical = new Tab( "Fysiek in/uit" );
		remote = new Tab( "Afstand in/uit" );
		failsafe = new Tab( "Teamleid" );
		statistics = new Tab( "Tewerkstelling" );
		admin = new Tab( "Administratie" );

		location = new GeoLocation();
		location.setWatch( true );
		location.setHighAccuracy( true );
		location.setTimeout( 100000 );
		location.setMaxAge( 200000 );
		add( location );

		tabs = new Tabs( physical, remote, admin );
		tabs.addThemeVariants( TabsVariant.LUMO_EQUAL_WIDTH_TABS, TabsVariant.LUMO_HIDE_SCROLL_BUTTONS );
		tabs.addSelectedChangeListener( event -> switchTab( event.getSelectedTab() ) );

		events = new HorizontalLayout();
		events.setSpacing( false );
		events.setAlignItems( Alignment.CENTER );
		events.getStyle().set( "line-height", "var(--lumo-line-height-m)" );

		content = new VerticalLayout();
		content.setSpacing( false );
		switchTab( tabs.getSelectedTab() );

		splitter = new SplitLayout(content, events);
		splitter.setOrientation(SplitLayout.Orientation.VERTICAL);
		splitter.setMaxHeight("350px");

		add( tabs, splitter );

	}


	private void switchTab( Tab tab ) {

		content.removeAll();

		final var oUser = authService.get();

		if ( tab.equals( physical ) && oUser.isPresent() ) {

			content.addAndExpand( new Paragraph( "Fysiek in/uit-checken" ) );
			final var user = oUser.get();

			final var checks = checkService.fromCourse( user.getCourse() );
			initAllCheckedData( checks );
			createBadge(checks.size());

			final var type = new RadioButtonGroup< CheckType >();
			type.setItems( DataProvider.ofItems( PHYSICAL_IN, PHYSICAL_OUT ) );
			type.addThemeVariants( RadioGroupVariant.LUMO_HELPER_ABOVE_FIELD );
			content.addAndExpand( type );

			// IF PRESENT
			final var scanner = new ZXingVaadinReader();
			scanner.setFrom( Constants.From.camera );
			scanner.setId( "video" ); // id needs to be 'video' if From.camera.
			scanner.setStyle( "object-fit: cover; width:auto; height: 35vh;" );
			content.addAndExpand( scanner );

			scanner.addValueChangeListener( onScan -> {
				final var check = checkService.create( check( user, userService.getByUsername( onScan.getValue() ), location, type.getValue() ) );
				processCheckedData( check );
			} );

		} else if ( tab.equals( remote ) && oUser.isPresent() ) {

			content.add( new Paragraph( "Afstand (Remote) in/uit-checken" ) );
			final var user = oUser.get();

			final var checks = checkService.fromCourse( user.getCourse() );
			initAllCheckedData( checks );
			createBadge(checks.size());

			final var type = new RadioButtonGroup< CheckType >();
			type.setItems( DataProvider.ofItems( REMOTE_IN, REMOTE_OUT ) );
			type.addThemeVariants( RadioGroupVariant.LUMO_HELPER_ABOVE_FIELD );
			content.add( type );

			// IF PRESENT
			final var scanner = new ZXingVaadinReader();
			scanner.setFrom( Constants.From.camera );
			scanner.setId( "video" ); // id needs to be 'video' if From.camera.
			scanner.setStyle( "object-fit: cover; width:auto; height: 95vh; max-width:50vw;" );
			content.add( scanner );

			scanner.addValueChangeListener( onScan -> {
				final var check = checkService.create( check( userService.getByUsername( onScan.getValue() ), user, location, type.getValue() ) );
				processCheckedData( check );
			} );


		} else if ( tab.equals( failsafe ) && oUser.isPresent() ) {

			content.add( new Paragraph( "Manueel in/uit-checken " ) );
			final var user = oUser.get();

			final var checks = checkService.fromCourse( user.getCourse() );
			initAllCheckedData( checks );
			createBadge(checks.size());

			final var failSafeForm = new FormLayout();

			final var safeCheck = new Select<>( values() );
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
			content.add( failSafeForm );


		} else if ( tab.equals( statistics ) && oUser.isPresent() ) {

			content.add( new Paragraph( "This is the Payment tab" ) );


		} else if ( tab.equals( admin ) && oUser.isPresent() ) {
			content.add( new Paragraph( "This is the Payment tab" ) );


		} else {
			content.add( new Paragraph( "There has been a technical conflict. Please contact the system administrator via helpdesk@intecbrussel.be" ) );


		}
	}


	/**
	 * Helper method for creating a badge.
	 */
	private Span createBadge( int value ) {

		Span badge = new Span( String.valueOf( value ) );
		badge.getElement().getThemeList().add( "badge small contrast" );
		badge.getStyle().set( "margin-inline-start", "var(--lumo-space-xs)" );
		return badge;
	}


	public void initAllCheckedData( List< Check > checks ) {

		for ( final Check check : checks ) {
			final var checkLayout = new CheckedUserLayout( check );
			events.add( checkLayout );
		}
	}


	public void processCheckedData( Check check ) {

		final var checkLayout = new CheckedUserLayout( check );
		events.add( checkLayout );

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

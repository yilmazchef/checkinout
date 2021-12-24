package it.vkod.views.pwa;


import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.server.VaadinSession;
import it.vkod.models.entities.CheckType;
import it.vkod.models.entities.UserRole;
import it.vkod.services.flow.AuthenticationService;
import it.vkod.services.flow.SessionService;
import it.vkod.views.components.NotificationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import static it.vkod.models.entities.CheckType.*;
import static it.vkod.models.entities.UserRole.*;

public class MobileLayout extends AppLayout {

	public MobileLayout( @Autowired AuthenticationService authService, @Autowired SessionService sessionService ) {

		final var tabs = new Tabs();
		final var inTab = new Tab();
		final var outTab = new Tab();
		final var managerTab = new Tab();
		final var adminTab = new Tab();
		final var restartTab = new Tab();
		final var exitTab = new Tab();

		final var oUser = authService.get();
		final var loginButton = new Button( VaadinIcon.DASHBOARD.create() );
		final var inButton = new Button( VaadinIcon.SIGN_IN_ALT.create() );
		final var managerButton = new Button( VaadinIcon.ACADEMY_CAP.create() );
		final var adminButton = new Button( VaadinIcon.BRIEFCASE.create() );
		final var outButton = new Button( VaadinIcon.SIGN_OUT_ALT.create() );
		final var logoutButton = new Button( VaadinIcon.CLOSE.create() );

		loginButton.addClickListener( onClick -> {
			VaadinSession.getCurrent().getSession().invalidate();
			sessionService.setCourse( "" );
			sessionService.setType( CheckType.OTHER );

			try {
				loginButton.getUI().ifPresent( ui -> ui.navigate( LoginView.class ) );
			} catch ( NotFoundException notFoundEx ) {
				NotificationUtils.error( notFoundEx.getMessage() ).open();
			}

		} );

		restartTab.add( loginButton );
		tabs.add( restartTab );

		oUser.ifPresent( user -> {
			sessionService.setCourse( user.getCourse() );

			final var isAdmin = ( user.getRoles().stream().anyMatch( role -> ( role == ADMIN ) ) );
			final var isManager = ( user.getRoles().stream().anyMatch( role -> ( role == UserRole.MANAGER ) ) );
			final var isTeacher = ( user.getRoles().stream().anyMatch( role -> ( role == UserRole.TEACHER ) ) );


			if ( isAdmin || isManager || isTeacher ) {

				inButton.setSizeFull();

				inButton.addClickListener( onClick -> {
					try {
						sessionService.setType( PHYSICAL_IN );
						inButton.getUI().ifPresent( ui -> ui.navigate( CheckView.class ) );
						inTab.getStyle().set( "background-color", "green" );
						outTab.getStyle().set( "background-color", "#161616" );
					} catch ( NotFoundException notFoundEx ) {
						NotificationUtils.error( notFoundEx.getMessage() ).open();
					}

				} );
				inTab.add( inButton );
				tabs.add( inTab );
			}

			if ( isManager ) {

				managerButton.setSizeFull();

				managerButton.addClickListener( onClick -> {
					try {
						managerButton.getUI().ifPresent( ui -> ui.navigate( CheckSafeView.class ) );
					} catch ( NotFoundException notFoundEx ) {
						NotificationUtils.error( notFoundEx.getMessage() ).open();
					}

				} );
				managerTab.add( managerButton );
				tabs.add( managerTab );
			}

			if ( isAdmin ) {
				adminButton.setSizeFull();

				adminButton.addClickListener( onClick -> {
					try {
						adminButton.getUI().ifPresent( ui -> ui.navigate( AdminView.class ) );
					} catch ( NotFoundException notFoundEx ) {
						NotificationUtils.error( notFoundEx.getMessage() ).open();
					}

				} );
				adminTab.add( adminButton );
				tabs.add( adminTab );
			}

			if ( isAdmin || isManager || isTeacher ) {
				outButton.setSizeFull();
				outButton.addClickListener( onClick -> {
					try {
						sessionService.setType( PHYSICAL_OUT );
						sessionService.setCourse( user.getCourse() );
						outButton.getUI().ifPresent( ui -> ui.navigate( CheckView.class ) );
						outTab.getStyle().set( "background-color", "red" );
						inTab.getStyle().set( "background-color", "#161616" );
					} catch ( NotFoundException notFoundEx ) {
						NotificationUtils.error( notFoundEx.getMessage() ).open();
					}

				} );
				outTab.add( outButton );
				tabs.add( outTab );
			}
		} );


		logoutButton.addClickListener( onClick -> {
			VaadinSession.getCurrent().getSession().invalidate();
			sessionService.setCourse( "" );
			sessionService.setType( PHYSICAL_OUT );
			try {
				logoutButton.getUI().ifPresent( ui -> ui.navigate( "logout" ) );
			} catch ( NotFoundException notFoundEx ) {
				NotificationUtils.error( notFoundEx.getMessage() ).open();
			}

		} );

		exitTab.add( logoutButton );
		tabs.add( exitTab );

		tabs.addThemeVariants( TabsVariant.LUMO_MINIMAL, TabsVariant.LUMO_EQUAL_WIDTH_TABS );
		addToNavbar( true, tabs );

	}

}

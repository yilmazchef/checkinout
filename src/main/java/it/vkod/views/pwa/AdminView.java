package it.vkod.views.pwa;


import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.KeyDownEvent;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouteConfiguration;
import it.vkod.services.flow.AdminService;
import it.vkod.services.flow.AuthenticationService;
import it.vkod.views.components.EmbeddedPdfDocument;
import it.vkod.views.components.NotificationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;

@PageTitle( "Administratie" )
@Route( value = "admin", layout = DesktopLayout.class )
@RouteAlias( value = "m/admin", layout = MobileLayout.class )
@RolesAllowed( { "ADMIN", "MANAGER", "LEADER", "TEACHER" } )
public class AdminView extends VerticalLayout {


	public AdminView( @Autowired AuthenticationService authService, @Autowired AdminService adminService ) {

		final var authUser = authService.get();

		if ( authUser.isEmpty() ) {
			NotificationUtils.error( "The active user is not authorized!" ).open();
		} else {
			final var courseField = new TextField( "Voer hier de course-code in: " );
			courseField.setRequired( true );
			courseField.setRequiredIndicatorVisible( true );
			courseField.addKeyDownListener( com.vaadin.flow.component.Key.ENTER,
					( ComponentEventListener< KeyDownEvent > ) keyDownEvent -> {
						final var actionLayout = new HorizontalLayout();
						final var route = RouteConfiguration.forSessionScope().getUrl( CheckView.class );

						final String PDF = route + "/api/v1/export/checks/pdf/" + courseField.getValue();
						final String CSV = route + "/api/v1/export/checks/pdf/" + courseField.getValue();
						final String XLS = route + "/api/v1/export/checks/pdf/" + courseField.getValue();
						final String USERS = route + "/api/v1/export/users/pdf/" + courseField.getValue();

						final var pdfAnchor = new Anchor( PDF, "Druk als PDF" );
						final var csvAnchor = new Anchor( CSV, "Druk als CSV" );
						final var excelAnchor = new Anchor( XLS, "Druk als EXCEL" );

						final var viewerLayout = new VerticalLayout();
						viewerLayout.add( new EmbeddedPdfDocument( USERS ) );

						actionLayout.add( pdfAnchor, csvAnchor, excelAnchor );

						add( actionLayout, viewerLayout );

					} );


			add( courseField );
		}


	}

}

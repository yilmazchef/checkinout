package it.vkod.views.pwa;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import it.vkod.services.flow.AdminService;
import it.vkod.views.components.EmbeddedPdfDocument;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import java.util.concurrent.atomic.AtomicLong;

import static it.vkod.api.ExportController.*;

@PageTitle( "System Admin - Full Access" )
@Route( value = "adm", layout = DesktopLayout.class )
@RouteAlias( value = "m/adm", layout = MobileLayout.class )
@RouteAlias( value = "admin", layout = DesktopLayout.class )
@RouteAlias( value = "mobile/admin", layout = MobileLayout.class )
@RolesAllowed( { "ADMIN", "MANAGER", "LEADER" } )
public class AdminView extends VerticalLayout {

	@SneakyThrows
	public AdminView( @Autowired AdminService adminService ) {

		final var courseField = new TextField( "Voer hier de course-code in: " );
		courseField.setRequired( true );
		courseField.setRequiredIndicatorVisible( true );

		final var env = System.getenv( "hostname" );
		final var hostname = env.contains( "localhost" )
				? env.replaceFirst( "/null", "" )
				: "https://checkinout.intecbrussel.be";

		Notification.show( "The app is running on " + hostname, 5000, Notification.Position.TOP_CENTER ).open();

		final var managementLayout = new VerticalLayout();

		final AtomicLong deleteCounter = new AtomicLong();
		final var flushButton = new Button( "Flush Database", onClick -> {
			final var flushLayout = new VerticalLayout();
			final var confirmationDialog = new Dialog();
			confirmationDialog.setCloseOnEsc( true );
			confirmationDialog.setOpened( true );
			final var info = new H4( "Please confirm database flush operation?" );
			flushLayout.add( info );
			final var confirmationLayout = new HorizontalLayout();
			final var yesButton = new Button( "Yes", yesClicked -> {
				long deletedCount = adminService.flushDatabase();
				deleteCounter.set( deletedCount );
				confirmationDialog.close();
			} );
			final var noButton = new Button( "No", noClicked -> {
				deleteCounter.lazySet( 0 );
				confirmationDialog.close();
			} );

			confirmationLayout.add( yesButton, noButton );
			flushLayout.add( confirmationLayout );
			confirmationDialog.add( confirmationLayout );

			add( flushLayout );

		} );

		managementLayout.add( flushButton );

		final var actionLayout = new VerticalLayout();

		Anchor pdfAnchor = new Anchor( hostname + EXPORT_USERS_PDF_URI + courseField.getValue(), "Exporteer als PDF" );
		Anchor csvAnchor = new Anchor( hostname + EXPORT_CHECKS_CSV_URI + courseField.getValue(), "Exporteer als CSV" );
		Anchor excelAnchor = new Anchor( hostname + EXPORT_CHECKS_EXCEL_URI + courseField.getValue(), "Exporteer als PDF" );

		actionLayout.add( pdfAnchor, csvAnchor, excelAnchor );

		final var viewerLayout = new VerticalLayout();

		viewerLayout.add( new EmbeddedPdfDocument( hostname + EXPORT_USERS_PDF_URI ) );

		add( managementLayout, actionLayout, viewerLayout );

	}

}
package it.vkod.views.pwa;


import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.wontlost.zxing.Constants;
import com.wontlost.zxing.ZXingVaadinReader;
import it.vkod.services.flow.AuthenticationService;
import it.vkod.services.flow.UserService;
import it.vkod.views.components.NotificationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;

@PageTitle( "Valideer QR" )
@Route( value = "val", layout = BaseLayout.class )
@PermitAll
public class ValidateView extends VerticalLayout {

	public ValidateView( @Autowired AuthenticationService authService, @Autowired UserService userService ) {

		addClassNames( "flex", "flex-col", "h-full" );
		setMargin( false );
		setPadding( false );
		setSpacing( false );
		setWidthFull();

		setJustifyContentMode( JustifyContentMode.CENTER );
		setHorizontalComponentAlignment( Alignment.CENTER );
		setAlignItems( Alignment.CENTER );

		final var scanLayout = new VerticalLayout();
		final var reader = new ZXingVaadinReader();

		reader.setFrom( Constants.From.camera );
		reader.setId( "video" ); // id needs to be 'video' if From.camera.
		reader.setStyle( "object-fit: cover; width:100; height:100vh; max-height:100" );

		reader.addValueChangeListener( scannedQRCode -> {
			final var oAttendee = userService.findByUsername( scannedQRCode.getValue() );
			if ( oAttendee.isPresent() ) {
				NotificationUtils.success( "GELDIG" ).open();
			} else {
				NotificationUtils.error( "ONGELDIG!" ).open();
			}
		} );

		scanLayout.setMargin( false );
		scanLayout.setPadding( false );
		scanLayout.setJustifyContentMode( JustifyContentMode.CENTER );
		scanLayout.setAlignItems( Alignment.CENTER );
		scanLayout.add( reader );

		add( scanLayout );

	}

}

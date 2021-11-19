package it.vkod.views;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.wontlost.zxing.Constants;
import com.wontlost.zxing.ZXingVaadinReader;
import it.vkod.repositories.CheckRepository;
import it.vkod.security.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@PageTitle( "Check-out" )
@Route( "out" )
@PermitAll
public class CheckoutView extends VerticalLayout {

	public CheckoutView( @Autowired AuthenticatedUser authenticatedUser,
	                     @Autowired CheckRepository checkRepository ) {

		initStyle();

		final var oUser = authenticatedUser.get();

		if(oUser.isPresent()){

			final var organizer = oUser.get();

			final var scanLayout = new VerticalLayout();
			final var reader = new ZXingVaadinReader();

			reader.setFrom( Constants.From.camera );
			reader.setId( "video" ); //id needs to be 'video' if From.camera.
			reader.setStyle( "object-fit: cover; width:100; height:100vh; max-height:100" );

			reader.addValueChangeListener( scannedQRCode -> {

				final var oCheck = checkRepository.findByCheckedOnAndQrcode(
						Date.valueOf( LocalDate.now() ), scannedQRCode.getValue()
				);

				if ( oCheck.isPresent() ) {

					final var check = oCheck.get();
					checkRepository.save( check.withCheckedOutAt( Time.valueOf( LocalTime.now() ) ) );


					Notification.show( "Granted (V): Welcome " + organizer.getFirstName() + " " + organizer.getLastName() + "!",
							4000,
							Notification.Position.BOTTOM_CENTER ).open();
				} else {
					Notification.show( ( "Rejected (X): " + scannedQRCode.getValue() ),
							4000,
							Notification.Position.BOTTOM_CENTER ).open();
				}


				scanLayout.setMargin( false );
				scanLayout.setPadding( false );
				scanLayout.setJustifyContentMode( JustifyContentMode.CENTER );
				scanLayout.setAlignItems( Alignment.CENTER );

			} );

			scanLayout.add( reader );
			add( scanLayout );
		}
	}


	private void initStyle() {

		addClassNames( "flex", "flex-col", "h-full" );
		setMargin( false );
		setPadding( false );
		setSpacing( false );
		setWidthFull();

		setJustifyContentMode( JustifyContentMode.CENTER );
		setHorizontalComponentAlignment( Alignment.CENTER );
		setAlignItems( Alignment.CENTER );
	}

}

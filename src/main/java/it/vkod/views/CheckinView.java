package it.vkod.views;


import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.wontlost.zxing.Constants;
import com.wontlost.zxing.ZXingVaadinReader;
import it.vkod.data.entity.Check;
import it.vkod.data.service.CheckService;
import it.vkod.security.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;

@PageTitle( "Check-in" )
@Route( "in" )
@RouteAlias( "" )
@PermitAll
public class CheckinView extends VerticalLayout {

	public CheckinView( @Autowired AuthenticatedUser authenticatedUser, @Autowired CheckService checkService ) {

		initStyle();

		final var user = authenticatedUser.get();


		user.ifPresent( u -> {

			final var scanLayout = new VerticalLayout();
			final var reader = new ZXingVaadinReader();

			reader.setFrom( Constants.From.camera );
			reader.setId( "video" ); //id needs to be 'video' if From.camera.
			reader.setStyle( "object-fit: cover; width:100; height:100vh; max-height:100" );

			reader.addValueChangeListener( onValueChange -> {
				final var expectedQR = u.getUsername();
				final var actualQR = onValueChange.getValue();
				final var matches = expectedQR.equalsIgnoreCase( actualQR );
				if ( matches ) {

					Check check = new Check()
							.setUser( u )
							.setQrcode( onValueChange.getValue() );

					final var savedKey = checkService.save( check );

					Notification.show( "Granted (V): Welcome " + u.getFirstName() + " " + u.getLastName() + "!" + "\n" + savedKey.toString(),
							4000,
							Notification.Position.BOTTOM_CENTER ).open();
				} else {
					Notification.show( ( "Rejected (X): " + actualQR ),
							4000,
							Notification.Position.BOTTOM_CENTER ).open();
				}

			} );

			scanLayout.setMargin( false );
			scanLayout.setPadding( false );
			scanLayout.setJustifyContentMode( JustifyContentMode.CENTER );
			scanLayout.setAlignItems( Alignment.CENTER );

			scanLayout.add( reader );
			add( scanLayout );

		} );


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

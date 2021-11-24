package it.vkod.views;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.wontlost.zxing.Constants;
import com.wontlost.zxing.ZXingVaadinReader;
import it.vkod.data.entity.User;
import it.vkod.security.AuthenticatedUser;
import it.vkod.services.EmailService;
import it.vkod.utils.QRUtils;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle( "Login with QR Code" )
@Route( "login_qr" )
@AnonymousAllowed
public class LoginQRView extends VerticalLayout {

	private final AuthenticatedUser authenticatedUser;
	private final EmailService emailService;

	private final VerticalLayout scanLayout = new VerticalLayout();
	private final VerticalLayout loginLayout = new VerticalLayout();

	private final LoginForm loginForm = new LoginForm(); // (1)


	public LoginQRView( @Autowired AuthenticatedUser authenticatedUser, @Autowired EmailService emailService ) {

		this.authenticatedUser = authenticatedUser;
		this.emailService = emailService;

		initStyle();


		loginForm.setAction( "login" );
		loginForm.setForgotPasswordButtonVisible( false );

		LoginI18n i18n = LoginI18n.createDefault();
		i18n.setHeader( new LoginI18n.Header() );
		i18n.getHeader().setTitle( "Check In / Out" );
		i18n.getHeader().setDescription( "Login using QR Scan" );
		i18n.setAdditionalInformation( null );
		loginForm.setI18n( i18n );

		loginLayout.add( loginForm );

		final var reader = new ZXingVaadinReader();

		reader.setFrom( Constants.From.camera );
		reader.setId( "video" ); //id needs to be 'video' if From.camera.
		reader.setStyle( "object-fit: cover; width:100; height:100vh; max-height:100" );

		reader.addValueChangeListener( scannedQRCode -> {
			final var scannedUser = new Object() {
				User data = null;
			};
			try {
				scannedUser.data = QRUtils.extractQR( scannedQRCode.getValue() );
			} catch ( JsonProcessingException jsonProcessingException ) {
				Notification.show( jsonProcessingException.getMessage(), 4000, Notification.Position.BOTTOM_CENTER ).open();
			}
			i18n.getForm().setUsername( scannedUser.data.getUsername() );
			i18n.getForm().setPassword( scannedUser.data.getHashedPassword() );
		} );

		scanLayout.add( reader );

		final var splitLayout = new SplitLayout();
		splitLayout.setOrientation( SplitLayout.Orientation.VERTICAL );
		splitLayout.addToPrimary( loginForm );
		splitLayout.addToSecondary( scanLayout );
		splitLayout.setSplitterPosition( 40 );
		splitLayout.setSizeFull();

		add( splitLayout );

	}


	private void initStyle() {

		scanLayout.addClassNames( "flex", "flex-col", "h-full" );
		scanLayout.setMargin( false );
		scanLayout.setPadding( false );
		scanLayout.setSpacing( false );
		scanLayout.setWidthFull();

		scanLayout.setJustifyContentMode( FlexComponent.JustifyContentMode.CENTER );
		scanLayout.setHorizontalComponentAlignment( FlexComponent.Alignment.CENTER );
		scanLayout.setAlignItems( FlexComponent.Alignment.CENTER );


		loginLayout.addClassNames( "flex", "flex-col", "h-full" );
		loginLayout.setMargin( false );
		loginLayout.setPadding( false );
		loginLayout.setSpacing( false );
		loginLayout.setWidthFull();

		loginLayout.setJustifyContentMode( FlexComponent.JustifyContentMode.EVENLY );
		loginLayout.setHorizontalComponentAlignment( FlexComponent.Alignment.STRETCH );
		loginLayout.setAlignItems( FlexComponent.Alignment.STRETCH );

	}

}

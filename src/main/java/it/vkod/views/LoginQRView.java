package it.vkod.views;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import it.vkod.repositories.UserRepository;
import it.vkod.security.AuthenticatedUser;
import it.vkod.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle( "Login with QR Code" )
@Route( "login_qr" )
@AnonymousAllowed
public class LoginQRView extends VerticalLayout {

	private final AuthenticatedUser authenticatedUser;
	private final EmailService emailService;
	private final UserRepository userRepository;
	private final ObjectMapper objectMapper;

	private final VerticalLayout scanLayout = new VerticalLayout();
	private final VerticalLayout loginLayout = new VerticalLayout();

	private final LoginForm loginForm = new LoginForm(); // (1)


	public LoginQRView( @Autowired AuthenticatedUser authenticatedUser, @Autowired EmailService emailService, @Autowired UserRepository userRepository ) {

		this.authenticatedUser = authenticatedUser;
		this.emailService = emailService;
		this.userRepository = userRepository;
		this.objectMapper = new ObjectMapper();

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

		final var scanner = new ZXingVaadinReader();

		scanner.setFrom( Constants.From.camera );
		scanner.setId( "video" ); //id needs to be 'video' if From.camera.
		scanner.setStyle( "object-fit:cover; max-width:100%; height:auto;" );

		scanner.addValueChangeListener( scannedQRCode -> {

			Notification.show( scannedQRCode.getValue() );

			try {
				this.objectMapper.readValue( scannedQRCode.getValue(), User.class );
			} catch ( JsonProcessingException jsonProcessingEx ) {
				Notification.show( jsonProcessingEx.getMessage(), 4000, Notification.Position.BOTTOM_CENTER );
			}

			final var oScannedUser = this.userRepository.findByUsernameOrEmailOrPhone( scannedQRCode.getValue(), scannedQRCode.getValue(), scannedQRCode.getValue() );

			if ( oScannedUser.isPresent() ) {
				final var scannedUser = oScannedUser.get();
				i18n.getForm().setUsername( scannedUser.getUsername() );
				i18n.getForm().setPassword( scannedUser.getHashedPassword() );
			}

		} );

		scanLayout.add( scanner );

		final var splitLayout = new SplitLayout();
		splitLayout.setOrientation( SplitLayout.Orientation.HORIZONTAL );
		splitLayout.addToPrimary( loginForm );
		splitLayout.addToSecondary( scanLayout );
		splitLayout.setSplitterPosition( 75 );
		splitLayout.setSizeFull();

		splitLayout.getStyle().set( "margin", "0" ).set( "padding", "0" );

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


		setSizeFull();
		setMargin( false );
		setPadding( false );
		setSpacing( false );

	}

}

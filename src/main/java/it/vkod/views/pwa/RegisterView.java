package it.vkod.views.pwa;


import com.google.zxing.WriterException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import it.vkod.models.entities.User;
import it.vkod.models.entities.UserRole;
import it.vkod.services.flow.AuthenticationService;
import it.vkod.services.flow.EmailService;
import it.vkod.services.flow.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static com.vaadin.flow.component.notification.Notification.Position.BOTTOM_CENTER;
import static com.vaadin.flow.component.notification.Notification.show;
import static it.vkod.utils.QRUtils.generateQR;

@PageTitle( "Inschrijven" )
@Route( value = "reg", layout = BaseLayout.class )
@AnonymousAllowed
public class RegisterView extends VerticalLayout {

	public RegisterView( @Autowired AuthenticationService authService, @Autowired UserService userService, @Autowired BCryptPasswordEncoder passwordEncoder, @Autowired EmailService emailService ) {


		final var userRole = new RadioButtonGroup< UserRole >();
		userRole.setItems( DataProvider.ofItems( UserRole.values() ) );

		if ( authService.get().isPresent() ) {
			final var roles = authService.get().get().getRoles().toArray( UserRole[]::new );

			if ( isAdmin( roles ) || isManager( roles ) ) {
				add( userRole );
			}
		}

		final var formLayout = new FormLayout();
		final var phoneField = new TextField();
		final var emailField = new EmailField();
		emailField.setReadOnly( true );
		final var usernameField = new TextField();
		usernameField.setRequiredIndicatorVisible( true );
		usernameField.setRequired( true );

		usernameField.addValueChangeListener( onChange -> {
			emailField.setValue( onChange.getValue().toLowerCase().concat( "@intecbrussel.be" ) );
			if ( emailField.isInvalid() ) {
				usernameField.clear();
			}
		} );

		final var firstNameField = new TextField();
		firstNameField.setAutofocus( true );

		final var lastNameField = new TextField();
		lastNameField.addValueChangeListener( onChange -> {
			usernameField.setValue(
					firstNameField.getValue().toLowerCase().concat( "." ).concat( lastNameField.getValue().toLowerCase() ) );
		} );

		final var passwordField = new PasswordField();
		passwordField.setRequired( true );
		passwordField.setClearButtonVisible( true );
		passwordField.setMinLength( 8 );
		passwordField.setRequiredIndicatorVisible( true );

		final var repeatField = new PasswordField();
		repeatField.setRequired( true );
		repeatField.setClearButtonVisible( true );
		repeatField.setMinLength( 8 );
		repeatField.setRequiredIndicatorVisible( true );

		repeatField.addValueChangeListener( onChange -> {
			final var passwordsMatch = passwordField.getValue().equalsIgnoreCase( onChange.getValue() );
			if ( !passwordsMatch && ( !passwordField.isEmpty() && !repeatField.isEmpty() ) ) {
				passwordField.getStyle().set( "color", "red" );
				passwordField.setInvalid( true );
				repeatField.getStyle().set( "color", "red" );
				repeatField.setInvalid( true );
			} else {
				passwordField.getStyle().set( "color", "green" );
				passwordField.setInvalid( true );
				repeatField.getStyle().set( "color", "green" );
				repeatField.setInvalid( true );
			}
		} );

		final var acceptCheck = new Checkbox( "I accept terms and conditions." );

		formLayout.addFormItem( firstNameField, "First name" );
		formLayout.addFormItem( lastNameField, "Last name" );
		formLayout.addFormItem( usernameField, "Username" );
		formLayout.addFormItem( emailField, "E-mail" );
		formLayout.addFormItem( phoneField, "Phone" );
		formLayout.addFormItem( passwordField, "Password" );
		formLayout.addFormItem( repeatField, "Repeat Password" );

		final var submitButton = new Button( "Submit Form", onClick -> {

			final var exists = userService.existsByUsername( usernameField.getValue() );

			if ( exists.equals( Boolean.FALSE ) ) {
				final var user = new User()
						.setFirstName( firstNameField.getValue().toLowerCase() )
						.setLastName( lastNameField.getValue().toLowerCase() )
						.setUsername( usernameField.getValue().toLowerCase() )
						.setEmail( emailField.getValue().toLowerCase() )
						.setPassword( passwordEncoder.encode( passwordField.getValue() ) )
						.setPhone( phoneField.getValue() )
						.setRoles( Collections.singleton( UserRole.STUDENT ) );

				final var savedUser = userService.createUser( user );

				if ( !savedUser.isNew() ) {

					try {
						emailService.sendSimpleMessage(
								savedUser.getEmail(),
								"Your << CheckInOut >> Account is successfully created.",
								"Username: ".concat( savedUser.getUsername() ).concat( "\n" ).concat( "Password: " )
										.concat( passwordField.getValue() ) );
					} catch ( MailException mailException ) {
						show( mailException.getMessage(), 4000, BOTTOM_CENTER ).open();
					}

					generateQRLayout( user );

					show( "Your << CheckInOut >> Account is successfully created.", 3000, BOTTOM_CENTER ).open();
				}
			} else {
				show( "Error! Make sure you have read and accepted 'Terms and Conditions'. Please double" +
								" check that all required information is entered.",
						4000, BOTTOM_CENTER ).open();

			}
		} );

		submitButton.setEnabled( false );

		acceptCheck.addValueChangeListener( onValueChange -> submitButton.setEnabled( onValueChange.getValue() ) );

		add( formLayout, acceptCheck, submitButton );

	}


	public void initLayout() {

		setPadding( false );
		setMargin( false );
		setSpacing( false );
		setSizeFull();

		setJustifyContentMode( JustifyContentMode.CENTER );
		setHorizontalComponentAlignment( Alignment.CENTER );
		setAlignItems( Alignment.CENTER );
	}


	public void generateQRLayout( User user ) {

		final var layout = new VerticalLayout();

		try {

			layout.add( convertToImage( generateQR( user.getUsername(), 512, 512 ), user.getUsername() ) );

			show( ( "Generated a QR Code for " + user.getUsername() ), 8000,
					BOTTOM_CENTER ).open();

		} catch ( WriterException | IOException fileEx ) {
			show( fileEx.getMessage(), 3000, BOTTOM_CENTER ).open();
		}

		layout.setMargin( false );
		layout.setPadding( false );
		layout.setJustifyContentMode( JustifyContentMode.CENTER );
		layout.setAlignItems( Alignment.CENTER );

		add( layout );
	}


	private Image convertToImage( final byte[] imageData, final String username ) {

		return new Image( new StreamResource( username.concat( "_QR.png" ),
				( InputStreamFactory ) () -> new ByteArrayInputStream( imageData ) ), username );
	}


	public boolean isAdmin( UserRole... roles ) {

		return Arrays.asList( roles ).contains( UserRole.ADMIN );
	}


	public boolean isManager( UserRole... roles ) {

		return Arrays.asList( roles ).contains( UserRole.MANAGER );
	}


	public boolean isTeacher( UserRole... roles ) {

		return Arrays.asList( roles ).contains( UserRole.TEACHER );
	}


	public boolean isStudent( UserRole... roles ) {

		return Arrays.asList( roles ).contains( UserRole.STUDENT );
	}

}
package it.vkod.views.pages;


import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle( "Inloggen" )
@Route( value = "login" )
@AnonymousAllowed
public class LoginPage extends LoginOverlay {

	public LoginPage() {

		setAction( "login" );

		LoginI18n i18n = LoginI18n.createDefault();
		i18n.setHeader( new LoginI18n.Header() );
		i18n.getHeader().setTitle( "Check In/Out" );
		i18n.getHeader().setDescription( "voornaam.familienaam (bvb: john.doe)" );
		i18n.getForm().setSubmit("Inloggen");
		i18n.getForm().setPassword("Wachtwoord");
		i18n.getForm().setUsername("Gebruikersnaam");
		i18n.setAdditionalInformation( null );
		setI18n( i18n );

		setForgotPasswordButtonVisible( true );
		setOpened( true );
	}

}

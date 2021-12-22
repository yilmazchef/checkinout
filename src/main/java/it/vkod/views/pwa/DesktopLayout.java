package it.vkod.views.pwa;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;

public class DesktopLayout extends AppLayout {

	public DesktopLayout() {

		H1 title = new H1( "Intec Aanmeldingssysteem" );
		title.getStyle()
				.set( "font-size", "var(--lumo-font-size-l)" )
				.set( "left", "var(--lumo-space-l)" )
				.set( "margin", "0" )
				.set( "position", "absolute" );

		Tabs tabs = getTabs();

		addToNavbar( title, tabs );
	}


	private Tabs getTabs() {

		Tabs tabs = new Tabs();
		tabs.getStyle().set( "margin", "auto" );
		tabs.add(
				createTab( "Aanmeldingen", CheckView.class ),
				createTab( "Remote", CheckRemoteView.class ),
				createTab( "Administratie", AdminView.class ),
				createTab( "Failsafe", CheckSafeView.class )
		);
		return tabs;
	}


	private Tab createTab( String viewName, Class< ? extends Component > routeLink ) {

		RouterLink link = new RouterLink();
		link.add( viewName );
		link.setRoute( routeLink );
		link.setTabIndex( -1 );

		return new Tab( link );
	}

}
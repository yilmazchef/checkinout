package it.vkod.views.pwa;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.RouterLink;

public class BaseLayout extends AppLayout {

	public BaseLayout() {

		Tabs tabs = getTabs();

		tabs.addThemeVariants( TabsVariant.LUMO_MINIMAL, TabsVariant.LUMO_EQUAL_WIDTH_TABS );
		addToNavbar( true, tabs );

	}


	private Tab createTab( VaadinIcon viewIcon, Class< ? extends Component > routeLink ) {

		Icon icon = viewIcon.create();
		icon.setSize( "var(--lumo-icon-size-s)" );
		icon.getStyle().set( "margin", "auto" );


		RouterLink link = new RouterLink();
		link.add( icon );
		link.setRoute( routeLink );
		link.setTabIndex( -1 );

		return new Tab( link );
	}


	private Tabs getTabs() {

		Tabs tabs = new Tabs();
		tabs.add(
				createTab( VaadinIcon.USER_CHECK, CheckView.class ),
				createTab( VaadinIcon.FILE_TREE, AdminView.class )
		);
		tabs.addThemeVariants( TabsVariant.LUMO_MINIMAL, TabsVariant.LUMO_EQUAL_WIDTH_TABS );
		return tabs;
	}

}

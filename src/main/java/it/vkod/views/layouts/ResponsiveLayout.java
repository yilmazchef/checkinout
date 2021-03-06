package it.vkod.views.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import it.vkod.services.exceptions.ErrorNotificationHandler;
import it.vkod.views.pages.CheckinDetailsPage;
import it.vkod.views.pages.CheckinPage;
import it.vkod.views.pages.CheckoutPage;
import it.vkod.views.pages.GeneratePage;

@CssImport("./styles/chefs-dark-mode.css")
public class ResponsiveLayout extends AppLayout {

    public ResponsiveLayout() {

        VaadinSession.getCurrent().setErrorHandler(new ErrorNotificationHandler());

        final var tabs = new Tabs();
        tabs.add(
                createTab("IN", CheckinPage.class),
                createTab("LIJST", CheckinDetailsPage.class),
                createTab("UIT", CheckoutPage.class),
                createTab("QRGEN", GeneratePage.class)
        );
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL, TabsVariant.LUMO_EQUAL_WIDTH_TABS);

        addToNavbar(true, tabs);

    }

    private Tab createTab(final String title, Class<? extends Component> component) {

        final var link = new RouterLink(title, component);
        link.setTabIndex(-1);
        return new Tab(link);
    }

}
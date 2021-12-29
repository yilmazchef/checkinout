package it.vkod.views.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import it.vkod.services.exceptions.ErrorNotificationHandler;
import it.vkod.views.pages.CheckinPage;
import it.vkod.views.pages.CheckoutPage;

@CssImport("./styles/chefs-dark-mode.css")
public class ResponsiveLayout extends AppLayout {

    public ResponsiveLayout() {

        VaadinSession.getCurrent().setErrorHandler(new ErrorNotificationHandler());

        final var tabs = new Tabs();
        tabs.add(
                createTab(VaadinIcon.COMPRESS_SQUARE, CheckinPage.class),
                createTab(VaadinIcon.EXPAND_SQUARE, CheckoutPage.class)
        );
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL, TabsVariant.LUMO_EQUAL_WIDTH_TABS);

        addToNavbar(true, tabs);

    }

    private Tab createTab(VaadinIcon viewIcon, Class<? extends Component> component) {
        final var icon = viewIcon.create();
        icon.setSize("var(--lumo-icon-size-s)");
        icon.getStyle().set("margin", "auto");

        final var link = new RouterLink();
        link.add(icon);
        link.setRoute(component);
        link.setTabIndex(-1);

        return new Tab(link);
    }
}
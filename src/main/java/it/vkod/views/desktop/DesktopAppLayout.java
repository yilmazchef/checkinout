package it.vkod.views.desktop;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;

import it.vkod.services.flow.AuthenticationService;

public class DesktopAppLayout extends AppLayout {

    private final AuthenticationService authService;

    public DesktopAppLayout(AuthenticationService authService) {

        this.authService = authService;

        final var title = new H1("Intec Aanmeldingssysteem");
        title.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "var(--lumo-space-m) var(--lumo-space-l)");

        final var tabs = getTabs();

        final var viewTitle = new H2("Inchecken/Uitchecken");
        Paragraph viewContent = new Paragraph("Lees meer over het project..");

        final var content = new Div();
        content.add(viewTitle, viewContent);

        addToNavbar(title);
        addToNavbar(true, tabs);

        setContent(content);
    }

    private Tabs getTabs() {

        final var tabs = new Tabs();

        final var oUser = authService.get();

        tabs.add(

                new Tab(new Button("Aanmelden", onClick -> {
                    VaadinSession.getCurrent().getSession().invalidate();
                    try {
                        UI.getCurrent().navigate(DesktopLoginView.class);
                    } catch (NotFoundException notFoundEx) {
                        notifyException(notFoundEx).open();
                    }

                }))

        );

        tabs.add(createTab("In/Out", oUser.isPresent() ? DesktopCheckView.class : DesktopRemoteCheckinView.class));

        if (oUser.isPresent()) {

            final var user = oUser.get();

            if (user.getRoles().contains("TEACHER") || user.getRoles().contains("MANAGER")) {
                createTab("Manager", DesktopCheckSafeView.class);
            }

            if (user.getRoles().contains("ADMIN")) {
                tabs.add(createTab("Admin", DesktopAdminView.class));
            }

        }

        tabs.add(

                new Tab(new Button("In", onClick -> {
                    VaadinSession.getCurrent().getSession().invalidate();
                    try {
                        UI.getCurrent().navigate(DesktopLoginView.class, new RouteParameters("userID", "123"));
                    } catch (NotFoundException notFoundEx) {
                        notifyException(notFoundEx).open();
                    }

                }))

        );

        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL, TabsVariant.LUMO_EQUAL_WIDTH_TABS);
        return tabs;
    }

    private Notification notifyException(Exception exception) {
        final var trainingError = Notification.show(
                exception.getMessage(),
                4000, Position.BOTTOM_CENTER);
        trainingError.addThemeVariants(NotificationVariant.LUMO_ERROR);
        return trainingError;
    }

    private Tab createTab(String viewName, Class<? extends Component> clazz) {

        final var link = new RouterLink();
        link.add(viewName);
        link.setRoute(clazz);
        link.setTabIndex(-1);

        return new Tab(link);
    }
}

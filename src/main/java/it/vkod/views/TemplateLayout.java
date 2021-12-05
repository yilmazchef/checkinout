package it.vkod.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.RouterLink;

public class TemplateLayout extends AppLayout {

    public TemplateLayout() {
        H1 title = new H1("Intec Aanmeldingssysteem");
        title.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "var(--lumo-space-m) var(--lumo-space-l)");

        Tabs tabs = getTabs();

        H2 viewTitle = new H2("Inchecken/Uitchecken");
        Paragraph viewContent = new Paragraph("Lees meer over het project..");

        Div content = new Div();
        content.add(viewTitle, viewContent);

        addToNavbar(title);
        addToNavbar(true, tabs);

        setContent(content);
    }

    private Tabs getTabs() {
        Tabs tabs = new Tabs();
        tabs.add(
                createTab("Inloggen", LoginView.class),
                createTab("Inchecken", CheckinView.class),
                createTab("Uitchecken", CheckoutView.class),
                createTab("Inschrijving", RegisterView.class),
                createTab("Administrateur", AdminView.class),
                new Tab(new Anchor("logout", "Uitloggen"))
        );
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL, TabsVariant.LUMO_EQUAL_WIDTH_TABS);
        return tabs;
    }

    private Tab createTab(String viewName, Class<? extends Component> clazz) {

        RouterLink link = new RouterLink();
        link.add(viewName);
        link.setRoute(clazz);
        link.setTabIndex(-1);

        return new Tab(link);
    }
}

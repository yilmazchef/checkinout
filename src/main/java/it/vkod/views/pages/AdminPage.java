package it.vkod.views.pages;


import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.KeyDownEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinSession;
import it.vkod.services.flow.AdminService;
import it.vkod.services.flow.AuthenticationService;
import it.vkod.views.layouts.NotificationLayout;
import it.vkod.views.layouts.ResponsiveLayout;

import javax.annotation.security.RolesAllowed;

import static it.vkod.api.ExportController.*;

@PageTitle("Administratie")
@Route(value = "admin", layout = ResponsiveLayout.class)
@RolesAllowed({"ADMIN", "MANAGER", "LEADER", "TEACHER"})
public class AdminPage extends VerticalLayout {

    private final AuthenticationService authService;
    private final AdminService adminService;


    public AdminPage(AuthenticationService authService, AdminService adminService) {
        this.authService = authService;
        this.adminService = adminService;

        final var authUser = this.authService.get();

        if (authUser.isEmpty()) {
            NotificationLayout.error("The active user is not authorized!").open();
        } else {

            Button flushButton = new Button("Close session", event -> {
                VaadinSession.getCurrent().getSession().invalidate();
                UI.getCurrent().getPage().reload();
            });

            final var courseField = new TextField("Voer hier de course-code in: ");
            courseField.setRequired(true);
            courseField.setRequiredIndicatorVisible(true);
            courseField.addKeyDownListener(com.vaadin.flow.component.Key.ENTER,
                    (ComponentEventListener<KeyDownEvent>) keyDownEvent -> {

                        final var course = courseField.getValue().replaceAll(" ", "+");
                        final var route = RouteConfiguration.forSessionScope().getUrl(HomePage.class).concat(API);

                        final var pdfAnchor = new Anchor(route.concat(PDF).concat("/").concat(course), "PDF");
                        final var csvAnchor = new Anchor(route.concat(CSV).concat("/").concat(course), "CSV");
                        final var excelAnchor = new Anchor(route.concat(EXCEL).concat("/").concat(course), "EXCEL");
                        final var qrAnchor = new Anchor(route.concat(USERS).concat("/").concat(course), "Gebruikers-QR");

                        add(pdfAnchor, csvAnchor, excelAnchor, qrAnchor);

                    });


            add(courseField, flushButton);
        }


    }

}

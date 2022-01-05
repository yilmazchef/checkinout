package it.vkod.views.pages;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import it.vkod.models.entities.Role;
import it.vkod.models.entities.User;
import it.vkod.services.flow.AuthenticationService;
import it.vkod.views.layouts.CsvLayout;
import it.vkod.views.layouts.NotificationLayout;
import it.vkod.views.layouts.ResponsiveLayout;

import javax.annotation.security.PermitAll;
import java.util.Optional;

@PageTitle("Document-lezer")
@Route(value = "docs", layout = ResponsiveLayout.class)
@PermitAll
public class DocumentPage extends VerticalLayout {

    private final AuthenticationService authService;

    public DocumentPage(AuthenticationService authService) {

        this.authService = authService;


        setAlignItems(Alignment.CENTER);

        Optional<User> oUser = authService.get();
        if (oUser.isEmpty() || hasRole(oUser.get(), Role.MANAGER)) {
            NotificationLayout.error("The user is not authorized to view this page!").open();
        } else {
            add(new CsvLayout());
        }


    }

    private boolean hasRole(User user, Role role) {
        return user.getRoles().stream().anyMatch(userRole -> userRole == role);
    }
}

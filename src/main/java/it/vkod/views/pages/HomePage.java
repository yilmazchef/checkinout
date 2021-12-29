package it.vkod.views.pages;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import it.vkod.models.entities.User;
import it.vkod.models.entities.UserRole;
import it.vkod.services.flow.AdminService;
import it.vkod.services.flow.AuthenticationService;
import it.vkod.services.flow.CheckService;
import it.vkod.services.flow.UserService;
import it.vkod.views.layouts.GuestCheckinLayout;
import it.vkod.views.layouts.ResponsiveLayout;

import java.util.Optional;

@PageTitle("Intec Brussel Aanmeldingssysteem")
@Route(value = "", layout = ResponsiveLayout.class)
@AnonymousAllowed
public class HomePage extends VerticalLayout {

    private final UserService userService;
    private final CheckService checkService;
    private final AuthenticationService authService;
    private final AdminService adminService;

    public HomePage(UserService userService, CheckService checkService,
                    AuthenticationService authService, AdminService adminService) {

        this.userService = userService;
        this.checkService = checkService;
        this.authService = authService;
        this.adminService = adminService;

        setAlignItems(Alignment.CENTER);

        Optional<User> oUser = authService.get();
        if (oUser.isEmpty()) {
            add(new GuestCheckinLayout(this.userService, this.checkService));
        } else {

            if (hasRole(oUser.get(), UserRole.TEACHER)) {
                add(new CheckinPage(this.authService, this.userService, this.checkService));
            } else if (hasRole(oUser.get(), UserRole.MANAGER)) {
                add(new AdminPage(this.authService, this.adminService));
            }
        }


    }

    private boolean hasRole(User oUser, UserRole role) {
        return oUser.getRoles().stream().anyMatch(userRole -> userRole == role);
    }
}

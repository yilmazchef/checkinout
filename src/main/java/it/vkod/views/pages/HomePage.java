package it.vkod.views.pages;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import it.vkod.models.entities.User;
import it.vkod.models.entities.Role;
import it.vkod.services.flow.*;
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
    private final EmailService emailService;

    public HomePage(UserService userService, CheckService checkService,
                    AuthenticationService authService, AdminService adminService, EmailService emailService) {

        this.userService = userService;
        this.checkService = checkService;
        this.authService = authService;
        this.adminService = adminService;
        this.emailService = emailService;

        setAlignItems(Alignment.CENTER);

        Optional<User> oUser = authService.get();
        if (oUser.isEmpty()) {
            add(new GuestCheckinLayout(this.userService, this.checkService, this.emailService));
        } else {

            if (hasRole(oUser.get(), Role.TEACHER)) {
                add(new CheckinPage(this.authService, this.userService, this.checkService, this.emailService));
            } else if (hasRole(oUser.get(), Role.MANAGER)) {
                add(new AdminPage(this.authService, this.adminService));
            }
        }


    }

    private boolean hasRole(User user, Role role) {
        return user.getRoles().stream().anyMatch(userRole -> userRole == role);
    }
}

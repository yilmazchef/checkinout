package it.vkod.views.pages;


import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import it.vkod.models.entities.Event;
import it.vkod.models.entities.Role;
import it.vkod.services.flow.AuthenticationService;
import it.vkod.services.flow.CheckService;
import it.vkod.services.flow.EmailService;
import it.vkod.services.flow.UserService;
import it.vkod.views.layouts.*;

import javax.annotation.security.PermitAll;

@PageTitle("Inchecken")
@Route(value = "in", layout = ResponsiveLayout.class)
@RouteAlias(value = "checkin", layout = ResponsiveLayout.class)
@PermitAll
@PreserveOnRefresh
public class CheckinPage extends VerticalLayout {

    private final AuthenticationService authService;
    private final UserService userService;
    private final CheckService checkService;
    private final EmailService emailService;

    public CheckinPage(final AuthenticationService authService,
                       final UserService userService, final CheckService checkService,
                       final EmailService emailService) {

        this.authService = authService;
        this.userService = userService;
        this.checkService = checkService;
        this.emailService = emailService;

        authService.get().ifPresentOrElse(user -> {
                    if (user.getRoles().stream().anyMatch(role -> role == Role.STUDENT)) {
                        add(new CheckEventLayout(this.checkService, user, Event.REMOTE_IN));
                    } else if (user.getRoles().stream().anyMatch(role ->
                            (role == Role.TEACHER) || (role == Role.MANAGER) || (role == Role.ADMIN))) {
                        add(new CheckEventLayout(this.checkService, user, Event.PHYSICAL_IN));
                    } else {
                        add(new GuestCheckinLayout(this.userService, this.checkService, this.emailService));
                    }
                },
                () -> NotificationLayout.error("Deze gebruiker heeft GEEN toegang.").open());


    }


}

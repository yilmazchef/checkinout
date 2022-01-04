package it.vkod.views.pages;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import it.vkod.models.entities.Role;
import it.vkod.services.flow.AuthenticationService;
import it.vkod.services.flow.CheckService;
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

    private final PhysicalCheckinLayout physicalCheckinLayout;
    private final RemoteCheckinLayout remoteCheckinLayout;
    private final CheckTypeDialogLayout type;

    public CheckinPage(final AuthenticationService authService, final UserService userService, final CheckService checkService) {

        this.authService = authService;
        this.userService = userService;
        this.checkService = checkService;

        physicalCheckinLayout = new PhysicalCheckinLayout(authService, checkService);
        remoteCheckinLayout = new RemoteCheckinLayout(authService, checkService);
        type = new CheckTypeDialogLayout();

        authService.get().ifPresentOrElse(user -> {
                    if (user.getRoles().stream().anyMatch(role -> role == Role.STUDENT)) {
                        add(remoteCheckinLayout);
                    } else if (user.getRoles().stream().anyMatch(role ->
                            (role == Role.TEACHER) || (role == Role.MANAGER) || (role == Role.ADMIN))) {
                        add(physicalCheckinLayout);
                    } else {
                        UI.getCurrent().navigate(RegisterPage.class);
                    }
                },
                () -> NotificationLayout.error("Deze gebruiker heeft GEEN toegang.").open());


    }


}

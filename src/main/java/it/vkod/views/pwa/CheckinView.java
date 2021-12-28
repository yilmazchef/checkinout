package it.vkod.views.pwa;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import it.vkod.models.entities.UserRole;
import it.vkod.services.flow.AuthenticationService;
import it.vkod.services.flow.CheckService;
import it.vkod.services.flow.UserService;
import it.vkod.views.components.NotificationUtils;

import javax.annotation.security.PermitAll;

@PageTitle("Inchecken")
@Route(value = "in", layout = AppLayoutBottomNavbar.class)
@RouteAlias(value = "checkin", layout = AppLayoutBottomNavbar.class)
@PermitAll
@PreserveOnRefresh
public class CheckinView extends VerticalLayout {

    private final AuthenticationService authService;
    private final UserService userService;
    private final CheckService checkService;

    private final PhysicalCheckinLayout physicalCheckinLayout;
    private final RemoteCheckinLayout remoteCheckinLayout;

    public CheckinView(final AuthenticationService authService, final UserService userService, final CheckService checkService) {

        this.authService = authService;
        this.userService = userService;
        this.checkService = checkService;

        physicalCheckinLayout = new PhysicalCheckinLayout(authService, userService, checkService);
        remoteCheckinLayout = new RemoteCheckinLayout(authService, userService, checkService);

        authService.get().ifPresentOrElse(user -> {
                    if (user.getRoles().stream().anyMatch(role -> role == UserRole.STUDENT)) {
                        add(remoteCheckinLayout);
                    } else if (user.getRoles().stream().anyMatch(role -> role == UserRole.TEACHER)) {
                        add(physicalCheckinLayout);
                    } else {
                        UI.getCurrent().navigate(RegisterView.class);
                    }
                },
                () -> {
                    NotificationUtils.error("Deze gebruiker heeft GEEN toegang.").open();

                });


    }


}

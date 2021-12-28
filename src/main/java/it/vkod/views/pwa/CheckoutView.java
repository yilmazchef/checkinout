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

@PageTitle("Uitchecken")
@Route(value = "out", layout = AppLayoutBottomNavbar.class)
@RouteAlias(value = "checkout", layout = AppLayoutBottomNavbar.class)
@PermitAll
@PreserveOnRefresh
public class CheckoutView extends VerticalLayout {

    private final AuthenticationService authService;
    private final UserService userService;
    private final CheckService checkService;

    private final PhysicalCheckoutLayout physicalCheckoutLayout;
    private final RemoteCheckoutLayout remoteCheckoutLayout;

    public CheckoutView(final AuthenticationService authService, final UserService userService, final CheckService checkService) {

        this.authService = authService;
        this.userService = userService;
        this.checkService = checkService;

        physicalCheckoutLayout = new PhysicalCheckoutLayout(authService, userService, checkService);
        remoteCheckoutLayout = new RemoteCheckoutLayout(authService, userService, checkService);

        authService.get().ifPresentOrElse(user -> {
                    if (user.getRoles().stream().anyMatch(role -> role == UserRole.STUDENT)) {
                        add(remoteCheckoutLayout);
                        getUI().ifPresent(ui -> ui.getPage().setTitle("Remote uitchecken"));
                    } else if (user.getRoles().stream().anyMatch(role -> role == UserRole.TEACHER)) {
                        add(physicalCheckoutLayout);
                    } else {
                        UI.getCurrent().navigate(RegisterView.class);
                    }
                },
                () -> {
                    NotificationUtils.error("Deze gebruiker heeft GEEN toegang.").open();

                });


    }


}

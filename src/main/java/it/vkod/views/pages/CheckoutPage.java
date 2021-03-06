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
import it.vkod.views.layouts.NotificationLayout;
import it.vkod.views.layouts.PhysicalCheckoutLayout;
import it.vkod.views.layouts.RemoteCheckoutLayout;
import it.vkod.views.layouts.ResponsiveLayout;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.security.PermitAll;
import java.text.MessageFormat;
import java.util.stream.Collectors;

// LOMBOK
@Slf4j

// VAADIN
@PageTitle("Uitchecken")
@Route(value = "out", layout = ResponsiveLayout.class)
@RouteAlias(value = "checkout", layout = ResponsiveLayout.class)
@PermitAll
@PreserveOnRefresh
public class CheckoutPage extends VerticalLayout {

    private final AuthenticationService authService;
    private final UserService userService;
    private final CheckService checkService;

    private final PhysicalCheckoutLayout physicalCheckoutLayout;
    private final RemoteCheckoutLayout remoteCheckoutLayout;

    public CheckoutPage(final AuthenticationService authService, final UserService userService, final CheckService checkService) {

        this.authService = authService;
        this.userService = userService;
        this.checkService = checkService;

        physicalCheckoutLayout = new PhysicalCheckoutLayout(authService, checkService);
        remoteCheckoutLayout = new RemoteCheckoutLayout(authService, checkService);

        authService.get().ifPresentOrElse(user -> {

                    log.info(
                            MessageFormat.format(
                                    "User {0} with role {1} is trying to checkout",
                                    user.getUsername(),
                                    user.getRoles().stream().map(Role::name).collect(Collectors.joining(", "))
                            )
                    );

                    if (user.getRoles().stream().anyMatch(role -> role == Role.STUDENT)) {

                        add(remoteCheckoutLayout);
                        getUI().ifPresent(ui -> ui.getPage().setTitle("Remote uitchecken"));
                    } else if (user.getRoles().stream().anyMatch(role ->
                            (role == Role.TEACHER) || (role == Role.MANAGER) || (role == Role.ADMIN))) {
                        add(physicalCheckoutLayout);
                    } else {
                        UI.getCurrent().navigate(RegisterPage.class);
                    }
                },
                () -> NotificationLayout.error("Deze gebruiker heeft GEEN toegang.").open());


    }


}

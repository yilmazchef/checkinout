package it.vkod.views.pages;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import it.vkod.models.entities.Role;
import it.vkod.services.flow.AuthenticationService;
import it.vkod.services.flow.EmailService;
import it.vkod.services.flow.UserService;
import it.vkod.views.layouts.NotificationLayout;
import it.vkod.views.layouts.RegisterLayout;
import it.vkod.views.layouts.ResponsiveLayout;

@PageTitle("Inschrijving")
@Route(value = "register", layout = ResponsiveLayout.class)
@AnonymousAllowed
public class RegisterPage extends VerticalLayout {

    private final AuthenticationService authService;
    private final UserService userService;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;

    private final Dialog dialog;

    public RegisterPage(final AuthenticationService authService, final UserService userService,
            final EmailService emailService, final BCryptPasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.userService = userService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;

        this.dialog = new Dialog();

        if (this.authService.hasRole(Role.GUEST) || this.authService.get().isEmpty()) {
            add(new RegisterLayout(this.userService, this.emailService, this.passwordEncoder));

        } else {

            final var flush = new Button("Create another account", onClick -> {
                VaadinSession.getCurrent().getSession().invalidate();

            });

            final var close = new Button("Close");

            dialog.add(flush, close);
            dialog.open();

        }

    }

}

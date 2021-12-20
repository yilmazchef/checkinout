package it.vkod.views.mobile;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;

import it.vkod.models.http.CheckType;
import it.vkod.models.http.TrainingCode;
import it.vkod.services.flow.AuthenticationService;
import it.vkod.services.flow.SessionService;
import lombok.Getter;

public class MobileTemplateLayout extends AppLayout {

    private final AuthenticationService authService;
    private final SessionService sessionService;

    private final Tabs tabs = new Tabs();
    private final Tab inTab = new Tab();
    private final Tab outTab = new Tab();
    private final Tab managerTab = new Tab();
    private final Tab adminTab = new Tab();

    public MobileTemplateLayout(AuthenticationService authService, SessionService sessionService) {

        this.authService = authService;
        this.sessionService = sessionService;

        final var oUser = this.authService.get();
        final var inButton = new Button(VaadinIcon.SIGN_IN_ALT.create());
        final var managerButton = new Button(VaadinIcon.ACADEMY_CAP.create());
        final var adminButton = new Button(VaadinIcon.BRIEFCASE.create());
        final var outButton = new Button(VaadinIcon.SIGN_OUT_ALT.create());

        if (oUser.isPresent()) {

            final var user = oUser.get();

            if (user.getRoles().contains("TEACHER") || user.getRoles().contains("MANAGER")
                    || user.getRoles().contains("ADMIN")) {
                inButton.setSizeFull();

                inButton.addClickListener(onClick -> {
                    try {
                        this.sessionService.setCheckType("IN");
                        this.sessionService.setTrainingCode(user.getCurrentTraining());
                        inButton.getUI().ifPresent(ui -> ui.navigate(MobileCheckView.class));
                    } catch (NotFoundException notFoundEx) {
                        notifyException(notFoundEx).open();
                    }

                });
                inTab.getStyle().set("background-color", "green");
                inTab.add(inButton);
                tabs.add(inTab);
            }

            if (user.getRoles().contains("MANAGER")) {
                managerButton.setSizeFull();

                managerButton.addClickListener(onClick -> {
                    try {
                        managerButton.getUI().ifPresent(ui -> ui.navigate(MobileCheckSafeView.class));
                    } catch (NotFoundException notFoundEx) {
                        notifyException(notFoundEx).open();
                    }

                });
                managerTab.add(managerButton);
                tabs.add(managerTab);
            }

            if (user.getRoles().contains("ADMIN")) {
                adminButton.setSizeFull();

                adminButton.addClickListener(onClick -> {
                    try {
                        adminButton.getUI().ifPresent(ui -> ui.navigate(MobileAdminView.class));
                    } catch (NotFoundException notFoundEx) {
                        notifyException(notFoundEx).open();
                    }

                });
                adminTab.add(adminButton);
                tabs.add(adminTab);
            }

            if (user.getRoles().contains("TEACHER") || user.getRoles().contains("MANAGER")
                    || user.getRoles().contains("ADMIN")) {
                outButton.setSizeFull();
                outButton.addClickListener(onClick -> {
                    try {
                        this.sessionService.setCheckType("OUT");
                        this.sessionService.setTrainingCode(user.getCurrentTraining());
                        outButton.getUI().ifPresent(ui -> ui.navigate(MobileCheckView.class));
                    } catch (NotFoundException notFoundEx) {
                        notifyException(notFoundEx).open();
                    }

                });
                outTab.getStyle().set("background-color", "red");
                outTab.add(outButton);
                tabs.add(outTab);
            }

        }

        final var logoutButton = new Button(VaadinIcon.CLOSE.create());
        logoutButton.getStyle()
                .set("position", "absolute")
                .set("right", "0").set("top", "0");
        logoutButton.addClickListener(onClick -> {
            VaadinSession.getCurrent().getSession().invalidate();
            this.sessionService.setTrainingCode("");
            this.sessionService.setCheckType("");
            try {
                logoutButton.getUI().ifPresent(ui -> ui.navigate("logout"));
            } catch (NotFoundException notFoundEx) {
                notifyException(notFoundEx).open();
            }

        });

        tabs.add(logoutButton);

        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL, TabsVariant.LUMO_EQUAL_WIDTH_TABS);
        addToNavbar(true, tabs);

    }

    private Notification notifyException(Exception exception) {
        final var trainingError = Notification.show(
                exception.getMessage(),
                4000, Position.BOTTOM_CENTER);
        trainingError.addThemeVariants(NotificationVariant.LUMO_ERROR);
        return trainingError;
    }
}

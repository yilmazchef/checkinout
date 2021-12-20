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

import it.vkod.models.http.CheckType;
import it.vkod.models.http.TrainingCode;
import it.vkod.services.flow.AuthenticationService;

public class MobileTemplateLayout extends AppLayout {

    private final AuthenticationService authService;

    public MobileTemplateLayout(AuthenticationService authService) {

        this.authService = authService;

        final var tabs = new Tabs();

        final var oUser = this.authService.get();
        final var inButton = new Button(VaadinIcon.SIGN_IN_ALT.create());
        final var managerButton = new Button(VaadinIcon.ACADEMY_CAP.create());
        final var adminButton = new Button(VaadinIcon.BRIEFCASE.create());
        final var outButton = new Button(VaadinIcon.SIGN_OUT_ALT.create());

        if (oUser.isPresent()) {

            final var user = oUser.get();

            if (user.getRoles().contains("TEACHER") || user.getRoles().contains("MANAGER")
                    || user.getRoles().contains("ADMIN")) {
                inButton.addClickListener(onClick -> {
                    try {
                        RouteParam typeParam = new RouteParam(CheckType.IN.getName(), CheckType.IN.getValue());
                        RouteParam trainingParam = new RouteParam(TrainingCode.QUERY.getName(),
                                user.getCurrentTraining());

                        inButton.getUI().ifPresent(ui -> ui.navigate(
                                MobileCheckView.class,
                                new RouteParameters(typeParam, trainingParam)));
                    } catch (NotFoundException notFoundEx) {
                        notifyException(notFoundEx).open();
                    }

                });
                Tab inTab = new Tab(inButton);
                inTab.getStyle().set("background-color", "green");
                tabs.add(inTab);
            }

            if (user.getRoles().contains("MANAGER")) {
                managerButton.addClickListener(onClick -> {
                    try {
                        RouteParam typeParam = new RouteParam(CheckType.IN.getName(), CheckType.IN.getValue());
                        RouteParam trainingParam = new RouteParam(TrainingCode.QUERY.getName(),
                                user.getCurrentTraining());

                        managerButton.getUI().ifPresent(ui -> ui.navigate(
                                MobileCheckSafeView.class,
                                new RouteParameters(typeParam, trainingParam)));

                    } catch (NotFoundException notFoundEx) {
                        notifyException(notFoundEx).open();
                    }

                });
                tabs.add(new Tab(managerButton));
            }

            if (user.getRoles().contains("ADMIN")) {
                adminButton.addClickListener(onClick -> {
                    try {
                        adminButton.getUI().ifPresent(ui -> ui.navigate(MobileAdminView.class));
                    } catch (NotFoundException notFoundEx) {
                        notifyException(notFoundEx).open();
                    }

                });
                tabs.add(new Tab(adminButton));
            }

            if (user.getRoles().contains("TEACHER") || user.getRoles().contains("MANAGER")
                    || user.getRoles().contains("ADMIN")) {
                outButton.addClickListener(onClick -> {
                    try {
                        RouteParam typeParam = new RouteParam(CheckType.IN.getName(), CheckType.IN.getValue());
                        RouteParam trainingParam = new RouteParam(TrainingCode.QUERY.getName(),
                                user.getCurrentTraining());

                        outButton.getUI().ifPresent(ui -> ui.navigate(
                                MobileCheckView.class,
                                new RouteParameters(typeParam, trainingParam)));

                    } catch (NotFoundException notFoundEx) {
                        notifyException(notFoundEx).open();
                    }

                });
                Tab outTab = new Tab(outButton);
                outTab.getStyle().set("background-color", "red");
                tabs.add(outTab);
            }

        }

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

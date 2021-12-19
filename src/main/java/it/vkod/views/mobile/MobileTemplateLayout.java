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
import it.vkod.views.AdminView;
import it.vkod.views.CheckSafeView;

public class MobileTemplateLayout extends AppLayout {

    private final AuthenticationService authService;

    public MobileTemplateLayout(AuthenticationService authService) {

        this.authService = authService;

        final var tabs = new Tabs();

        final var oUser = this.authService.get();

        if (oUser.isPresent()) {

            final var user = oUser.get();

            if (user.getRoles().contains("TEACHER") || user.getRoles().contains("MANAGER")
                    || user.getRoles().contains("ADMIN"))
                tabs.add(new Tab(new Button(VaadinIcon.SIGN_IN_ALT.create(), onClick -> {
                    try {
                        RouteParam typeParam = new RouteParam(CheckType.IN.getName(), CheckType.IN.getValue());
                        RouteParam trainingParam = new RouteParam(TrainingCode.QUERY.getName(),
                                user.getCurrentTraining());
                        UI.getCurrent().navigate(MobileCheckView.class,
                                new RouteParameters(typeParam, trainingParam));
                    } catch (NotFoundException notFoundEx) {
                        notifyException(notFoundEx).open();
                    }

                })));

            if (user.getRoles().contains("MANAGER"))
                tabs.add(new Tab(new Button(VaadinIcon.ACADEMY_CAP.create(), onClick -> {
                    try {
                        UI.getCurrent().navigate(CheckSafeView.class);
                    } catch (NotFoundException notFoundEx) {
                        notifyException(notFoundEx).open();
                    }

                })));

            if (user.getRoles().contains("ADMIN")) {
                tabs.add(new Tab(new Button(VaadinIcon.BRIEFCASE.create(), onClick -> {
                    try {
                        UI.getCurrent().navigate(AdminView.class);
                    } catch (NotFoundException notFoundEx) {
                        notifyException(notFoundEx).open();
                    }

                })));
            }

            if (user.getRoles().contains("TEACHER") || user.getRoles().contains("MANAGER")
                    || user.getRoles().contains("ADMIN"))
                tabs.add(new Tab(new Button(VaadinIcon.SIGN_OUT_ALT.create(), onClick -> {
                    try {
                        RouteParam typeParam = new RouteParam(CheckType.OUT.getName(), CheckType.OUT.getValue());
                        RouteParam trainingParam = new RouteParam(TrainingCode.QUERY.getName(),
                                user.getCurrentTraining());
                        UI.getCurrent().navigate(MobileCheckView.class,
                                new RouteParameters(typeParam, trainingParam));
                    } catch (NotFoundException notFoundEx) {
                        notifyException(notFoundEx).open();
                    }

                })));

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

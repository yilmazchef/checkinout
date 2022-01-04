package it.vkod.views.pages;


import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import it.vkod.models.entities.Check;
import it.vkod.models.entities.Course;
import it.vkod.models.entities.Role;
import it.vkod.services.flow.AuthenticationService;
import it.vkod.services.flow.CheckService;
import it.vkod.views.layouts.CheckedUserLayout;
import it.vkod.views.layouts.ResponsiveLayout;

import javax.annotation.security.PermitAll;
import java.util.LinkedList;
import java.util.List;

import static it.vkod.models.entities.Event.*;

@PageTitle("Inchecken-detail")
@Route(value = "in/details", layout = ResponsiveLayout.class)
@RouteAlias(value = "checkin/details", layout = ResponsiveLayout.class)
@PermitAll
public class CheckinDetailsPage extends VerticalLayout {

    private final AuthenticationService authService;
    private final CheckService checkService;

    private final VerticalLayout events;
    private final Select<Course> course;


    public CheckinDetailsPage(final AuthenticationService authService, final CheckService checkService) {

        this.authService = authService;
        this.checkService = checkService;

        initCheckinLayoutStyle();

        events = initEventsLayout();
        course = initCourseLayout();

        authService.get().ifPresent(user -> {

            course.setValue(user.getCourse());
            final var authorized = (user.getRoles().stream().anyMatch(role -> role == Role.MANAGER || role == Role.ADMIN));

            if (!authorized) {
                course.setReadOnly(true);
                course.setVisible(false);
            }

            final var checks = new Object() {
                List<Check> data = new LinkedList<>();
            };

            checks.data = checkService.fetchAllByCourse(course.getValue(), PHYSICAL_IN, REMOTE_IN, GUEST_IN);

            for (final Check check : checks.data) {
                final var checkLayout = new CheckedUserLayout(check);
                events.add(checkLayout);
            }

            course.addValueChangeListener(onCourseChange -> {
                events.removeAll();

                checks.data = checkService.fetchAllByCourse(course.getValue(), PHYSICAL_IN, REMOTE_IN, GUEST_IN);

                for (final Check check : checks.data) {
                    final var checkLayout = new CheckedUserLayout(check);
                    events.add(checkLayout);
                }
            });


        });


        add(course, events);

    }

    private Select<Course> initCourseLayout() {
        final var layout = new Select<>(Course.values());
        layout.setRequiredIndicatorVisible(true);

        layout.setEmptySelectionAllowed(false);
        layout.getStyle()
                .set("width", "200px")
                .set("max-width", "50vw")
                .set("height", "25px")
                .set("max-height", "50px");

        return layout;
    }

    private VerticalLayout initEventsLayout() {
        final var layout = new VerticalLayout();
        layout.getStyle()
                .set("width", "100vw")
                .set("height", "80vh");

        return layout;
    }

    private void initCheckinLayoutStyle() {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setMargin(false);
        setPadding(false);
        setSpacing(false);
        setSizeFull();
    }


}

package it.vkod.views.layouts;


import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.server.VaadinSession;
import com.wontlost.zxing.Constants;
import com.wontlost.zxing.ZXingVaadinReader;
import it.vkod.models.entities.Check;
import it.vkod.models.entities.Course;
import it.vkod.models.entities.Role;
import it.vkod.services.flow.AuthenticationService;
import it.vkod.services.flow.CheckService;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.elmot.flow.sensors.GeoLocation;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import static it.vkod.models.entities.Event.PHYSICAL_IN;
import static it.vkod.views.layouts.NotificationLayout.error;
import static it.vkod.views.layouts.NotificationLayout.success;

// LOMBOK
@Slf4j
public class PhysicalCheckoutLayout extends VerticalLayout {

    private final AuthenticationService authService;
    private final CheckService checkService;

    private final GeoLocation location;
    private final ZXingVaadinReader scanner;
    private final Select<Course> course;


    public PhysicalCheckoutLayout(final AuthenticationService authService, final CheckService checkService) {

        this.authService = authService;
        this.checkService = checkService;

        initCheckoutLayoutStyle();

        location = initLocationLayout();
        scanner = initScannerLayout();
        course = initCourseLayout();

        authService.get().ifPresent(user -> {

            final List<Check> checks = checkService.fetchAllByCourse(course.getValue(), PHYSICAL_IN);

            log.info(
                    MessageFormat.format(
                            "Found {0} physical checks for course {1}",
                            checks.size(),
                            user.getCourse()
                    )
            );

            course.setValue(user.getCourse());
            final var authorized = (user.getRoles().stream().anyMatch(role -> role == Role.MANAGER || role == Role.ADMIN));

            if (!authorized) {
                course.setReadOnly(true);
            }

            scanner.addValueChangeListener(onScan -> {

                log.info(
                        MessageFormat.format(
                                "Scanning physical checkout with code {0}",
                                onScan.getValue()
                        )
                );

                final Optional<Check> checkRequest = checkService.checkout(
                        VaadinSession.getCurrent().getSession().getId(),
                        user.getCourse(), user.getUsername(), onScan.getValue(),
                        location.getValue().getLatitude(),
                        location.getValue().getLongitude(),
                        false, false
                );

                log.info(
                        MessageFormat.format(
                                "Checkout request {0}",
                                checkRequest.isPresent() ? "succeeded" : "failed"
                        )
                );

                if (!checks.contains(checkRequest) && checkRequest.isPresent() && !checkRequest.get().isDuplicated()) {
                    success(checkRequest.get().getAttendee().toString() + ": " + checkRequest.get().getEvent().name()).open();
                } else {
                    error("User has checked in before. Checkout process is rolled-back.").open();
                }

            });
        });

        add(course, location, scanner);

    }

    private Select<Course> initCourseLayout() {
        final var layout = new Select<>(Course.values());
        layout.setRequiredIndicatorVisible(true);

        layout.setEmptySelectionAllowed(false);
        layout.getStyle()
                .set("position", "absolute")
                .set("margin-top", "0")
                .set("margin-left", "0")
                .set("padding", "0")
                .set("width", "200px")
                .set("max-width", "50vw")
                .set("height", "25px")
                .set("max-height", "50px")
                .set("z-index", "0");

        return layout;
    }

    private HorizontalLayout initEventsLayout() {
        final HorizontalLayout layout;
        layout = new HorizontalLayout();
        layout.getStyle()
                .set("position", "absolute")
                .set("margin-bottom", "15vh")
                .set("margin-left", "0")
                .set("padding", "0")
                .set("width", "100vw")
                .set("height", "25px")
                .set("max-height", "50px")
                .set("z-index", "0");

        return layout;
    }

    private GeoLocation initLocationLayout() {
        final GeoLocation layout;
        layout = new GeoLocation();
        layout.setWatch(true);
        layout.setHighAccuracy(true);
        layout.setTimeout(100000);
        layout.setMaxAge(200000);
        return layout;
    }

    private void initCheckoutLayoutStyle() {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setMargin(false);
        setPadding(false);
        setSpacing(false);
        setSizeFull();
    }

    private ZXingVaadinReader initScannerLayout() {
        final var style = "position: absolute;" +
                " top: 0;" +
                " left: 0;" +
                " display: block;" +
                " width: 100%;" +
                " height: 100%;" +
                " object-fit: cover;" +
                " z-index: -1;";
        final var type = "video";
        final var layout = new ZXingVaadinReader();
        layout.setFrom(Constants.From.camera);
        layout.setId(type); // id needs to be 'video' if From.camera.
        layout.setStyle(style);
        return layout;
    }


}

package it.vkod.views.layouts;


import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import com.wontlost.zxing.Constants;
import com.wontlost.zxing.ZXingVaadinReader;
import it.vkod.models.entities.Check;
import it.vkod.services.flow.AuthenticationService;
import it.vkod.services.flow.CheckService;
import org.vaadin.elmot.flow.sensors.GeoLocation;

import static it.vkod.models.entities.Event.*;
import static it.vkod.views.layouts.NotificationLayout.*;

public class RemoteCheckoutLayout extends VerticalLayout {

    private final AuthenticationService authService;
    private final CheckService checkService;

    private final HorizontalLayout events;
    private final GeoLocation location;
    private final ZXingVaadinReader scanner;


    public RemoteCheckoutLayout(final AuthenticationService authService, final CheckService checkService) {

        this.authService = authService;
        this.checkService = checkService;

        initCheckinLayoutStyle();

        location = initLocationLayout();
        events = initEventsLayout();
        scanner = initScannerLayout();

        authService.get().ifPresent(user -> {

            final var checks = checkService.fetchAllByCourse(user.getCourse(), REMOTE_OUT);

            for (final Check check : checks) {
                final var checkLayout = new CheckedUserLayout(check);
                events.add(checkLayout);
            }

            scanner.addValueChangeListener(onScan -> {

                final var checkRequest = checkService.checkout(
                        VaadinSession.getCurrent().getSession().getId(),
                        user.getCourse(), onScan.getValue(), user.getUsername(),
                        location.getValue().getLatitude(),
                        location.getValue().getLongitude(),
                        false, false
                );

                if (!checks.contains(checkRequest) && !checkRequest.isDuplicated()) {
                    final var checkLayout = new CheckedUserLayout(checkRequest);
                    events.add(checkLayout);
                    success(checkRequest.getAttendee().toString() + ": " + checkRequest.getEvent().name()).open();
                } else {
                    error("User has already checked out before. Checkout process is rolled-back.").open();
                }

            });
        });

        add(location, events, scanner);

    }

    private HorizontalLayout initEventsLayout() {
        final HorizontalLayout layout;
        layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setMargin(false);
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setHeight("10vh");
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

    private void initCheckinLayoutStyle() {
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
                " object-fit: cover;";
        final var type = "video";
        final var layout = new ZXingVaadinReader();
        layout.setFrom(Constants.From.camera);
        layout.setId(type); // id needs to be 'video' if From.camera.
        layout.setStyle(style);
        return layout;
    }


}

package it.vkod.views.layouts;


import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import com.wontlost.zxing.Constants;
import com.wontlost.zxing.ZXingVaadinReader;
import it.vkod.models.entities.Check;
import it.vkod.services.flow.AuthenticationService;
import it.vkod.services.flow.CheckService;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.elmot.flow.sensors.GeoLocation;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import static it.vkod.models.entities.Event.REMOTE_OUT;
import static it.vkod.views.layouts.NotificationLayout.error;
import static it.vkod.views.layouts.NotificationLayout.success;

// LOMBOK
@Slf4j
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

            final List<Check> checks = checkService.fetchAllByCourse(user.getCourse(), REMOTE_OUT);

            log.info(
                    MessageFormat.format(
                            "Found {0} remote checks for course {1}",
                            checks.size(),
                            user.getCourse()
                    )
            );

            for (final Check check : checks) {
                final var checkLayout = new CheckedUserLayout(check);
                events.add(checkLayout);
            }

            scanner.addValueChangeListener(onScan -> {

                log.info(
                        MessageFormat.format(
                                "Scanning remote checkout with code {0}",
                                onScan.getValue()
                        )
                );

                final Optional<Check> checkRequest = checkService.checkout(
                        VaadinSession.getCurrent().getSession().getId(),
                        user.getCourse(), onScan.getValue(), user.getUsername(),
                        location.getValue().getLatitude(),
                        location.getValue().getLongitude(),
                        false, false
                );

                log.info(
                        MessageFormat.format(
                                "Checkout request for code {0} returned {1}",
                                onScan.getValue(),
                                checkRequest
                        )
                );

                if (!checks.contains(checkRequest) && checkRequest.isPresent() && !checkRequest.get().isDuplicated()) {

                    log.info(
                            MessageFormat.format(
                                    "Checkout request for code {0} is valid",
                                    onScan.getValue()
                            )
                    );

                    final var checkLayout = new CheckedUserLayout(checkRequest.get());
                    events.add(checkLayout);
                    success(checkRequest.get().getAttendee().toString() + ": " + checkRequest.get().getEvent().name()).open();
                } else {

                    String message = MessageFormat.format(
                            "Checkout request for code {0} is invalid",
                            onScan.getValue()
                    );
                    log.info(
                            message
                    );

                    error(message).open();
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

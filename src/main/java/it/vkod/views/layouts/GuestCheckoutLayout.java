package it.vkod.views.layouts;


import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import com.wontlost.zxing.Constants;
import com.wontlost.zxing.ZXingVaadinReader;
import it.vkod.models.entities.Check;
import it.vkod.models.entities.Event;
import it.vkod.models.entities.User;
import it.vkod.services.flow.AuthenticationService;
import it.vkod.services.flow.CheckService;
import it.vkod.services.flow.UserService;
import org.vaadin.elmot.flow.sensors.GeoLocation;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static it.vkod.models.entities.Event.REMOTE_OUT;

public class GuestCheckoutLayout extends VerticalLayout {

    private final AuthenticationService authService;
    private final UserService userService;
    private final CheckService checkService;

    private final HorizontalLayout events;
    private final GeoLocation location;
    private final ZXingVaadinReader scanner;


    public GuestCheckoutLayout(final AuthenticationService authService, final UserService userService, final CheckService checkService) {

        this.authService = authService;
        this.userService = userService;
        this.checkService = checkService;

        initCheckinLayoutStyle();

        location = initLocationLayout();
        events = initEventsLayout();
        scanner = initScannerLayout();

        add(location, events, scanner);

        authService.get().ifPresent(this::initCheckinLayout);

    }

    private HorizontalLayout initEventsLayout() {
        final var layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setMargin(false);
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setHeight("10vh");
        return layout;
    }

    private GeoLocation initLocationLayout() {
        final var layout = new GeoLocation();
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


    private void initCheckinLayout(User user) {

        final List<Check> checks = this.checkService.fetchAllByCourse(user.getCourse(), REMOTE_OUT);

        for (final Check check : checks) {
            final var checkLayout = new CheckedUserLayout(check);
            events.add(checkLayout);
        }

        scanner.addValueChangeListener(onScan -> {

            final Optional<Check> newCheck = this.checkService.createOrUpdate(
                    check(userService.getByUsername(onScan.getValue()), user, location, REMOTE_OUT));

            if (newCheck.isPresent() && !checks.contains(newCheck.get())) {
                final var checkLayout = new CheckedUserLayout(newCheck.get());
                events.add(checkLayout);
                NotificationLayout.success(newCheck.get().getAttendee().toString() + ": " + newCheck.get().getEvent().name()).open();
            } else {
                NotificationLayout.error(onScan.getValue() + ": " + " not acceptable.").open();
            }

        });

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


    public Check check(User organizer, User attendee, GeoLocation location, Event type) {

        return new Check()
                .setOrganizer(organizer)
                .setAttendee(attendee)
                .setCourse(attendee.getCourse())
                .setLat(location.getValue().getLatitude())
                .setLon(location.getValue().getLongitude())
                .setValidation(new Random().nextInt(8999) + 1000)
                .setSession(VaadinSession.getCurrent().getSession().getId())
                .setEvent(type);
    }

}

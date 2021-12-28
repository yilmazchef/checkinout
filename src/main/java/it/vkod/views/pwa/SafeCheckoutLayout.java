package it.vkod.views.pwa;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.VaadinSession;
import com.wontlost.zxing.Constants;
import com.wontlost.zxing.ZXingVaadinReader;
import it.vkod.models.entities.Check;
import it.vkod.models.entities.CheckType;
import it.vkod.models.entities.User;
import it.vkod.services.flow.AuthenticationService;
import it.vkod.services.flow.CheckService;
import it.vkod.services.flow.UserService;
import it.vkod.views.components.CheckedUserLayout;
import it.vkod.views.components.NotificationUtils;
import org.vaadin.elmot.flow.sensors.GeoLocation;

import java.time.LocalDateTime;
import java.util.Random;

import static it.vkod.models.entities.CheckType.PHYSICAL_OUT;

public class SafeCheckoutLayout extends VerticalLayout {

    private final AuthenticationService authService;
    private final UserService userService;
    private final CheckService checkService;

    private final HorizontalLayout events;
    private final GeoLocation location;
    private final ZXingVaadinReader scanner;


    public SafeCheckoutLayout(final AuthenticationService authService, final UserService userService, final CheckService checkService) {

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


    private void initCheckinLayout(User user) {

        final var checks = checkService.fromTodayAndCourse(user.getCourse(), PHYSICAL_OUT);

        for (final Check check : checks) {
            final var checkLayout = new CheckedUserLayout(check);
            events.add(checkLayout);
        }

        scanner.addValueChangeListener(onScan -> {

            if (onScan.getValue().equalsIgnoreCase(user.getUsername())) {

                final var failSafeForm = new FormLayout();

                final var safeDate = new DateTimePicker();
                safeDate.setLabel("Het datum van de check");
                safeDate.setHelperText("Het datum moet tussen de laatste 60 dagen zijn.");
                safeDate.setAutoOpen(true);
                safeDate.setMin(LocalDateTime.now().minusDays(1));
                safeDate.setMax(LocalDateTime.now().plusDays(7));
                safeDate.setValue(LocalDateTime.now());

                final var safeUsername = new TextField("Gebruikersnaam:");

                final var safeSubmit = new Button("Verzenden", VaadinIcon.CHECK_SQUARE.create());
                safeSubmit.addClickListener(onSafeSubmit -> {

                    final var newCheck = checkService.createOrUpdate(
                            check(user, userService.getByUsername(onScan.getValue()), location, PHYSICAL_OUT));

                    if (!checks.contains(newCheck)) {
                        final var checkLayout = new CheckedUserLayout(newCheck);
                        events.add(checkLayout);
                    }

                    NotificationUtils.success(newCheck.getAttendee().toString() + ": " + newCheck.getType().name()).open();


                });

                failSafeForm.add(safeUsername, safeDate, safeSubmit);
                add(failSafeForm);

                scanner.setVisible(false);


            } else {
                NotificationUtils.error("Uw account heeft GEEN toegang tot de failsafe-modus.").open();
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


    public Check check(User organizer, User attendee, GeoLocation location, CheckType type) {

        return new Check()
                .setOrganizer(organizer)
                .setAttendee(attendee)
                .setCourse(attendee.getCourse())
                .setLat(location.getValue().getLatitude())
                .setLon(location.getValue().getLongitude())
                .setValidation(new Random().nextInt(8999) + 1000)
                .setSession(VaadinSession.getCurrent().getSession().getId())
                .setType(type);
    }

}

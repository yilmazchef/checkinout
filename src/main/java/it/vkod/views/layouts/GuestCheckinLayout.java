package it.vkod.views.layouts;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.VaadinSession;
import com.wontlost.zxing.Constants;
import com.wontlost.zxing.ZXingVaadinReader;
import it.vkod.models.entities.Check;
import it.vkod.models.entities.CheckType;
import it.vkod.models.entities.User;
import it.vkod.services.flow.CheckService;
import it.vkod.services.flow.UserService;
import org.vaadin.elmot.flow.sensors.GeoLocation;

import java.util.Random;
import java.util.UUID;

import static it.vkod.models.entities.CheckType.GUEST_IN;

public class GuestCheckinLayout extends VerticalLayout {

    private final UserService userService;
    private final CheckService checkService;

    private final HorizontalLayout events;
    private final GeoLocation location;
    private final ZXingVaadinReader scanner;


    public GuestCheckinLayout(final UserService userService, final CheckService checkService) {

        this.userService = userService;
        this.checkService = checkService;

        initCheckinLayoutStyle();

        location = initLocationLayout();
        events = initEventsLayout();
        scanner = initScannerLayout();

        final var course = new TextField("Course");
        final var organizer = new TextField("Organizer");

        final var firstName = new TextField("Voornaam");
        final var lastName = new TextField("Familienaam");
        final var username = new TextField("Gebruikersnaam");
        final var email = new TextField("email");
        final var submit = new Button("Submit");

        final var formLayout = new FormLayout();
        formLayout.add(
                firstName, lastName,
                username,
                organizer,
                course,
                submit
        );
        formLayout.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new FormLayout.ResponsiveStep("500px", 2)
        );
        // Stretch the username field over 2 columns
        formLayout.setColspan(username, 2);

        submit.addClickListener(onSubmit -> {
            String pwd = UUID.randomUUID().toString();
            User attendee = userService.createUser(new User()
                    .setUsername(username.getValue())
                    .setEmail(email.getValue())
                    .setFirstName(firstName.getValue())
                    .setLastName(lastName.getValue())
                    .setCourse(course.getValue())
                    .setPassword(pwd)
            );

            final var newCheck = initCheckinLayout(
                    course.getValue(),
                    userService.getByUsername(organizer.getValue()),
                    attendee);

            scanner.addValueChangeListener(onScan -> {

                if (onScan.getValue().equalsIgnoreCase(String.valueOf(newCheck.getValidation()))) {
                    final var checkLayout = new CheckedUserLayout(newCheck);
                    events.add(checkLayout);
                }

                NotificationLayout.success(newCheck.getAttendee().toString() + ": " + newCheck.getType().name()).open();

            });

            add(scanner);
        });

        events.add(formLayout);

        add(location, events);


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


    private Check initCheckinLayout(final String course, final User organizer, final User attendee) {

        final var checks = checkService.fromTodayAndCourse(course, GUEST_IN);

        for (final Check check : checks) {
            final var checkLayout = new CheckedUserLayout(check);
            events.add(checkLayout);
        }

        return checkService.createOrUpdate(check(organizer, attendee, location, GUEST_IN));


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

package it.vkod.views.layouts;


import com.google.zxing.WriterException;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.wontlost.zxing.Constants;
import com.wontlost.zxing.ZXingVaadinReader;
import it.vkod.models.entities.Check;
import it.vkod.models.entities.CheckType;
import it.vkod.models.entities.User;
import it.vkod.models.entities.UserRole;
import it.vkod.services.flow.CheckService;
import it.vkod.services.flow.EmailService;
import it.vkod.services.flow.UserService;
import it.vkod.views.pages.LoginPage;
import org.vaadin.elmot.flow.sensors.GeoLocation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static it.vkod.models.entities.CheckType.GUEST_IN;
import static it.vkod.utils.QRUtils.generateQR;

@PreserveOnRefresh
public class GuestCheckinLayout extends VerticalLayout {

    private final UserService userService;
    private final CheckService checkService;

    private final HorizontalLayout events;
    private final GeoLocation location;
    private final ZXingVaadinReader scanner;
    private final VerticalLayout generate;

    private final FormLayout formLayout;

    private final Select<User> organizers;
    private final TextField course;
    private final TextField firstName;
    private final TextField lastName;
    private final TextField username;
    private final TextField email;
    private final TextField phone;
    private final Button submit;
    private final Button login;

    private static final String WHATSAPP_REDIRECT_URL = "https://api.whatsapp.com/send?phone=";

    public GuestCheckinLayout(final UserService userService, final CheckService checkService, final EmailService emailService) {

        this.userService = userService;
        this.checkService = checkService;

        initCheckinLayoutStyle();

        location = initLocationLayout();
        events = initEventsLayout();
        scanner = initScannerLayout();
        generate = initGenerateLayout();
        organizers = initOrganizersLayout();

        course = new TextField("Course");

        firstName = new TextField("Voornaam");
        lastName = new TextField("Familienaam");
        username = new TextField("Gebruikersnaam");
        email = new TextField("Email");
        phone = new TextField("GSM-nummer");
        phone.setRequiredIndicatorVisible(true);
        phone.setClearButtonVisible(true);
        phone.setPlaceholder("+32XXXXXXXXX");
        submit = new Button("Submit");
        login = new Button("Already Registered? Login");

        formLayout = new FormLayout();
        formLayout.add(
                firstName, lastName,
                username, email, phone,
                organizers,
                course,
                submit, login
        );
        formLayout.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new FormLayout.ResponsiveStep("480px", 2),
                new FormLayout.ResponsiveStep("640px", 3)
        );

        login.addClickListener(onLogin -> {
            UI.getCurrent().navigate(LoginPage.class);
        });

        submit.addClickListener(onSubmit -> {
            String pwd = UUID.randomUUID().toString();
            User attendee = userService.createUser(new User()
                    .setUsername(username.getValue())
                    .setEmail(email.getValue())
                    .setPhone(phone.getValue())
                    .setFirstName(firstName.getValue())
                    .setLastName(lastName.getValue())
                    .setCourse(course.getValue())
                    .setPassword(pwd)
                    .setRoles(Set.of(UserRole.GUEST))
            );

            final var newCheck = initCheckinLayout(course.getValue(), organizers.getValue(), attendee);

            scanner.addValueChangeListener(onScan -> {

                if (onScan.getValue().equalsIgnoreCase(String.valueOf(newCheck.getValidation()))) {

                    try {

                        final var image = convertToImage(generateQR(attendee.getUsername(), 256, 256), attendee.getUsername());
                        generate.add(
                                image);
                        Notification.show((
                                "Generated a QR Code for " + attendee.getUsername()), 8000, Notification.Position.BOTTOM_CENTER).open();

                        final var sendWhatsAppButton = new Button("Whatsapp", onSendClick -> {
                            UI.getCurrent().getPage().setLocation(WHATSAPP_REDIRECT_URL.concat(attendee.getPhone()));
                        });

                        final var sendEmailButton = new Button("Email", onSendClick -> {
                            emailService.sendSimpleMessage(attendee.getEmail(), "Welcome bij Intec. Je QR code is nu klaar.", image.getSrc());
                        });

                        generate.add(sendWhatsAppButton, sendEmailButton);
                        generate.setVisible(true);
                        scanner.setVisible(false);
                        events.setVisible(false);

                    } catch (WriterException | IOException fileEx) {
                        Notification.show(fileEx.getMessage(), 3000, Notification.Position.BOTTOM_CENTER).open();
                    }

                }

                NotificationLayout.success(newCheck.getAttendee().toString() + ": " + newCheck.getType().name()).open();

            });

            add(scanner, generate);
        });

        events.add(formLayout);

        add(location, events);


    }

    private Select<User> initOrganizersLayout() {
        final var layout = new Select<>(userService.teachers().toArray(User[]::new));
        layout.setLabel("Organisator");
        layout.setEmptySelectionAllowed(false);
        layout.setRequiredIndicatorVisible(true);
        layout.setTextRenderer(User::getUsername);
        return layout;
    }

    private Image convertToImage(final byte[] imageData, final String username) {

        return new Image(new StreamResource(
                username.concat("_QR.png"),
                (InputStreamFactory) () -> new ByteArrayInputStream(imageData)),
                username);
    }

    private VerticalLayout initGenerateLayout() {
        final VerticalLayout layout;
        layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setMargin(false);
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setVisible(false);
        layout.setHeight("100vh");
        return layout;
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

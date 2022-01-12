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
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import it.vkod.models.entities.Course;
import it.vkod.models.entities.Role;
import it.vkod.models.entities.User;
import it.vkod.services.flow.EmailService;
import it.vkod.services.flow.UserService;
import it.vkod.utils.QRUtils;
import it.vkod.views.pages.LoginPage;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.vaadin.elmot.flow.sensors.GeoLocation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@PreserveOnRefresh
public class RegisterLayout extends VerticalLayout {

    private final UserService userService;
    private final EmailService emailService;

    private final BCryptPasswordEncoder passwordEncoder;

    private final HorizontalLayout events;
    private final GeoLocation location;
    private final VerticalLayout generate;

    private final FormLayout formLayout;

    private final Select<User> organizers;
    private final Select<Course> course;
    private final TextField firstName;
    private final TextField lastName;
    private final TextField username;
    private final PasswordField password;
    private final PasswordField repeat;
    private final TextField email;
    private final TextField phone;
    private final Button submit;
    private final Button login;

    public RegisterLayout(final UserService userService, final EmailService emailService, final BCryptPasswordEncoder passwordEncoder) {

        this.userService = userService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;

        location = initLocationLayout();
        events = initEventsLayout();
        generate = initGenerateLayout();
        organizers = initOrganizersLayout();

        course = new Select<>(Course.values());

        firstName = new TextField("Voornaam");
        lastName = new TextField("Familienaam");
        username = new TextField("Gebruikersnaam");
        password = new PasswordField("Wachtwoord");
        repeat = new PasswordField("Valideer Wachtwoord");
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
                username, password, repeat,
                email, phone,
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

        login.addClickListener(onLogin -> UI.getCurrent().navigate(LoginPage.class));

        submit.addClickListener(onSubmit -> {
            final var pwd = passwordEncoder.encode(password.getValue());
            final var attendee = this.userService.createUser(new User()
                    .setUsername(username.getValue())
                    .setEmail(email.getValue())
                    .setPhone(phone.getValue())
                    .setFirstName(firstName.getValue())
                    .setLastName(lastName.getValue())
                    .setCourse(course.getValue())
                    .setPassword(pwd)
                    .setRoles(Set.of(Role.GUEST))
            );


            try {

                final var image = convertToImage(QRUtils.generateQR(attendee.getUsername(), 256, 256), attendee.getUsername());
                generate.add(
                        image);
                Notification.show((
                        "Generated a QR Code for " + attendee.getUsername()), 8000, Notification.Position.BOTTOM_CENTER).open();

                final var sendEmailButton = new Button("Email", onSendClick -> {
                    this.emailService.sendSimpleMessage(attendee.getEmail(), "Welcome bij Intec. Je QR code is nu klaar.", image.getSrc());
                });

                generate.add(sendEmailButton);
                generate.setVisible(true);
                events.setVisible(false);

                NotificationLayout.success("Success! Een opniuew account voor " + attendee + " wodt gecreëerd.").open();


            } catch (WriterException | IOException fileEx) {
                Notification.show(fileEx.getMessage(), 3000, Notification.Position.BOTTOM_CENTER).open();
            }

        });

        add(generate);
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


}

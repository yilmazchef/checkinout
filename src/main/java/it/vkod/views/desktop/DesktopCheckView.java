package it.vkod.views.desktop;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

import javax.annotation.security.PermitAll;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import com.wontlost.zxing.Constants;
import com.wontlost.zxing.ZXingVaadinReader;

import org.vaadin.elmot.flow.sensors.GeoLocation;

import it.vkod.models.dto.CheckDetails;
import it.vkod.models.entity.Check;
import it.vkod.models.entity.Event;
import it.vkod.models.entity.User;
import it.vkod.services.flow.AuthenticationService;
import it.vkod.services.flow.CheckService;
import it.vkod.services.flow.UserService;

@PageTitle("Inchecken")
@Route(value = "in", layout = DesktopAppLayout.class)
@RouteAlias(value = "checkin", layout = DesktopAppLayout.class)
@RouteAlias(value = "", layout = DesktopAppLayout.class)
@PermitAll
@StyleSheet("css/checkview.css")
public class DesktopCheckView extends VerticalLayout {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final CheckService checkService;

    private static final String CHECK_IN = "IN";
    private static final String CHECK_OUT = "OUT";

    public DesktopCheckView(AuthenticationService authenticationService, UserService userService,
            CheckService checkService) {

        this.authenticationService = authenticationService;
        this.userService = userService;
        this.checkService = checkService;

        initStyle();

        final var user = this.authenticationService.get();

        if (user.isPresent()) {

            final var configLayout = new HorizontalLayout();

            final var typeSelect = new RadioButtonGroup<String>();
            typeSelect.setItems(CHECK_IN, CHECK_OUT);
            typeSelect.setValue(CHECK_IN);
            typeSelect.setEnabled(false);

            final var courseSelect = courseSelection();
            courseSelect.addValueChangeListener(onSelection -> {

                final var organizer = user.get();

                final var matchesWithTrainersCourse = organizer.getCurrentTraining().contains(onSelection.getValue());

                typeSelect.setEnabled(matchesWithTrainersCourse);

                if (matchesWithTrainersCourse) {

                    initializeScannerLayout(onSelection.getValue(), typeSelect.getValue(), organizer);

                } else {
                    notifyTeacherToAuthenticate().open();
                }
            });

            configLayout.add(courseSelect, typeSelect);
            add(configLayout);

        } else {
            notifyStudentToAuthenticate().open();
        }

    }

    private void initializeScannerLayout(final String training, final String type, final User organizer) {
        final var locationLayout = new VerticalLayout();
        locationLayout.setMargin(false);
        locationLayout.setPadding(false);
        locationLayout.setSpacing(false);

        final var geoLocation = geoLocation();
        locationLayout.add(geoLocation);

        final var activeChecks = new HorizontalLayout();
        final var initChecks = this.checkService.fetchInDetailsToday();

        for (CheckDetails cd : initChecks) {
            activeChecks.add(singleCheckDetails(cd));
        }

        final var scanner = qrReader();

        scanner.addValueChangeListener(qr -> {

            final var coordinates = new Double[] {
                    geoLocation.getValue().getLatitude(),
                    geoLocation.getValue().getLongitude()
            };
            final var oAttendee = this.userService.findByUsername(qr.getValue());

            if (oAttendee.isPresent() && type.equalsIgnoreCase(CHECK_IN)) {
                checkInUser(oAttendee.get(), coordinates, organizer, training);
                activeChecks.removeAll();

                final var allCheckIns = this.checkService.fetchInDetailsToday();

                for (CheckDetails cd : allCheckIns) {
                    activeChecks.add(singleCheckDetails(cd));
                }

            } else if (oAttendee.isPresent() && type.equalsIgnoreCase(CHECK_OUT)) {
                checkOutUser(oAttendee.get(), coordinates, organizer, training);
                activeChecks.removeAll();

                final var allCheckOuts = this.checkService.fetchOutDetailsToday();

                for (CheckDetails cd : allCheckOuts) {
                    activeChecks.add(singleCheckDetails(cd));
                }

            } else {
                notifyTeacherToAuthenticate().open();
            }
        });

        add(scanner, activeChecks, locationLayout);

        final var trainingChangedMessage = Notification.show("Switched to " + training);
        trainingChangedMessage.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        trainingChangedMessage.open();
    }

    private HorizontalLayout singleCheckDetails(CheckDetails details) {

        final var row = new HorizontalLayout();
        row.setAlignItems(Alignment.CENTER);

        final var fullName = details.getFirstName() + " " + details.getLastName();

        final var avatar = new Avatar();
        avatar.setName(fullName);
        avatar.setImage(details.getProfile());

        final var name = new Span(fullName);
        final var roles = new Span(details.getRoles());
        roles.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "var(--lumo-font-size-s)");

        final var column = new VerticalLayout(name, roles);
        column.setPadding(false);
        column.setSpacing(false);

        row.add(avatar, column);
        row.getStyle().set("line-height", "var(--lumo-line-height-m)");
        return row;
    }

    private Notification notifyStudentToAuthenticate() {
        final var authenticationError = Notification.show(
                "The user is NOT authorized. Please go back to login page and authenticate with a teacher, manager, or admin.",
                4000, Position.BOTTOM_CENTER);
        authenticationError.addThemeVariants(NotificationVariant.LUMO_ERROR);

        final var text = new Div(new Text("Unauthorized event."));

        final var closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> {
            authenticationError.close();
            UI.getCurrent().navigate(DesktopLoginView.class);
        });

        final var layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(Alignment.CENTER);

        authenticationError.add(layout);
        return authenticationError;
    }

    private Notification notifyTeacherToAuthenticate() {
        final var trainingError = Notification.show(
                "Authenticated user does not have access to create events for this training.",
                4000, Position.BOTTOM_CENTER);
        trainingError.addThemeVariants(NotificationVariant.LUMO_ERROR);
        trainingError.open();

        removeAll();

        final var reAuthenticateButton = new Anchor("login", "Reauthenticate with another account");
        trainingError.add(reAuthenticateButton);
        return trainingError;
    }

    private ZXingVaadinReader qrReader() {
        final var reader = new ZXingVaadinReader();

        reader.setFrom(Constants.From.camera);
        reader.setId("video"); // id needs to be 'video' if From.camera.
        reader.setStyle("object-fit: cover; width:35vw; height:65vh; max-height:35vw");
        return reader;
    }

    private GeoLocation geoLocation() {
        final var geoLocation = new GeoLocation();
        geoLocation.setWatch(true);
        geoLocation.setHighAccuracy(true);
        geoLocation.setTimeout(100000);
        geoLocation.setMaxAge(200000);
        return geoLocation;
    }

    private Select<String> courseSelection() {
        final var courseSelect = new Select<String>();
        courseSelect.setLabel("Selecteer een opleiding");
        courseSelect.setItems(DataProvider.ofItems(
                "Java Jan 21",
                "Java Jun 21",
                "Java Sep 21",
                "Java Dec 21",
                "C# Jan 21",
                "C# Jun 21",
                "C# Sep 21",
                "C# Dec 21",
                "Python Jan 21",
                "Python Jun 21",
                "Python Sep 21",
                "Python Dec 21"));
        return courseSelect;
    }

    private void initLayoutStyle(final VerticalLayout layout, final String width) {
        layout.setMargin(false);
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getStyle().set("margin-top", "4vh");
        layout.setWidth(width);
        layout.setHeight("90vh");
    }

    private void checkInUser(final User attendee, final Double[] coordinates, final User organizer,
            final String training) {

        final var hasCheckedInBefore = this.checkService.fetchByDate(Date.valueOf(LocalDate.now()),
                attendee.getUsername());

        if (hasCheckedInBefore.isEmpty()) {
            final var checkEntity = new Check()
                    .setIsActive(true)
                    .setCurrentSession(VaadinSession.getCurrent().getSession().getId())
                    .setPincode(new Random().nextInt(9999))
                    .setCheckedInAt(Time.valueOf(LocalTime.now()))
                    .setCheckedOutAt(Time.valueOf(LocalTime.now()))
                    .setQrcode(attendee.getUsername())
                    .setLat(coordinates[0])
                    .setLon(coordinates[1])
                    .setCheckedOn(Date.valueOf(LocalDate.now()));

            final var check = this.checkService.createCheck(checkEntity);

            final var oEvent = this.checkService.fetchByAttendeeIdAndCheckId(attendee.getId(), check.getId(), CHECK_IN);
            final var eventBeingEdited = new Object() {
                Event data = null;
            };

            if (oEvent.isPresent()) {
                eventBeingEdited.data = oEvent
                        .get()
                        .setCheckId(check.getId())
                        .setAttendeeId(attendee.getId())
                        .setOrganizerId(organizer.getId())
                        .setCheckType("IN")
                        .setTraining(training);
            } else {
                eventBeingEdited.data = new Event()
                        .setCheckId(check.getId())
                        .setAttendeeId(attendee.getId())
                        .setOrganizerId(organizer.getId())
                        .setCheckType("IN")
                        .setTraining(training);
            }

            final var savedOrUpdatedEvent = this.checkService.createEvent(eventBeingEdited.data);

            final var foundUser = this.userService.findUserById(savedOrUpdatedEvent.getAttendeeId());
            if (foundUser.isPresent()) {
                notifyCheck(organizer, organizer, false);
            }

        } else {
            Notification.show(
                    (attendee.getFirstName() + " " + attendee.getLastName() + " heeft vroeger al ingecheckt."),
                    4000,
                    Notification.Position.BOTTOM_CENTER)
                    .open();
        }

    }

    private void checkOutUser(final User attendee, final Double[] coordinates, final User organizer,
            final String training) {

        final var foundCheck = this.checkService.fetchByDate(Date.valueOf(LocalDate.now()), attendee.getUsername());

        if (foundCheck.isPresent()) {

            final var check = this.checkService.createCheck(
                    foundCheck
                            .get()
                            .setCurrentSession(VaadinSession.getCurrent().getSession().getId())
                            .setQrcode(attendee.getUsername())
                            .setCheckedOutAt(Time.valueOf(LocalTime.now()))
                            .setLat(coordinates[0])
                            .setLon(coordinates[1]));

            final var oEvent = this.checkService.fetchByAttendeeIdAndCheckId(
                    attendee.getId(), check.getId(), CHECK_OUT);

            final var eventBeingEdited = new Object() {
                Event data = null;
            };

            if (oEvent.isPresent()) {
                eventBeingEdited.data = oEvent
                        .get()
                        .setCheckId(check.getId())
                        .setAttendeeId(attendee.getId())
                        .setOrganizerId(organizer.getId())
                        .setCheckType(CHECK_OUT)
                        .setTraining(training);
            } else {
                eventBeingEdited.data = new Event()
                        .setCheckId(check.getId())
                        .setAttendeeId(attendee.getId())
                        .setOrganizerId(organizer.getId())
                        .setCheckType(CHECK_OUT)
                        .setTraining(training);
            }

            final var savedOrUpdatedEvent = this.checkService.createEvent(eventBeingEdited.data);
            if (!savedOrUpdatedEvent.isNew()) {
                notifyCheck(organizer, organizer, true);
            }

        } else {
            Notification.show(
                    ("Geannuleerd, GEEN valid QR-code: " + attendee.getUsername()),
                    4000,
                    Notification.Position.BOTTOM_CENTER)
                    .open();
        }

    }

    private void notifyCheck(final User attendee, final User organizer, final boolean isCheckOut) {

        final var message = attendee.getFirstName()
                + " "
                + attendee.getLastName()
                + (!isCheckOut ? " heeft ingecheckt " : " heeft uitgecheckt")
                + "."
                + "Org: "
                + organizer.getFirstName()
                + " "
                + organizer.getLastName();

        final var notification = Notification.show(message, 3000, Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.open();
    }

    private void initStyle() {

        addClassNames("flex", "flex-col", "h-full");
        setMargin(false);
        setPadding(false);
        setSpacing(false);
        setWidthFull();

        setJustifyContentMode(JustifyContentMode.CENTER);
        setHorizontalComponentAlignment(Alignment.CENTER);
        setAlignItems(Alignment.CENTER);
    }
}
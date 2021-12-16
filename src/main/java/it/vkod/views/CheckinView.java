package it.vkod.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import com.wontlost.zxing.Constants;
import com.wontlost.zxing.ZXingVaadinReader;
import it.vkod.models.dto.CheckDetails;
import it.vkod.models.entity.Check;
import it.vkod.models.entity.Event;
import it.vkod.models.entity.User;
import it.vkod.services.flow.AuthenticationService;
import it.vkod.services.flow.CheckService;
import it.vkod.services.flow.UserService;
import org.vaadin.elmot.flow.sensors.GeoLocation;

import javax.annotation.security.PermitAll;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

@PageTitle("Inchecken")
@Route(value = "in", layout = TemplateLayout.class)
@RouteAlias(value = "checkin", layout = TemplateLayout.class)
@RouteAlias(value = "", layout = TemplateLayout.class)
@PermitAll
public class CheckinView extends VerticalLayout {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final CheckService checkService;

    public CheckinView(
            AuthenticationService authenticationService, UserService userService,
            CheckService checkService) {

        this.authenticationService = authenticationService;
        this.userService = userService;
        this.checkService = checkService;

        initStyle();

        final var splitLayout = new HorizontalLayout();

        final var user = this.authenticationService.get();

        if (user.isPresent()) {

            final var attendeesGrid = new Grid<CheckDetails>();
            attendeesGrid.setColumnReorderingAllowed(true);

            final var configLayout = new HorizontalLayout();

            final var typeSelect = typeSelection();

            final var courseSelect = courseSelection();
            courseSelect.addValueChangeListener(onSelection -> {

                final var trainingChangedMessage = Notification.show("Switching to " + onSelection.getValue());
                trainingChangedMessage.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
                trainingChangedMessage.open();

                final var matchesWithTrainersCourse = onSelection.getValue()
                        .equalsIgnoreCase(user.get().getCurrentTraining());

                if (matchesWithTrainersCourse) {

                    final var locationLayout = new VerticalLayout();
                    locationLayout.setMargin(false);
                    locationLayout.setPadding(false);
                    locationLayout.setSpacing(false);

                    final var geoLocation = geoLocation();
                    locationLayout.add(geoLocation);

                    final var leftLayout = new VerticalLayout();
                    initLayoutStyle(leftLayout, "35vw");

                    final var reader = qrReader();

                    reader.addValueChangeListener(scannedQRCode -> {

                        final var username = scannedQRCode.getValue();
                        final var lat = geoLocation.getValue().getLatitude();
                        final var lon = geoLocation.getValue().getLongitude();
                        final var organizer = user.get();
                        final var training = courseSelect.getValue();
                        final var type = typeSelection().getValue() == null ? "IN" : typeSelection().getValue();

                        check(username, lat, lon, organizer, training, attendeesGrid, type);
                    });

                    leftLayout.add(reader, locationLayout);

                    final var rightLayout = new VerticalLayout();
                    initLayoutStyle(rightLayout, "55vw");

                    rightLayout.add(attendeesGrid);
                    splitLayout.add(leftLayout, rightLayout);

                    add(splitLayout);

                } else {
                    final var trainingError = Notification.show(
                            "Authenticated user does not have access to create events for this training.",
                            4000, Position.BOTTOM_CENTER);
                    trainingError.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    trainingError.open();

                    removeAll();

                    final var reAuthenticateButton = new Anchor("login", "Reauthenticate with another account");
                    add(reAuthenticateButton);
                }
            });

            configLayout.add(courseSelect, typeSelect);
            add(configLayout);

        } else {
            final var authenticationError = Notification.show(
                    "The user is NOT authorized. Please go back to login page and authenticate with a teacher, manager, or admin.",
                    4000, Position.BOTTOM_CENTER);
            authenticationError.addThemeVariants(NotificationVariant.LUMO_ERROR);

            Div text = new Div(new Text("Unauthorized event."));

            Button closeButton = new Button(new Icon("lumo", "cross"));
            closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
            closeButton.getElement().setAttribute("aria-label", "Close");
            closeButton.addClickListener(event -> {
                authenticationError.close();
                UI.getCurrent().navigate(LoginView.class);
            });

            HorizontalLayout layout = new HorizontalLayout(text, closeButton);
            layout.setAlignItems(Alignment.CENTER);

            authenticationError.add(layout);
            authenticationError.open();
        }

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

    private Select<String> typeSelection() {
        final var typeSelect = new Select<String>();
        typeSelect.setLabel("Checktyp");
        typeSelect.setItems(DataProvider.ofItems("IN", "OUT"));
        return typeSelect;
    }

    private Select<String> courseSelection() {
        final var courseSelect = new Select<String>();
        courseSelect.setLabel("Selecteer een opleiding");
        courseSelect.setItems(DataProvider.ofItems(
                "Java Jan 21",
                "Java Jun 21",
                "Java Sep 21",
                "Java Dec 21",
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

    private void check(final String username,
            final Double lat, final Double lon,
            final User organizer,
            final String training,
            final Grid<CheckDetails> attendeesGrid,
            final String type) {

        if (type.equalsIgnoreCase("IN")) {
            checkInUser(username, lat, lon, organizer, training, attendeesGrid);
        } else if (type.equalsIgnoreCase("OUT")) {
            checkOutUser(username, lat, lon, organizer, training, attendeesGrid);
        }
    }

    private void checkInUser(final String username,
            final Double lat, final Double lon,
            final User organizer,
            final String training,
            final Grid<CheckDetails> attendeesGrid) {

        final var oAttendee = this.userService.findByUsername(username);

        boolean valid = oAttendee.isPresent() && oAttendee.get().getCurrentTraining().equalsIgnoreCase(training);

        if (valid) {

            final var attendee = oAttendee.get();

            final var hasCheckedInBefore = this.checkService.fetchByDate(Date.valueOf(LocalDate.now()), username);

            if (hasCheckedInBefore.isEmpty()) {
                final var checkEntity = new Check()
                        .setIsActive(true)
                        .setCurrentSession(VaadinSession.getCurrent().getSession().getId())
                        .setPincode(new Random().nextInt(9999))
                        .setCheckedInAt(Time.valueOf(LocalTime.now()))
                        .setCheckedOutAt(Time.valueOf(LocalTime.now()))
                        .setQrcode(username)
                        .setLat(lat)
                        .setLon(lon)
                        .setCheckedOn(Date.valueOf(LocalDate.now()));

                final var check = this.checkService.createCheck(checkEntity);

                final var oEvent = this.checkService.fetchByAttendeeIdAndCheckId(attendee.getId(), check.getId(), "IN");
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
                if (foundUser.isPresent() && attendeesGrid != null) {
                    attendeesGrid.setItems(checkService.fetchInDetailsToday());
                }

                final var message = attendee.getFirstName()
                        + " "
                        + attendee.getLastName()
                        + " heeft ingecheckt "
                        + "."
                        + "Org: "
                        + organizer.getFirstName()
                        + " "
                        + organizer.getLastName();
                Notification.show(
                        message,
                        4000,
                        Notification.Position.BOTTOM_CENTER)
                        .open();

                final var notification = Notification.show("Event is submitted!");

                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            } else {
                Notification.show(
                        (username + " heeft vroeger al gecheckt."),
                        4000,
                        Notification.Position.BOTTOM_CENTER)
                        .open();
            }

        } else {
            Notification.show(
                    (username + " is GEEN valid QR-code."),
                    4000,
                    Notification.Position.BOTTOM_CENTER)
                    .open();
        }
    }

    private void checkOutUser(final String scannedQRCode,
            final Double lat, final Double lon,
            final User organizer,
            final String training,
            final Grid<CheckDetails> attendeesGrid) {

        final var oAttendee = this.userService.findByUsername(scannedQRCode);
        if (oAttendee.isPresent()) {
            final var attendee = oAttendee.get();

            final var foundCheck = this.checkService.fetchByDate(Date.valueOf(LocalDate.now()), scannedQRCode);

            if (foundCheck.isPresent()) {

                final var check = this.checkService.createCheck(
                        foundCheck
                                .get()
                                .setCurrentSession(VaadinSession.getCurrent().getSession().getId())
                                .setQrcode(scannedQRCode)
                                .setCheckedOutAt(Time.valueOf(LocalTime.now()))
                                .setLat(lat)
                                .setLon(lon));

                final var oEvent = this.checkService.fetchByAttendeeIdAndCheckId(
                        attendee.getId(), check.getId(), "OUT");

                final var eventBeingEdited = new Object() {
                    Event data = null;
                };

                if (oEvent.isPresent()) {
                    eventBeingEdited.data = oEvent
                            .get()
                            .setCheckId(check.getId())
                            .setAttendeeId(attendee.getId())
                            .setOrganizerId(organizer.getId())
                            .setCheckType("OUT")
                            .setTraining(training);
                } else {
                    eventBeingEdited.data = new Event()
                            .setCheckId(check.getId())
                            .setAttendeeId(attendee.getId())
                            .setOrganizerId(organizer.getId())
                            .setCheckType("OUT")
                            .setTraining(training);
                }

                final var savedOrUpdatedEvent = this.checkService.createEvent(eventBeingEdited.data);
                if (!savedOrUpdatedEvent.isNew() && attendeesGrid != null) {
                    attendeesGrid.setItems(this.checkService.fetchOutDetailsToday());
                }

                Notification.show(
                        attendee.getFirstName()
                                + " "
                                + attendee.getLastName()
                                + " heeft gecheckt"
                                + "."
                                + "Org: "
                                + organizer.getFirstName()
                                + " "
                                + organizer.getLastName(),
                        4000,
                        Notification.Position.BOTTOM_CENTER)
                        .open();

            } else {
                Notification.show(
                        ("Geannuleerd, GEEN valid QR-code: " + scannedQRCode),
                        4000,
                        Notification.Position.BOTTOM_CENTER)
                        .open();
            }

        } else {
            Notification.show(
                    (scannedQRCode + " heeft nog NIET gecheckt."),
                    4000,
                    Notification.Position.BOTTOM_CENTER)
                    .open();
        }
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

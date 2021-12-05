package it.vkod.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import com.wontlost.zxing.Constants;
import com.wontlost.zxing.ZXingVaadinReader;
import it.vkod.data.dto.CheckDTO;
import it.vkod.data.entity.Check;
import it.vkod.data.entity.Event;
import it.vkod.data.entity.User;
import it.vkod.services.AuthenticationService;
import it.vkod.services.CheckService;
import it.vkod.services.UserService;
import org.vaadin.elmot.flow.sensors.GeoLocation;

import javax.annotation.security.PermitAll;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@PageTitle("Inchecken")
@Route(value = "in", layout = TemplateLayout.class)
@RouteAlias("checkin")
@RouteAlias("")
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

        user.ifPresent(
                organizer -> {
                    final var attendeesGrid = new Grid<CheckDTO>();
                    attendeesGrid
                            .addColumn(CheckDTO::getFirstName)
                            .setHeader("Voornaam")
                            .setKey("firstName");
                    attendeesGrid
                            .addColumn(CheckDTO::getLastName)
                            .setHeader("Familienaam")
                            .setKey("lastName");
                    attendeesGrid.addColumn(CheckDTO::getEmail).setHeader("Email").setKey("email");
                    attendeesGrid.addThemeVariants(
                            GridVariant.LUMO_NO_BORDER,
                            GridVariant.LUMO_NO_ROW_BORDERS,
                            GridVariant.LUMO_ROW_STRIPES);

                    attendeesGrid.setItems(this.checkService.findAllCheckinDetailsOfToday());

                    final var locationLayout = new VerticalLayout();
                    locationLayout.setMargin(false);
                    locationLayout.setPadding(false);
                    locationLayout.setSpacing(false);
                    final var latField = new TextField("Latitude");
                    latField.setReadOnly(true);
                    final var lonField = new TextField("Longitude");
                    lonField.setReadOnly(true);
                    final var geoLocation = new GeoLocation();
                    geoLocation.setWatch(true);
                    geoLocation.setHighAccuracy(true);
                    geoLocation.setTimeout(100000);
                    geoLocation.setMaxAge(200000);
                    geoLocation.addValueChangeListener(onLocationChange -> {
                        latField.setValue(String.valueOf(onLocationChange.getValue().getLatitude()));
                        lonField.setValue(String.valueOf(onLocationChange.getValue().getLongitude()));
                    });
                    locationLayout.add(latField, lonField, geoLocation);

                    final var leftLayout = new VerticalLayout();
                    leftLayout.setMargin(false);
                    leftLayout.setPadding(false);
                    leftLayout.setSpacing(false);
                    final var reader = new ZXingVaadinReader();

                    reader.setFrom(Constants.From.camera);
                    reader.setId("video"); // id needs to be 'video' if From.camera.
                    reader.setStyle("object-fit: cover; width:400px; height:100vh; max-height:500px");

                    reader.addValueChangeListener(
                            scannedQRCode -> checkInUser(organizer, attendeesGrid, scannedQRCode.getValue(),
                                    Float.valueOf(latField.getValue()), Float.valueOf(lonField.getValue())));

                    leftLayout.add(reader, locationLayout);

                    final var rightLayout = new VerticalLayout();
                    rightLayout.setMargin(false);
                    rightLayout.setPadding(false);
                    rightLayout.setSpacing(false);

                    FormLayout failSafeForm = new FormLayout();

                    TextField usernameField = new TextField();
                    usernameField.setLabel("Gebruikersnaam cursist");
                    usernameField.setRequired(true);

                    final var failSafeRegisterButton =
                            new Button(
                                    "Manueel Inchecken",
                                    onClick -> checkInUser(organizer, attendeesGrid, usernameField.getValue(),
                                            Float.valueOf(latField.getValue()), Float.valueOf(lonField.getValue())));

                    failSafeForm.add(usernameField, failSafeRegisterButton);

                    rightLayout.add(failSafeForm, attendeesGrid);

                    splitLayout.add(leftLayout, rightLayout);

                });


        add(splitLayout);
    }

    private void checkInUser(
            final User organizer, final Grid<CheckDTO> attendeesGrid, final String scannedQRCode, final Float lat, final Float lon) {

        final var oAttendee = this.userService.findByUsername(scannedQRCode);
        if (oAttendee.isPresent()) {
            final var attendee = oAttendee.get();

            final var hasCheckedInBefore =
                    this.checkService.findChecksByCheckedOnAndQrcode(Date.valueOf(LocalDate.now()), scannedQRCode);

            if (hasCheckedInBefore.isEmpty()) {
                final var checkEntity =
                        new Check()
                                .withActive(true)
                                .withCurrentSession(VaadinSession.getCurrent().getSession().getId())
                                .withPincode(111111)
                                .withCheckedInAt(Time.valueOf(LocalTime.now()))
                                .withCheckedOutAt(Time.valueOf(LocalTime.now()))
                                .withQrcode(scannedQRCode)
                                .withLat(lat)
                                .withLon(lon)
                                .withCheckedOn(Date.valueOf(LocalDate.now()));

                final var check = this.checkService.createCheck(checkEntity);

                final var oEvent =
                        this.checkService.findByAttendeeIdAndCheckIdAndCheckType(
                                attendee.getId(), check.getId(), "IN");

                final var eventBeingEdited =
                        new Object() {
                            Event data = null;
                        };

                if (oEvent.isPresent()) {
                    eventBeingEdited.data =
                            oEvent
                                    .get()
                                    .withCheckId(check.getId())
                                    .withAttendeeId(attendee.getId())
                                    .withOrganizerId(organizer.getId())
                                    .withCheckType("IN");
                } else {
                    eventBeingEdited.data =
                            new Event()
                                    .withCheckId(check.getId())
                                    .withAttendeeId(attendee.getId())
                                    .withOrganizerId(organizer.getId())
                                    .withCheckType("IN");
                }

                final var savedOrUpdatedEvent = this.checkService.createEvent(eventBeingEdited.data);

                final var foundUser = this.userService.findUserById(savedOrUpdatedEvent.getAttendeeId());
                if (foundUser.isPresent()) attendeesGrid.setItems(checkService.findAllCheckinDetailsOfToday());

                Notification.show(
                                attendee.getFirstName()
                                        + " "
                                        + attendee.getLastName()
                                        + " heeft ingecheckt "
                                        + "."
                                        + "Verantwoordelijk: "
                                        + organizer.getFirstName()
                                        + " "
                                        + organizer.getLastName(),
                                4000,
                                Notification.Position.BOTTOM_CENTER)
                        .open();
            } else {
                Notification.show(
                                (scannedQRCode + " heeft vroeger al gecheckt."),
                                4000,
                                Notification.Position.BOTTOM_CENTER)
                        .open();
            }

        } else {
            Notification.show(
                            (scannedQRCode + " is GEEN valid QR-code."),
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

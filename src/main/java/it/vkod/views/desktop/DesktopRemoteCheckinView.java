package it.vkod.views.desktop;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
import java.util.List;

@PageTitle("Inchecken-remote")
@Route(value = "inrem", layout = DesktopAppLayout.class)
@RouteAlias(value = "checkin/remote", layout = DesktopAppLayout.class)
@PermitAll
public class DesktopRemoteCheckinView extends VerticalLayout {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final CheckService checkService;

    public DesktopRemoteCheckinView(AuthenticationService authenticationService, UserService userService,
            CheckService checkService) {

        this.authenticationService = authenticationService;
        this.userService = userService;
        this.checkService = checkService;

        initStyle();

        final var splitLayout = new HorizontalLayout();

        final var user = this.authenticationService.get();

        if (user.isPresent()) {

            final var remoteUser = user.get();

            final var checksToday = this.checkService.fetchInDetailsToday();
            final Grid<CheckDetails> checkinGrid = checkDetailsGrid(checksToday);

            final var locationLayout = new VerticalLayout();
            locationLayout.setMargin(false);
            locationLayout.setPadding(false);
            locationLayout.setSpacing(false);

            final GeoLocation geoLocation = geoLocation();
            locationLayout.add(geoLocation);

            final var leftLayout = new VerticalLayout();
            leftLayout.setMargin(false);
            leftLayout.setPadding(false);
            leftLayout.setSpacing(false);

            final var reader = qrScanner();
            reader.addValueChangeListener(
                    scannedQRCode -> checkInUser(remoteUser, checkinGrid, scannedQRCode.getValue(),
                            geoLocation.getValue().getLatitude(), geoLocation.getValue().getLongitude()));

            leftLayout.add(reader, locationLayout);

            final var rightLayout = new VerticalLayout();
            rightLayout.setMargin(false);
            rightLayout.setPadding(false);
            rightLayout.setSpacing(false);

            rightLayout.add(checkinGrid);

            splitLayout.add(leftLayout, rightLayout);

            add(splitLayout);

        }

    }

    public ZXingVaadinReader qrScanner() {

        final var reader = new ZXingVaadinReader();
        reader.setFrom(Constants.From.camera);
        reader.setId("video"); // id needs to be 'video' if From.camera.
        reader.setStyle("object-fit: cover; width:400px; height:100vh; max-height:500px");
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

    private Grid<CheckDetails> checkDetailsGrid(List<CheckDetails> checksToday) {
        final var checkinGrid = new Grid<CheckDetails>();
        checkinGrid
                .addColumn(CheckDetails::getFirstName)
                .setHeader("Voornaam")
                .setKey("firstName");
        checkinGrid
                .addColumn(CheckDetails::getLastName)
                .setHeader("Familienaam")
                .setKey("lastName");
        checkinGrid.addColumn(CheckDetails::getEmail).setHeader("Email").setKey("email");
        checkinGrid.addThemeVariants(
                GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS,
                GridVariant.LUMO_ROW_STRIPES);

        checkinGrid.setItems(checksToday);
        return checkinGrid;
    }

    private void checkInUser(final User organizer, final Grid<CheckDetails> attendeesGrid,
            final String scannedQRCode, final Double lat, final Double lon) {

        final var oAttendee = this.userService.findByUsername(scannedQRCode);

        if (oAttendee.isPresent()) {

            final var attendee = oAttendee.get();

            final var hasCheckedInBefore = this.checkService.fetchByDate(Date.valueOf(LocalDate.now()), scannedQRCode);

            if (hasCheckedInBefore.isEmpty()) {
                final var checkEntity = new Check()
                        .setIsActive(true)
                        .setCurrentSession(VaadinSession.getCurrent().getSession().getId())
                        .setPincode(111111)
                        .setCheckedInAt(Time.valueOf(LocalTime.now()))
                        .setCheckedOutAt(Time.valueOf(LocalTime.now()))
                        .setQrcode(scannedQRCode)
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
                            .setTraining(attendee.getCurrentTraining());
                } else {
                    eventBeingEdited.data = new Event()
                            .setCheckId(check.getId())
                            .setAttendeeId(attendee.getId())
                            .setOrganizerId(organizer.getId())
                            .setCheckType("IN")
                            .setTraining(attendee.getCurrentTraining());
                }

                final var savedOrUpdatedEvent = this.checkService.createEvent(eventBeingEdited.data);

                final var foundUser = this.userService.findUserById(savedOrUpdatedEvent.getAttendeeId());
                if (foundUser.isPresent())
                    attendeesGrid.setItems(checkService.fetchInDetailsToday());

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

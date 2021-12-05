package it.vkod.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import com.wontlost.zxing.Constants;
import com.wontlost.zxing.ZXingVaadinReader;
import it.vkod.data.dto.CheckDTO;
import it.vkod.data.entity.Event;
import it.vkod.data.entity.User;
import it.vkod.services.AuthenticationService;
import it.vkod.services.CheckService;
import it.vkod.services.UserService;

import javax.annotation.security.PermitAll;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@PageTitle("Uitchecken")
@Route(value = "out", layout = TemplateLayout.class)
@RouteAlias("checkout")
@PermitAll
public class CheckoutView extends VerticalLayout {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final CheckService checkService;

    private final SplitLayout splitLayout = new SplitLayout();
    private final Grid<CheckDTO> attendeesGrid = new Grid<>();
    private User organizer;

    public CheckoutView(
            AuthenticationService authenticationService, UserService userService,
            CheckService checkService) {

        this.userService = userService;
        this.checkService = checkService;
        this.authenticationService = authenticationService;

        initStyle();

        final var user = this.authenticationService.get();

        user.ifPresent(
                u -> {
                    this.organizer = u;

                    attendeesGrid.setWidthFull();
                    attendeesGrid.setHeightFull();

                    attendeesGrid
                            .addColumn(CheckDTO::getFirstName)
                            .setHeader("Voornaam")
                            .setKey("firstName");

                    attendeesGrid
                            .addColumn(CheckDTO::getLastName)
                            .setHeader("Familienaam")
                            .setKey("lastName");
                    attendeesGrid.addColumn(CheckDTO::getEmail).setHeader("Email").setKey("email");

                    attendeesGrid
                            .addColumn(CheckDTO::getCheckedOn)
                            .setHeader("Gecheckt op")
                            .setKey("checked_on");

                    attendeesGrid
                            .addColumn(CheckDTO::getCheckedInAt)
                            .setHeader("Ingecheckt om")
                            .setKey("checked_in_at");

                    attendeesGrid
                            .addColumn(CheckDTO::getCheckedOutAt)
                            .setHeader("Uitgecheckt om")
                            .setKey("checked_out_at");

                    attendeesGrid.addThemeVariants(
                            GridVariant.LUMO_NO_BORDER,
                            GridVariant.LUMO_NO_ROW_BORDERS,
                            GridVariant.LUMO_ROW_STRIPES);

                    final var checksGridDataList = this.checkService.findCheckoutDetailsOfToday();
                    attendeesGrid.setItems(checksGridDataList);

                    final var scanLayout = new VerticalLayout();
                    final var reader = new ZXingVaadinReader();

                    reader.setFrom(Constants.From.camera);
                    reader.setId("video"); // id needs to be 'video' if From.camera.
                    reader.setStyle("object-fit: cover; width:50%; height:100vh; max-height:90%");

                    reader.addValueChangeListener(scannedQRCode -> checkOutUser(scannedQRCode.getValue()));

                    scanLayout.setMargin(false);
                    scanLayout.setPadding(false);
                    scanLayout.setJustifyContentMode(JustifyContentMode.CENTER);
                    scanLayout.setAlignItems(Alignment.CENTER);
                    scanLayout.add(reader);

                    // TODO: Add GeoLocation Layout here..

                    final var changesLayout = new VerticalLayout();
                    final var failSafeLayout = new FormLayout();

                    final var usernameField = new TextField();
                    usernameField.setLabel("Gebruikersnaam cursist");
                    usernameField.setRequired(true);

                    final var failSafeRegisterButton =
                            new Button("Manueel uitchecken", onClick -> checkOutUser(usernameField.getValue()));

                    failSafeLayout.add(usernameField, failSafeRegisterButton);
                    changesLayout.add(failSafeLayout, attendeesGrid);

                    changesLayout.add(attendeesGrid);

                    splitLayout.addToPrimary(scanLayout);
                    splitLayout.addToSecondary(changesLayout);
                });

        splitLayout.setSplitterPosition(80);
        add(splitLayout);
    }

    private void checkOutUser(final String scannedQRCode) {

        final var oAttendee = this.userService.findByUsername(scannedQRCode);
        if (oAttendee.isPresent()) {
            final var attendee = oAttendee.get();

            final var foundCheck =
                    this.checkService.findChecksByCheckedOnAndQrcode(Date.valueOf(LocalDate.now()), scannedQRCode);

            if (foundCheck.isPresent()) {

                final var check =
                        this.checkService.createCheck(
                                foundCheck
                                        .get()
                                        .withCurrentSession(VaadinSession.getCurrent().getSession().getId())
                                        .withQrcode(scannedQRCode)
                                        .withCheckedOutAt(Time.valueOf(LocalTime.now()))
                                        .withLat(20.00F)
                                        .withLon(20.00F));

                final var oEvent =
                        this.checkService.findByAttendeeIdAndCheckIdAndCheckType(
                                attendee.getId(), check.getId(), "OUT");

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
                                    .withOrganizerId(this.organizer.getId())
                                    .withCheckType("OUT");
                } else {
                    eventBeingEdited.data =
                            new Event()
                                    .withCheckId(check.getId())
                                    .withAttendeeId(attendee.getId())
                                    .withOrganizerId(this.organizer.getId())
                                    .withCheckType("OUT");
                }

                final var savedOrUpdatedEvent = this.checkService.createEvent(eventBeingEdited.data);
                if (!savedOrUpdatedEvent.isNew())
                    attendeesGrid.setItems(this.checkService.findCheckoutDetailsOfToday());

                Notification.show(
                                attendee.getFirstName()
                                        + " "
                                        + attendee.getLastName()
                                        + " heeft gecheckt"
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

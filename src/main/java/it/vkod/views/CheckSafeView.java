package it.vkod.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import it.vkod.data.dto.CheckDTO;
import it.vkod.data.entity.Check;
import it.vkod.data.entity.Course;
import it.vkod.data.entity.Event;
import it.vkod.data.entity.User;
import it.vkod.services.AuthenticationService;
import it.vkod.services.CheckService;
import it.vkod.services.UserService;
import org.vaadin.elmot.flow.sensors.GeoLocation;

import javax.annotation.security.RolesAllowed;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

@PageTitle("Inchecken/Uitchecken")
@Route(value = "safe", layout = TemplateLayout.class)
@RouteAlias(value = "failsafe", layout = TemplateLayout.class)
@RolesAllowed({"ADMIN", "MANAGER", "LEADER"})
public class CheckSafeView extends VerticalLayout {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final CheckService checkService;

    private final Grid<CheckDTO> attendeesGrid = new Grid<>();

    public CheckSafeView(
            AuthenticationService authenticationService, UserService userService,
            CheckService checkService) {

        this.authenticationService = authenticationService;
        this.userService = userService;
        this.checkService = checkService;

        initStyle();

        final var user = this.authenticationService.get();

        user.ifPresent(
                organizer -> {

                    this.attendeesGrid.addColumn(CheckDTO::getFirstName).setHeader("Voornaam").setKey("firstName");
                    this.attendeesGrid.addColumn(CheckDTO::getLastName).setHeader("Familienaam").setKey("lastName");
                    this.attendeesGrid.addColumn(CheckDTO::getEmail).setHeader("Email").setKey("email");
                    this.attendeesGrid.addColumn(CheckDTO::getCheckedOn).setHeader("Datum").setKey("checked_on");
                    this.attendeesGrid.addColumn(CheckDTO::getCheckedInAt).setHeader("In").setKey("checked_in_at");
                    this.attendeesGrid.addColumn(CheckDTO::getCheckedOutAt).setHeader("Uit").setKey("checked_out_at");
                    this.attendeesGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
                    this.attendeesGrid.setItems(this.checkService.findAllCheckinDetailsOfToday());

                    final var geoLocation = new GeoLocation();
                    geoLocation.setWatch(true);
                    geoLocation.setHighAccuracy(true);
                    geoLocation.setTimeout(100000);
                    geoLocation.setMaxAge(200000);

                    final var failsafeLayout = new VerticalLayout();
                    final var failSafeForm = new FormLayout();

                    final var usernameField = new TextField();
                    usernameField.setLabel("Gebruikersnaam");
                    usernameField.setRequired(true);

                    final var coursesSelect = new Select<Course>();
                    coursesSelect.setLabel("Selecteer een course");
                    coursesSelect.setEmptySelectionAllowed(false);
                    coursesSelect.setItemLabelGenerator(course -> course.getTitle());
                    coursesSelect.setItems(checkService.fetchCourse());

                    final var checkInButton = new Button("Inchecken", onClick ->
                            checkInUser(usernameField.getValue(), geoLocation.getValue().getLatitude(), geoLocation.getValue().getLongitude(), coursesSelect.getValue(), organizer));

                    final var checkOutButton = new Button("Uitchecken", onClick -> checkOutUser(
                            usernameField.getValue(), geoLocation.getValue().getLatitude(), geoLocation.getValue().getLongitude(), coursesSelect.getValue(), organizer));

                    usernameField.addValueChangeListener(onValueChange -> {
                        checkInButton.setEnabled(!onValueChange.getValue().isEmpty());
                        checkOutButton.setEnabled(!onValueChange.getValue().isEmpty());
                    });

                    failSafeForm.add(coursesSelect, usernameField, checkInButton, checkOutButton);
                    failsafeLayout.add(failSafeForm);

                    add(failsafeLayout, attendeesGrid, geoLocation);

                });

    }

    private void checkInUser(final String username, final Double lat, final Double lon, final Course course, final User organizer) {

        final var oAttendee = this.userService.findByUsername(username);
        if (oAttendee.isPresent()) {
            final var attendee = oAttendee.get();

            final var hasCheckedInBefore =
                    this.checkService.findChecksByCheckedOnAndQrcode(Date.valueOf(LocalDate.now()), username);

            if (hasCheckedInBefore.isEmpty()) {
                final var checkEntity =
                        new Check()
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

                final var oEvent =
                        this.checkService.findByAttendeeIdAndCheckIdAndCheckType(
                                attendee.getId(), check.getId(), "IN");

                final var eventBeingEdited = new Object() {
                    Event data = null;
                };

                if (oEvent.isPresent()) {
                    eventBeingEdited.data =
                            oEvent
                                    .get()
                                    .setCheckId(check.getId())
                                    .setAttendeeId(attendee.getId())
                                    .setOrganizerId(organizer.getId())
                                    .setCheckType("IN")
                                    .setCourseId(course.getId());
                } else {
                    eventBeingEdited.data =
                            new Event()
                                    .setCheckId(check.getId())
                                    .setAttendeeId(attendee.getId())
                                    .setOrganizerId(organizer.getId())
                                    .setCheckType("IN")
                                    .setCourseId(course.getId());
                }

                final var savedOrUpdatedEvent = this.checkService.createEvent(eventBeingEdited.data);

                final var foundUser = this.userService.findUserById(savedOrUpdatedEvent.getAttendeeId());
                if (foundUser.isPresent())
                    this.attendeesGrid.setItems(this.checkService.findAllCheckinDetailsOfToday());

                Notification.show(
                                attendee.getFirstName()
                                        + " "
                                        + attendee.getLastName()
                                        + " heeft ingecheckt "
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

    private void checkOutUser(final String username, final Double lat, final Double lon, final Course course, final User organizer) {

        final var oAttendee = this.userService.findByUsername(username);
        if (oAttendee.isPresent()) {
            final var attendee = oAttendee.get();

            final var foundCheck =
                    this.checkService.findChecksByCheckedOnAndQrcode(Date.valueOf(LocalDate.now()), username);

            if (foundCheck.isPresent()) {

                final var check =
                        this.checkService.createCheck(
                                foundCheck
                                        .get()
                                        .setCurrentSession(VaadinSession.getCurrent().getSession().getId())
                                        .setQrcode(username)
                                        .setCheckedOutAt(Time.valueOf(LocalTime.now()))
                                        .setLat(lat)
                                        .setLon(lon));

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
                                    .setCheckId(check.getId())
                                    .setAttendeeId(attendee.getId())
                                    .setOrganizerId(organizer.getId())
                                    .setCheckType("OUT")
                                    .setCourseId(course.getId());
                } else {
                    eventBeingEdited.data =
                            new Event()
                                    .setCheckId(check.getId())
                                    .setAttendeeId(attendee.getId())
                                    .setOrganizerId(organizer.getId())
                                    .setCheckType("OUT")
                                    .setCourseId(course.getId());
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
                                ("Geannuleerd, GEEN valid QR-code: " + username),
                                4000,
                                Notification.Position.BOTTOM_CENTER)
                        .open();
            }

        } else {
            Notification.show(
                            (username + " heeft nog NIET gecheckt."),
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

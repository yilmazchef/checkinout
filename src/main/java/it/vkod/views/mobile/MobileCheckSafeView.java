package it.vkod.views.mobile;

import com.flowingcode.vaadin.addons.twincolgrid.TwinColGrid;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import it.vkod.models.dto.CheckDetails;
import it.vkod.models.entity.Check;
import it.vkod.models.entity.Event;
import it.vkod.models.entity.User;
import it.vkod.models.http.CheckType;
import it.vkod.models.http.TrainingCode;
import it.vkod.services.flow.AuthenticationService;
import it.vkod.services.flow.CheckService;
import it.vkod.services.flow.UserService;
import org.vaadin.elmot.flow.sensors.GeoLocation;

import javax.annotation.security.RolesAllowed;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static com.vaadin.flow.component.notification.NotificationVariant.LUMO_ERROR;
import static com.vaadin.flow.component.notification.NotificationVariant.LUMO_SUCCESS;

@PageTitle("Inchecken/Uitchecken")
@Route(value = "m/safe", layout = MobileTemplateLayout.class)
@RouteAlias(value = "mobile/failsafe", layout = MobileTemplateLayout.class)
@RolesAllowed({ "ADMIN", "MANAGER", "LEADER" })
public class MobileCheckSafeView extends VerticalLayout implements HasUrlParameter<String> {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final CheckService checkService;

    public MobileCheckSafeView(
            AuthenticationService authenticationService, UserService userService,
            CheckService checkService) {

        this.authenticationService = authenticationService;
        this.userService = userService;
        this.checkService = checkService;

        initStyle();

    }

    private void initializeFailsafeLayout(final String training, final Set<User> students, final User organizer) {

        final var studentsGrid = new TwinColGrid<>(students, "Failsafe aanmelden")
                .addColumn(item -> String.valueOf(item.getId()), "ID")
                .addColumn(User::getFirstName, "Voornaam")
                .addColumn(User::getLastName, "Familienaam")
                .addColumn(User::getEmail, "Email")
                .withSelectionGridCaption("Ingecheckt")
                .withAvailableGridCaption("Alles")
                .withoutAddAllButton()
                .withSizeFull()
                .withDragAndDropSupport();
        studentsGrid.setHeight("50vh");

        final var attendeesGrid = new Grid<CheckDetails>();
        attendeesGrid.setColumnReorderingAllowed(true);

        final var dateTimePicker = new DateTimePicker();
        dateTimePicker.setLabel("Appointment date and time");
        dateTimePicker.setHelperText("Must be within 60 days from today");
        dateTimePicker.setAutoOpen(true);
        dateTimePicker.setMin(LocalDateTime.now().minusDays(1));
        dateTimePicker.setMax(LocalDateTime.now().plusDays(7));
        dateTimePicker.setValue(LocalDateTime.now());

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

        final var checkInButton = new Button(VaadinIcon.SIGN_IN_ALT.create(),
                onClick -> checkInUser(usernameField.getValue(),
                        geoLocation.getValue().getLatitude(), geoLocation.getValue().getLongitude(),
                        organizer,
                        training,
                        attendeesGrid));

        final var checkOutButton = new Button(VaadinIcon.SIGN_OUT_ALT.create(),
                onClick -> checkOutUser(
                        usernameField.getValue(),
                        geoLocation.getValue().getLatitude(), geoLocation.getValue().getLongitude(),
                        organizer,
                        training,
                        attendeesGrid));

        usernameField.addValueChangeListener(onValueChange -> {
            checkInButton.setEnabled(!onValueChange.getValue().isEmpty());
            checkOutButton.setEnabled(!onValueChange.getValue().isEmpty());
        });

        failSafeForm.add(usernameField, dateTimePicker, checkInButton, checkOutButton);
        failsafeLayout.add(failSafeForm, studentsGrid);

        addAndExpand(failsafeLayout, attendeesGrid, geoLocation);
    }

    private Notification notify(String errorMessage) {
        return notify(errorMessage, LUMO_SUCCESS);
    }

    private Notification notify(String errorMessage, NotificationVariant variant) {

        final var notification = new Notification();
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.MIDDLE);

        final var text = new Div(new Text(errorMessage));

        final var closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> notification.close());

        final var layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(Alignment.CENTER);

        notification.add(layout);
        return notification;
    }

    private void checkInUser(final String username,
            final Double lat, final Double lon,
            final User organizer,
            final String training,
            final Grid<CheckDetails> attendeesGrid) {

        final var oAttendee = this.userService.findByUsername(username);
        if (oAttendee.isPresent()) {

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

                final var oEvent = this.checkService.fetchByAttendeeIdAndCheckId(
                        attendee.getId(), check.getId(), "IN");

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
                    attendeesGrid.setItems(this.checkService.fetchInDetailsToday());
                }

                notify(attendee.getFirstName()
                        + " "
                        + attendee.getLastName()
                        + " heeft ingecheckt "
                        + "."
                        + "Organisator: "
                        + organizer.getFirstName()
                        + " "
                        + organizer.getLastName())
                                .open();
            } else {

                notify((username + " heeft vroeger al gecheckt."), LUMO_ERROR).open();
            }

        } else {
            notify((username + " is GEEN valid QR-code."), LUMO_ERROR).open();
        }
    }

    private void checkOutUser(final String username,
            final Double lat, final Double lon,
            final User organizer,
            final String training,
            final Grid<CheckDetails> attendeesGrid) {

        final var oAttendee = this.userService.findByUsername(username);
        if (oAttendee.isPresent()) {
            final var attendee = oAttendee.get();

            final var foundCheck = this.checkService.fetchByDate(Date.valueOf(LocalDate.now()), username);

            if (foundCheck.isPresent()) {

                final var check = this.checkService.createCheck(
                        foundCheck
                                .get()
                                .setCurrentSession(VaadinSession.getCurrent().getSession().getId())
                                .setQrcode(username)
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
                if (!savedOrUpdatedEvent.isNew()) {
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

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {

        final var location = event.getLocation();
        final var queryParameters = location.getQueryParameters();
        final Map<String, List<String>> parametersMap = queryParameters.getParameters();

        final var user = this.authenticationService.get();
        final var types = parametersMap.get(CheckType.IN.getName());
        final String typeParam = (types != null && !types.isEmpty()) ? parametersMap.get(CheckType.IN.getName()).get(0)
                : CheckType.IN.getValue();
        final String trainingParam = (parametersMap.get(TrainingCode.QUERY.getName()) != null
                && !parametersMap.get(TrainingCode.QUERY.getName()).isEmpty())
                        ? parametersMap.get(TrainingCode.QUERY.getName()).get(0)
                        : user.orElseThrow(null).getCurrentTraining();

        if (user.isPresent()) {
            final var students = this.userService.fetchAll();
            initializeFailsafeLayout(trainingParam, students, user.get());
        }
    }
}
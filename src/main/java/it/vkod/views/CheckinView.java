package it.vkod.views;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import com.wontlost.zxing.Constants;
import com.wontlost.zxing.ZXingVaadinReader;
import it.vkod.data.entity.Check;
import it.vkod.data.entity.Course;
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

        user.ifPresent(
                organizer -> {

                    final ChecksGrid attendeesGrid = new ChecksGrid(this.checkService);

                    final var courseSelect = new Select<Course>();
                    courseSelect.setLabel("Selecteer een course");
                    courseSelect.setItemLabelGenerator(course -> course.getTitle());
                    courseSelect.setItems(checkService.fetchCourse());

                    final var locationLayout = new VerticalLayout();
                    locationLayout.setMargin(false);
                    locationLayout.setPadding(false);
                    locationLayout.setSpacing(false);

                    final var geoLocation = new GeoLocation();
                    geoLocation.setWatch(true);
                    geoLocation.setHighAccuracy(true);
                    geoLocation.setTimeout(100000);
                    geoLocation.setMaxAge(200000);

                    locationLayout.add(geoLocation);

                    final var leftLayout = new VerticalLayout();
                    initLayoutStyle(leftLayout, "35vw");
                    final var reader = new ZXingVaadinReader();

                    reader.setFrom(Constants.From.camera);
                    reader.setId("video"); // id needs to be 'video' if From.camera.
                    reader.setStyle("object-fit: cover; width:35vw; height:65vh; max-height:35vw");

                    reader.addValueChangeListener(scannedQRCode -> checkInUser(
                            scannedQRCode.getValue(),
                            geoLocation.getValue().getLatitude(), geoLocation.getValue().getLongitude(),
                            courseSelect.getValue(), organizer,
                            attendeesGrid));

                    leftLayout.add(reader, locationLayout);

                    final var rightLayout = new VerticalLayout();
                    initLayoutStyle(rightLayout, "55vw");

                    rightLayout.add(courseSelect, attendeesGrid);
                    splitLayout.add(leftLayout, rightLayout);

                });


        add(splitLayout);
    }

    private void initLayoutStyle(VerticalLayout layout, String width) {
        layout.setMargin(false);
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getStyle().set("margin-top", "4vh");
        layout.setWidth(width);
        layout.setHeight("90vh");
    }

    private void checkInUser(final String username,
                             final Double lat, final Double lon,
                             final Course course, final User organizer,
                             final ChecksGrid attendeesGrid) {

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
                if (foundUser.isPresent() && attendeesGrid != null) {
                    attendeesGrid.setItems(checkService.findAllCheckinDetailsOfToday());
                }

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

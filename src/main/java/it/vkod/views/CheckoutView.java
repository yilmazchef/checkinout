package it.vkod.views;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import com.wontlost.zxing.Constants;
import com.wontlost.zxing.ZXingVaadinReader;
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

@PageTitle("Uitchecken")
@Route(value = "out", layout = TemplateLayout.class)
@RouteAlias(value = "checkout", layout = TemplateLayout.class)
@PermitAll
public class CheckoutView extends VerticalLayout {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final CheckService checkService;

    private final HorizontalLayout splitLayout = new HorizontalLayout();

    public CheckoutView(
            AuthenticationService authenticationService, UserService userService,
            CheckService checkService) {

        this.userService = userService;
        this.checkService = checkService;
        this.authenticationService = authenticationService;

        initParentStyle();

        final var user = this.authenticationService.get();

        user.ifPresentOrElse(organizer -> {

            final var attendeesGrid = new ChecksGrid(this.checkService);

            final var coursesBox = new ComboBox<Course>("Courses");
            coursesBox.setItemLabelGenerator(course -> course.getTitle());
            coursesBox.setItems(checkService.fetchCourse());

            final var leftLayout = new VerticalLayout();
            final var reader = new ZXingVaadinReader();

            reader.setFrom(Constants.From.camera);
            reader.setId("video"); // id needs to be 'video' if From.camera.
            reader.setStyle("object-fit: cover; width:35vw; height:65vh; max-height:45vw");

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

            reader.addValueChangeListener(scannedQRCode -> checkOutUser(
                    scannedQRCode.getValue(),
                    geoLocation.getValue().getLatitude(),
                    geoLocation.getValue().getLongitude(),
                    coursesBox.getValue(), organizer,
                    attendeesGrid));

            initLayoutStyle(leftLayout, "35vw");

            leftLayout.add(reader, locationLayout);

            final var rightLayout = new VerticalLayout();
            initLayoutStyle(rightLayout, "55vw");


            rightLayout.add(coursesBox, attendeesGrid);

            splitLayout.add(leftLayout, rightLayout);

            add(splitLayout);

        }, () -> {
            Notification.show("Er is GEEN check-data.").open();
        });

    }

    private void checkOutUser(final String scannedQRCode,
                              final Double lat, final Double lon,
                              final Course course, final User organizer,
                              final ChecksGrid attendeesGrid) {

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
                                        .setCurrentSession(VaadinSession.getCurrent().getSession().getId())
                                        .setQrcode(scannedQRCode)
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
                if (!savedOrUpdatedEvent.isNew() && attendeesGrid != null) {
                    attendeesGrid.setItems(this.checkService.findCheckoutDetailsOfToday());
                }

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

    private void initParentStyle() {

        addClassNames("flex", "flex-col", "h-full");
        setMargin(false);
        setPadding(false);
        setSpacing(false);
        setWidthFull();

        setJustifyContentMode(JustifyContentMode.CENTER);
        setHorizontalComponentAlignment(Alignment.CENTER);
        setAlignItems(Alignment.CENTER);
    }

    private void initLayoutStyle(VerticalLayout layout, String width) {
        layout.setMargin(false);
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getStyle().set("margin-top", "4vh");
        layout.setWidth(width);
        layout.setHeight("90vh");
    }
}

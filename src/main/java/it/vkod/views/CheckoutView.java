package it.vkod.views;

import com.vaadin.flow.component.UI;
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
import it.vkod.data.dto.ChecksGridData;
import it.vkod.data.entity.Event;
import it.vkod.data.entity.User;
import it.vkod.repositories.CheckRepository;
import it.vkod.repositories.EventRepository;
import it.vkod.repositories.UserRepository;
import it.vkod.security.AuthenticatedUser;
import it.vkod.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;

import javax.annotation.security.PermitAll;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import static com.vaadin.flow.component.notification.Notification.Position.BOTTOM_CENTER;
import static com.vaadin.flow.component.notification.Notification.show;

@PageTitle("Check-out")
@Route("out")
@RouteAlias("checkout")
@PermitAll
public class CheckoutView extends VerticalLayout {

    private final AuthenticatedUser authenticatedUser;
    private final UserRepository userRepository;
    private final CheckRepository checkRepository;
    private final EventRepository eventRepository;
    private final EmailService emailService;

    private final SplitLayout splitLayout = new SplitLayout();
    private final Grid<ChecksGridData> attendeesGrid = new Grid<>();
    private User organizer;

    public CheckoutView(
            @Autowired AuthenticatedUser authenticatedUser,
            @Autowired UserRepository userRepository,
            @Autowired CheckRepository checkRepository,
            @Autowired EventRepository eventRepository,
            @Autowired EmailService emailService) {

        initStyle();

        this.authenticatedUser = authenticatedUser;
        this.userRepository = userRepository;
        this.checkRepository = checkRepository;
        this.eventRepository = eventRepository;
        this.emailService = emailService;

        final var user = authenticatedUser.get();

        user.ifPresent(
                u -> {
                    this.organizer = u;

                    attendeesGrid.setWidthFull();
                    attendeesGrid.setHeightFull();

                    attendeesGrid
                            .addColumn(ChecksGridData::getFirstName)
                            .setHeader("First Name")
                            .setKey("firstName");

                    attendeesGrid
                            .addColumn(ChecksGridData::getLastName)
                            .setHeader("LastName")
                            .setKey("lastName");
                    attendeesGrid.addColumn(ChecksGridData::getEmail).setHeader("Email").setKey("email");

                    attendeesGrid
                            .addColumn(ChecksGridData::getCheckedOn)
                            .setHeader("CheckedOn")
                            .setKey("checked_on");

                    attendeesGrid
                            .addColumn(ChecksGridData::getCheckedInAt)
                            .setHeader("CheckedInAt")
                            .setKey("checked_in_at");

                    attendeesGrid
                            .addColumn(ChecksGridData::getCheckedOutAt)
                            .setHeader("CheckedOutAt")
                            .setKey("checked_out_at");

                    attendeesGrid.addThemeVariants(
                            GridVariant.LUMO_NO_BORDER,
                            GridVariant.LUMO_NO_ROW_BORDERS,
                            GridVariant.LUMO_ROW_STRIPES);

                    final var checksGridDataList = checkRepository.findAllCheckoutsOfToday();
                    attendeesGrid.setItems(checksGridDataList);

                    final var scanLayout = new VerticalLayout();
                    final var reader = new ZXingVaadinReader();

                    reader.setFrom(Constants.From.camera);
                    reader.setId("video"); // id needs to be 'video' if From.camera.
                    reader.setStyle("object-fit: cover; width:100; height:100vh; max-height:100");

                    reader.addValueChangeListener(scannedQRCode -> checkOutUser(scannedQRCode.getValue()));

                    scanLayout.setMargin(false);
                    scanLayout.setPadding(false);
                    scanLayout.setJustifyContentMode(JustifyContentMode.CENTER);
                    scanLayout.setAlignItems(Alignment.CENTER);
                    scanLayout.add(reader);

                    final var changesLayout = new VerticalLayout();
                    final var failSafeLayout = new FormLayout();

                    final var usernameField = new TextField();
                    usernameField.setLabel("Student Username");
                    usernameField.setRequired(true);

                    final var failSafeRegisterButton =
                            new Button("Check OUT Manually", onClick -> checkOutUser(usernameField.getValue()));

                    failSafeLayout.add(usernameField, failSafeRegisterButton);
                    changesLayout.add(failSafeLayout, attendeesGrid);

                    final var sendMailButton = createEmailButton();
                    final var exportToApiButton = sendExportRequest();

                    changesLayout.add(attendeesGrid, sendMailButton, exportToApiButton);

                    splitLayout.addToPrimary(scanLayout);
                    splitLayout.addToSecondary(changesLayout);
                });

        splitLayout.setSplitterPosition(80);
        add(splitLayout);
    }

    private Button sendExportRequest() {
        return new Button("Export to CSV", onClick -> UI.getCurrent().getPage().open("checks/csv"));
    }

    private Button createEmailButton() {
        return
                new Button(
                        "Send Mail",
                        onClick -> {
                            try {
                                final var mailContent =
                                        Arrays.deepToString(checkRepository.findAllChecksOfToday().toArray());
                                this.emailService.sendSimpleMessage(
                                        "yilmaz.brievenbus@gmail.com",
                                        "Aanwezigheidslijst " + LocalDate.now(),
                                        mailContent);

                                Notification.show(mailContent, 5000, Notification.Position.BOTTOM_CENTER)
                                        .open();
                            } catch (MailException mailException) {
                                show(mailException.getMessage(), 4000, BOTTOM_CENTER).open();
                            }
                        });
    }

    private void checkOutUser(final String scannedQRCode) {

        final var oAttendee = userRepository.findByUsername(scannedQRCode);
        if (oAttendee.isPresent()) {
            final var attendee = oAttendee.get();

            final var foundCheck =
                    checkRepository.findByCheckedOnAndQrcode(Date.valueOf(LocalDate.now()), scannedQRCode);

            if (foundCheck.isPresent()) {

                final var check =
                        checkRepository.save(
                                foundCheck
                                        .get()
                                        .withCurrentSession(VaadinSession.getCurrent().getSession().getId())
                                        .withQrcode(scannedQRCode)
                                        .withCheckedOutAt(Time.valueOf(LocalTime.now()))
                                        .withLat(20.00F)
                                        .withLon(20.00F));

                final var oEvent =
                        eventRepository.findByAttendeeIdAndCheckIdAndCheckType(
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

                final var savedOrUpdatedEvent = eventRepository.save(eventBeingEdited.data);
                if (!savedOrUpdatedEvent.isNew())
                    attendeesGrid.setItems(checkRepository.findAllCheckoutsOfToday());

                Notification.show(
                                attendee.getFirstName()
                                        + " "
                                        + attendee.getLastName()
                                        + " has checked OUT"
                                        + "."
                                        + "Organized by "
                                        + organizer.getFirstName()
                                        + " "
                                        + organizer.getLastName(),
                                4000,
                                Notification.Position.BOTTOM_CENTER)
                        .open();

            } else {
                Notification.show(
                                ("Rejected (Invalid or Unregistered QR): " + scannedQRCode),
                                4000,
                                Notification.Position.BOTTOM_CENTER)
                        .open();
            }

        } else {
            Notification.show(
                            ("The following user has NOT checked in (?): " + scannedQRCode),
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

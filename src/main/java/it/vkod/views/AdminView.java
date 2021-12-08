package it.vkod.views;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.vkod.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import java.util.concurrent.atomic.AtomicLong;

@PageTitle("System Admin - Full Access")
@Route(value = "adm", layout = TemplateLayout.class)
@RolesAllowed({"ADMIN", "MANAGER", "LEADER"})
public class AdminView extends VerticalLayout {


    private final String hostname;
    private final AdminService adminService;

    private static final String URL_PDF = "/checks/pdf";
    private static final String URL_CSV = "/checks/csv";
    private static final String URL_EXCEL = "/checks/excel";

    public AdminView(@Autowired AdminService adminService) {

        this.adminService = adminService;
        this.hostname = System.getenv("hostname");

        Notification.show("The app is running on " + hostname, 5000, Notification.Position.TOP_CENTER).open();

        final var managementLayout = new VerticalLayout();

        final AtomicLong deleteCounter = new AtomicLong();
        final var flushButton = new Button("Flush Database", onClick -> {
            final var flushLayout = new VerticalLayout();
            final var confirmationDialog = new Dialog();
            confirmationDialog.setCloseOnEsc(true);
            confirmationDialog.setOpened(true);
            final var info = new H4("Please confirm database flush operation?");
            flushLayout.add(info);
            final var confirmationLayout = new HorizontalLayout();
            final var yesButton = new Button("Yes", yesClicked -> {
                long deletedCount = adminService.flushDatabase();
                deleteCounter.set(deletedCount);
                confirmationDialog.close();
            });
            final var noButton = new Button("No", noClicked -> {
                deleteCounter.lazySet(0);
                confirmationDialog.close();
            });

            confirmationLayout.add(yesButton, noButton);
            flushLayout.add(confirmationLayout);
            confirmationDialog.add(confirmationLayout);

            add(flushLayout);

        });

        managementLayout.add(flushButton);

        final var actionLayout = new VerticalLayout();

        Anchor pdfAnchor = new Anchor(hostname.replaceFirst("/null", "") + URL_PDF, "Exporteer als PDF");
        Anchor csvAnchor = new Anchor(hostname.replaceFirst("/null", "") + URL_CSV, "Exporteer als CSV");
        Anchor excelAnchor = new Anchor(hostname.replaceFirst("/null", "") + URL_EXCEL, "Exporteer als PDF");

        actionLayout.add(pdfAnchor, csvAnchor, excelAnchor);

        add(managementLayout, actionLayout);

    }

}

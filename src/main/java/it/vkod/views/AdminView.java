package it.vkod.views;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
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

    private final AdminService adminService;
    private static final String URL_PDF = "http://localhost:8080/checks/pdf";
    private static final String URL_CSV = "http://localhost:8080/checks/csv";
    private static final String URL_EXCEL = "http://localhost:8080/checks/excel";

    public AdminView(@Autowired AdminService adminService) {

        this.adminService = adminService;

        final var managementLayout = new VerticalLayout();

        final AtomicLong deleteCounter = new AtomicLong();
        final var flushButton = new Button("Flush Database", onClick -> {
            long deletedCount = adminService.flushDatabase();
            deleteCounter.set(deletedCount);
        });

        managementLayout.add(flushButton);

        final var actionLayout = new VerticalLayout();

        Anchor pdfAnchor = new Anchor(URL_PDF, "Exporteer als PDF");
        Anchor csvAnchor = new Anchor(URL_CSV, "Exporteer als CSV");
        Anchor excelAnchor = new Anchor(URL_EXCEL, "Exporteer als PDF");

        actionLayout.add(pdfAnchor, csvAnchor, excelAnchor);

        add(managementLayout, actionLayout);

    }

}

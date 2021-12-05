package it.vkod.views;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.vkod.api.ExportController;
import it.vkod.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import java.util.concurrent.atomic.AtomicLong;

@PageTitle("System Admin - Full Access")
@Route(value = "adm", layout = TemplateLayout.class)
@RolesAllowed({"ADMIN", "MANAGER", "LEADER"})
public class AdminView extends VerticalLayout {

    private final AdminService adminService;


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

        final var pdfButton = new Button("Export PDF", onClick -> {
            UI.getCurrent().navigate(ExportController.EXPORT_CHECKS_PDF_URI);
        });

        final var csvButton = new Button("Export CSV", onClick -> {
            UI.getCurrent().navigate(ExportController.EXPORT_CHECKS_CSV_URI);
        });

        final var excelButton = new Button("Export EXCEL", onClick -> {
            UI.getCurrent().navigate(ExportController.EXPORT_CHECKS_EXCEL_URI);
        });

        actionLayout.add(pdfButton, csvButton, excelButton);

        add(managementLayout, actionLayout);

    }

}

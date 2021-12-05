package it.vkod.views;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
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


        String baseURL = RouteConfiguration.forSessionScope().getUrl(AdminView.class);
        final var baseAbsoluteURL = baseURL.concat("/").concat("checks").concat("/")
                .replace("adm/", "");
        
        Anchor pdfAnchor = new Anchor(
                baseAbsoluteURL.concat("pdf"),
                "Exporteer als PDF");

        Anchor csvAnchor = new Anchor(
                baseAbsoluteURL.concat("csv"),
                "Exporteer als CSV");

        Anchor excelAnchor = new Anchor(
                baseAbsoluteURL.concat("excel"),
                "Exporteer als PDF");

        actionLayout.add(pdfAnchor, csvAnchor, excelAnchor);

        add(managementLayout, actionLayout);

    }

}

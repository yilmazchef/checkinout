package it.vkod.api;

import it.vkod.repositories.CheckRepository;
import it.vkod.repositories.EventRepository;
import it.vkod.repositories.UserRepository;
import it.vkod.services.AuthenticationService;
import it.vkod.services.ExportService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Controller
@RolesAllowed({"MANAGER", "ADMIN"})
public class ExportController {

    public static final String EXPORT_CHECKS_CSV_URI = "/checks/csv";
    public static final String EXPORT_CHECKS_PDF_URI = "/checks/pdf";
    public static final String EXPORT_USERS_PDF_URI = "/users/pdf";
    public static final String EXPORT_CHECKS_EXCEL_URI = "/checks/xlsx";

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final CheckRepository checkRepository;
    private final EventRepository eventRepository;
    private final ExportService exportService;


    @SneakyThrows
    @GetMapping(EXPORT_CHECKS_CSV_URI)
    public void toCSV(HttpServletResponse response) {
        exportService.toCSV(response, checkRepository.findAllChecksOfToday());
    }

    @SneakyThrows
    @GetMapping(EXPORT_CHECKS_PDF_URI)
    public void toPDF(HttpServletResponse response) {
        exportService.toPDF(response, checkRepository.findAllChecksOfToday(), authenticationService.get().get());
    }

    @SneakyThrows
    @GetMapping(EXPORT_CHECKS_EXCEL_URI)
    public void toExcel(HttpServletResponse response) {
        exportService.toExcel(response, checkRepository.findAllChecksOfToday());
    }

    @SneakyThrows
    @GetMapping(EXPORT_USERS_PDF_URI)
    public void toQRPrint(HttpServletResponse response) {
        exportService.toPDF(response, userRepository.findAllUsernames().toArray(String[]::new));

    }


}

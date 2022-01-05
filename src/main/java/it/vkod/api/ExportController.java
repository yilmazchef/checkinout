package it.vkod.api;


import it.vkod.models.entities.Course;
import it.vkod.models.entities.User;
import it.vkod.repositories.CheckRepository;
import it.vkod.repositories.UserRepository;
import it.vkod.services.flow.ExportService;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = ExportController.API)
@RolesAllowed({"MANAGER", "ADMIN"})
public class ExportController {

    private final UserRepository userRepository;
    private final CheckRepository checkRepository;
    private final ExportService exportService;

    public static final String API = "/api/v1/export";
    public static final String CSV = "/checks/csv";
    public static final String PDF = "/checks/pdf";
    public static final String EXCEL = "/checks/xlsx";
    public static final String USERS = "/users/pdf";

    public ExportController(UserRepository userRepository, CheckRepository checkRepository, ExportService exportService) {
        this.userRepository = userRepository;
        this.checkRepository = checkRepository;
        this.exportService = exportService;
    }


    @SneakyThrows
    @GetMapping(value = CSV + "/{course}")
    public void toCSV(HttpServletResponse response, @PathVariable final Course course) {

        exportService.toCSV(response, checkRepository.findAllTodayByCourse(course));
    }


    @SneakyThrows
    @GetMapping(PDF + "/{course}")
    public void toPDF(HttpServletResponse response, @PathVariable final Course course) {

        exportService.toPDF(response, checkRepository.findAllTodayByCourse(course));
    }


    @SneakyThrows
    @GetMapping(EXCEL + "/{course}")
    public void toExcel(HttpServletResponse response, @PathVariable final Course course) {

        exportService.toExcel(response, checkRepository.findAllTodayByCourse(course));
    }


    @SneakyThrows
    @GetMapping(value = USERS + "/{course}")
    public void toQRPrint(HttpServletResponse response, @PathVariable final Course course) {

        exportService.toPDF(response, userRepository.findAllByCourse(course).stream().map(User::getUsername).collect(Collectors.toUnmodifiableSet()));

    }


}

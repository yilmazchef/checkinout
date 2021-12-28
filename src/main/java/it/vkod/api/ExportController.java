package it.vkod.api;


import it.vkod.models.entities.User;
import it.vkod.repositories.CheckRepository;
import it.vkod.repositories.UserRepository;
import it.vkod.services.flow.ExportService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;

@RequiredArgsConstructor
@RestController
@RequestMapping( path = "api/v1/export/" )
@RolesAllowed( { "MANAGER", "ADMIN" } )
public class ExportController {

	public static final String EXPORT_CHECKS_CSV_URI = "checks/csv";
	public static final String EXPORT_USERS_PDF_URI = "users/pdf";
	public static final String EXPORT_CHECKS_EXCEL_URI = "checks/xlsx";

	private final UserRepository userRepository;
	private final CheckRepository checkRepository;
	private final ExportService exportService;


	@SneakyThrows
	@GetMapping( value = "/checks/csv/{course}" )
	public void toCSV( HttpServletResponse response, @PathVariable final String course ) {

		exportService.toCSV( response, checkRepository.findAllByCourse( course ) );
	}


	@SneakyThrows
	@GetMapping( "/checks/pdf/{course}" )
	public void toPDF( HttpServletResponse response, @PathVariable final String course ) {

		exportService.toPDF( response, checkRepository.findAllByCourse( course ) );
	}


	@SneakyThrows
	@GetMapping( "/checks/xlsx/{course}" )
	public void toExcel( HttpServletResponse response, @PathVariable final String course ) {

		exportService.toExcel( response, checkRepository.findAllByCourse( course ) );
	}


	@SneakyThrows
	@GetMapping( value = "/users/pdf/{course}" )
	public void toQRPrint( HttpServletResponse response, @PathVariable final String course ) {

		exportService.toPDF( response, userRepository.findAllByCourse( course ).stream().map( User::getUsername ).collect( Collectors.toUnmodifiableSet() ) );

	}


}

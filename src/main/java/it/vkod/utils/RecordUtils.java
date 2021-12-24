package it.vkod.utils;


import com.vaadin.flow.spring.annotation.SpringComponent;
import it.vkod.models.entities.User;
import it.vkod.models.entities.UserRole;
import it.vkod.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;
import java.util.Set;

import static it.vkod.models.entities.UserRole.*;

@SpringComponent
@RequiredArgsConstructor
public class RecordUtils {

	private static final Logger logger = LoggerFactory.getLogger( RecordUtils.class );

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;


	@Bean
	public CommandLineRunner initUserRecords() {

		return args -> {

			logger.info( "Generating pre-defined user records for testing environment. Deactivate this on production." );

			if ( userRepository.count() <= 0L ) {

				mock( "wouter.vandenberge", "Wouter", "Van den Berge", ADMIN );

				mock( "pearl.de.smet", "Pearl", "De Smet", MANAGER, TEACHER );
				mock( "cindy.pipeleers", "Cindy", "Pipeleers" );

				mock( "yilmaz.mustafa", "Yilmaz", "Mustafa", "JavaJun21", TEACHER );
				mock( "patrick.geudens", "Patrick", "Geudens", "JavaSep20", TEACHER );
				mock( "atilla.balin", "Atilla", "Balin", "FrontendJan21", TEACHER );
				mock( "manuel.cogneau", "Manuel", "Cogneau", "JavaJan21", TEACHER );
				mock( "benedikt.lantsoght", "Benedikt", "Lantsoght", "FrontendJun21", TEACHER );
				mock( "kenan.kurda", "Kenan", "Kurda", "CSharpJan21", TEACHER );
				mock( "alexander.keisse", "Alexander", "Keisse", "JavaSep21", TEACHER );
				mock( "quinten.declerck", "Quinten", "De Clerck", "CSharpJun21", TEACHER );

				mock( "hilal.demir", "Hilal", "Demir", "JavaJun21", STUDENT );
				mock( "ahmed.faqiri", "Ahmed", "Faqiri", "JavaJun21", STUDENT );
				mock( "alexandru.tudorache", "Alexandru", "Tudorache", "JavaJun21", STUDENT );
				mock( "sameerun.acchukatla", "Sameerun", "Acchukatla", "JavaJun21", STUDENT );
				mock( "rahime.yildiz", "Rahime", "Yildiz", "JavaJun21", STUDENT );
				mock( "sinan.hubeyb.cam", "Sinan", "Hubeyb Cam", "JavaJun21", STUDENT );
				mock( "joey.de.kort", "Joey", "De Kort", "JavaJun21", STUDENT );
				mock( "irina.afanassenko", "Irina", "Afanassenko", "JavaJun21", STUDENT );
				mock( "ali.jamal.alwasseti", "Ali Jamal", "Alwasseti", "JavaJun21", STUDENT );

			}

		};
	}


	private void mock( String username, String firstName, String lastName, String course, UserRole... roles ) {

		mock( username, firstName, lastName, "Administratie", roles );
	}


	private void mock( String username, String firstName, String lastName, UserRole... roles ) {

		User user = new User()
				.setUsername( username ).setPassword( passwordEncoder.encode( "P@ssw0rd" ) )
				.setProfile( "https://i.pravatar.cc/300" )
				.setFirstName( firstName ).setLastName( lastName )
				.setCourse( "Administratie" )
				.setEmail( username.concat( "@" ).concat( "intecbrussel.be" ) )
				.setPhone( String.format( "0467334%d", username.length() * new Random().nextInt( 1000 ) ) )
				.setRoles( Set.of( roles ) );

		logger.info( "New ADMIN is added: " + userRepository.save( user ) );
	}

}
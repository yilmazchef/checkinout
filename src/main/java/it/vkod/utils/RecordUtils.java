package it.vkod.utils;


import com.vaadin.flow.spring.annotation.SpringComponent;
import it.vkod.data.entity.User;
import it.vkod.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringComponent
@RequiredArgsConstructor
public class RecordUtils {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;


	@Bean
	public CommandLineRunner initUserRecords() {

		return args -> {
			Logger logger = LoggerFactory.getLogger( getClass() );
			if ( userRepository.count() != 0L ) {
				logger.info( "Using existing database" );
				return;
			}

			logger.info( "Generating pre-defined user records" );

			final var profileImg1 = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80";
			final var profileImg2 = "https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80";

			createUserRecord( "student", profileImg1, "John", "Doe", "USER" );

			createUserRecord( "yilmaz.mustafa", profileImg2, "Yilmaz", "Mustafa", "TEACHER" );
			createUserRecord( "patrick.geudens", profileImg1, "Patrick", "Geudens", "TEACHER" );
			createUserRecord( "atilla.balin", profileImg2, "Atilla", "Balin", "TEACHER" );
			createUserRecord( "manuel.cogneau", profileImg1, "Manuel", "Cogneau", "TEACHER" );
			createUserRecord( "benedikt.lantsoght", profileImg2, "Benedikt", "Lantsoght", "TEACHER" );
			createUserRecord( "kenan.kurda", profileImg2, "Kenan", "Kurda", "TEACHER" );
			createUserRecord( "alexander.keisse", profileImg1, "Alexander", "Keisse", "TEACHER" );
			createUserRecord( "quinten.declerck", profileImg2, "Quinten", "De Clerck", "TEACHER" );

			createUserRecord( "pearl.de.smet", profileImg2, "Pearl", "De Smet", "LEADER" );
			createUserRecord( "wouter.vandenberge", profileImg2, "Wouter", "Van den Berge", "ADMIN" );


		};
	}


	private void createUserRecord( String username, String profile, String firstName, String lastName, String roles ) {

		User user = new User().withUsername( username ).withHashedPassword( passwordEncoder.encode( "P@ssw0rd" ) )
				.withProfile( profile )
				.withFirstName( firstName ).withLastName( lastName )
				.withEmail( username.concat( "@" ).concat( "@intecbrussel.be" ) )
				.withPhone( "046733254" + username.length() )
				.withRoles( roles );

		userRepository.save( user );
	}

}
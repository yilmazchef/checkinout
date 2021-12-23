package it.vkod.utils;


import com.vaadin.flow.spring.annotation.SpringComponent;
import it.vkod.models.entities.User;
import it.vkod.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;

@SpringComponent
@RequiredArgsConstructor
public class RecordUtils {

	private static final Logger logger = LoggerFactory.getLogger( RecordUtils.class );

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;

	private static final String STUDENT_ROLE = "STUDENT";
	private static final String TEACHER_ROLE = "TEACHER";
	private static final String MANAGER_ROLE = "MANAGER";
	private static final String ADMIN_ROLE = "ADMIN";


	@Bean
	public CommandLineRunner initUserRecords() {

		return args -> {

			logger.info( "Generating pre-defined user records for testing environment. Deactivate this on production." );

			final var profileImg1 = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80";
			final var profileImg2 = "https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80";

			if ( userRepository.count() <= 0L ) {

				createAdminRecord( "wouter.vandenberge", profileImg2, "Wouter", "Van den Berge" );

				createManagerRecord( "pearl.de.smet", profileImg2, "Pearl", "De Smet" );
				createManagerRecord( "cindy.pipeleers", profileImg1, "Cindy", "Pipeleers" );

				createTeacherRecord( "yilmaz.mustafa", profileImg2, "Yilmaz", "Mustafa", "JavaJun21" );
				createTeacherRecord( "patrick.geudens", profileImg1, "Patrick", "Geudens", "JavaSep20" );
				createTeacherRecord( "atilla.balin", profileImg2, "Atilla", "Balin", "FrontendJan21" );
				createTeacherRecord( "manuel.cogneau", profileImg1, "Manuel", "Cogneau", "JavaJan21" );
				createTeacherRecord( "benedikt.lantsoght", profileImg2, "Benedikt", "Lantsoght", "FrontendJun21" );
				createTeacherRecord( "kenan.kurda", profileImg2, "Kenan", "Kurda", "CSharpJan21" );
				createTeacherRecord( "alexander.keisse", profileImg1, "Alexander", "Keisse", "JavaSep21" );
				createTeacherRecord( "quinten.declerck", profileImg2, "Quinten", "De Clerck", "CSharpJun21" );

				createStudentRecord( "hilal.demir", profileImg1, "Hilal", "Demir", "JavaJun21" );
				createStudentRecord( "ahmed.faqiri", profileImg2, "Ahmed", "Faqiri", "JavaJun21" );
				createStudentRecord( "alexandru.tudorache", profileImg1, "Alexandru", "Tudorache", "JavaJun21" );
				createStudentRecord( "sameerun.acchukatla", profileImg2, "Sameerun", "Acchukatla", "JavaJun21" );
				createStudentRecord( "rahime.yildiz", profileImg1, "Rahime", "Yildiz", "JavaJun21" );
				createStudentRecord( "sinan.hubeyb.cam", profileImg2, "Sinan", "Hubeyb Cam", "JavaJun21" );
				createStudentRecord( "joey.de.kort", profileImg1, "Joey", "De Kort", "JavaJun21" );
				createStudentRecord( "irina.afanassenko", profileImg2, "Irina", "Afanassenko", "JavaJun21" );
				createStudentRecord( "ali.jamal.alwasseti", profileImg2, "Ali Jamal", "Alwasseti", "JavaJun21" );

			}

		};
	}


	private void createAdminRecord( String username, String profile, String firstName, String lastName ) {

		User user = new User()
				.setUsername( username ).setPassword( passwordEncoder.encode( "P@ssw0rd" ) )
				.setProfile( profile )
				.setFirstName( firstName ).setLastName( lastName )
				.setCourse( "Administratie" )
				.setEmail( username.concat( "@" ).concat( "intecbrussel.be" ) )
				.setPhone( String.format( "0467334%d", username.length() * new Random().nextInt( 1000 ) ) )
				.setRoles( TEACHER_ROLE + "," + MANAGER_ROLE + "," + ADMIN_ROLE );

		final var savedUser = userRepository.save( user );
		logger.info( "New ADMIN is added: " + savedUser );
	}


	private void createManagerRecord( String username, String profile, String firstName, String lastName, String course ) {

		User user = new User()
				.setUsername( username ).setPassword( passwordEncoder.encode( "P@ssw0rd" ) )
				.setProfile( profile )
				.setFirstName( firstName ).setLastName( lastName )
				.setCourse( course )
				.setEmail( username.concat( "@" ).concat( "intecbrussel.be" ) )
				.setPhone( String.format( "0467334%d", username.length() * new Random().nextInt( 1000 ) ) )
				.setRoles( TEACHER_ROLE + "," + MANAGER_ROLE );

		final var savedUser = userRepository.save( user );
		logger.info( "New MANAGER is added: " + savedUser );
	}


	private void createManagerRecord( String username, String profile, String firstName, String lastName ) {

		createManagerRecord( username, profile, firstName, lastName, "Java Juni 21" );
	}


	private void createTeacherRecord( String username, String profile, String firstName, String lastName, String course ) {

		User entity = mock( username, profile, firstName, lastName, course, TEACHER_ROLE );
		logger.info( "New TEACHER is added: " + entity );
	}


	private void createStudentRecord( String username, String profile, String firstName, String lastName, String course ) {

		User entity = mock( username, profile, firstName, lastName, course, STUDENT_ROLE );
		logger.info( "New STUDENT is added: " + entity );
	}


	private User mock( final String username, final String profile, final String firstName, final String lastName, final String course, final String studentRole ) {

		return userRepository.save( new User()
				.setUsername( username ).setPassword( passwordEncoder.encode( "P@ssw0rd" ) )
				.setProfile( profile )
				.setFirstName( firstName ).setLastName( lastName )
				.setCourse( course )
				.setEmail( username.concat( "@" ).concat( "intecbrussel.be" ) )
				.setPhone( String.format( "0467334%d", username.length() * new Random().nextInt( 1000 ) ) )
				.setRoles( studentRole ) );
	}

}
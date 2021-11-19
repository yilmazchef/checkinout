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

			createUserRecord( "student",
					"https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80",
					"John", "Doe",
					"USER" );

			createUserRecord( "yilmaz.mustafa",
					"https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80",
					"Yilmaz", "Mustafa",
					"ADMIN" );

			createUserRecord( "pearl.de.smet",
					"https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80",
					"Pearl", "De Smet",
					"ADMIN" );

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
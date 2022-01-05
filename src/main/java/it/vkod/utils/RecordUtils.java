package it.vkod.utils;


import com.vaadin.flow.spring.annotation.SpringComponent;
import it.vkod.models.entities.Course;
import it.vkod.models.entities.Role;
import it.vkod.models.entities.User;
import it.vkod.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static it.vkod.models.entities.Role.*;

@SpringComponent
@RequiredArgsConstructor
public class RecordUtils {

    private static final Logger logger = LoggerFactory.getLogger(RecordUtils.class);

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;


    @Bean
    public CommandLineRunner initUserRecords() {

        return args -> {

            logger.info("Generating pre-defined user records for testing environment. Deactivate this on production.");

            if (userRepository.count() <= 0L) {

                mock("wouter.vandenberge", "Wouter", "Van den Berge", Course.ONGEDEFINEERD, ADMIN);

                mock("pearl.de.smet", "Pearl", "De Smet", Course.ONGEDEFINEERD, MANAGER, TEACHER);
                mock("cindy.pipeleers", "Cindy", "Pipeleers", Course.ONGEDEFINEERD, MANAGER);

                mock("yilmaz.mustafa", "Yilmaz", "Mustafa", Course.BO_JAVA_21_JUNI, TEACHER);

            }

        };
    }


    private void mock(String username, String firstName, String lastName, Course course, Role... roles) {

        final var entity = userRepository.save(new User()
                .setUsername(username).setPassword(passwordEncoder.encode("P@ssw0rd"))
                .setProfile("https://i.pravatar.cc/300")
                .setFirstName(firstName).setLastName(lastName)
                .setCourse(course)
                .setEmail(username.concat("@").concat("intecbrussel.be"))
                .setPhone(String.format("+3246734%d", username.length() * new Random().nextInt(8999) + 1000))
                .setRoles(Set.of(roles)));

        logger.info(Arrays.stream(roles).map(Enum::name).collect(Collectors.joining(",")) + " is added: " + entity);
    }

}
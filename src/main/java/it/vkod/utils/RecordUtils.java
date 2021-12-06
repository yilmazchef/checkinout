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

import java.util.Random;

@SpringComponent
@RequiredArgsConstructor
public class RecordUtils {

    private static final Logger logger = LoggerFactory.getLogger(RecordUtils.class);

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;


    @Bean
    public CommandLineRunner initUserRecords() {

        final String STUDENT_ROLE = "STUDENT";
        final String TEACHER_ROLE = "TEACHER";
        final String MANAGER_ROLE = "MANAGER";
        final String ADMIN_ROLE = "ADMIN";

        return args -> {
            if (userRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }

            logger.info("Generating pre-defined user records for testing environment. Deactivate this on production.");

            final var profileImg1 = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80";
            final var profileImg2 = "https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80";

            createUserRecord("yilmaz.mustafa", profileImg2, "Yilmaz", "Mustafa", TEACHER_ROLE);
            createUserRecord("patrick.geudens", profileImg1, "Patrick", "Geudens", TEACHER_ROLE);
            createUserRecord("atilla.balin", profileImg2, "Atilla", "Balin", TEACHER_ROLE);
            createUserRecord("manuel.cogneau", profileImg1, "Manuel", "Cogneau", TEACHER_ROLE);
            createUserRecord("benedikt.lantsoght", profileImg2, "Benedikt", "Lantsoght", TEACHER_ROLE);
            createUserRecord("kenan.kurda", profileImg2, "Kenan", "Kurda", TEACHER_ROLE);
            createUserRecord("alexander.keisse", profileImg1, "Alexander", "Keisse", TEACHER_ROLE);
            createUserRecord("quinten.declerck", profileImg2, "Quinten", "De Clerck", TEACHER_ROLE);

            createUserRecord("pearl.de.smet", profileImg2, "Pearl", "De Smet", TEACHER_ROLE + "," + MANAGER_ROLE);
            createUserRecord("cindy.pipeleers", profileImg1, "Cindy", "Pipeleers", MANAGER_ROLE);
            createUserRecord("wouter.vandenberge", profileImg2, "Wouter", "Van den Berge", MANAGER_ROLE + "," + ADMIN_ROLE);

            createUserRecord("hilal.demir", profileImg1, "Hilal", "Demir", STUDENT_ROLE);
            createUserRecord("ahmed.faqiri", profileImg2, "Ahmed", "Faqiri", STUDENT_ROLE);
            createUserRecord("alexandru.tudorache", profileImg1, "Alexandru", "Tudorache", STUDENT_ROLE);
            createUserRecord("sameerun.acchukatla", profileImg2, "Sameerun", "Acchukatla", STUDENT_ROLE);
            createUserRecord("rahime.yildiz", profileImg1, "Rahime", "Yildiz", STUDENT_ROLE);
            createUserRecord("sinan.hubeyb.cam", profileImg2, "Sinan", "Hubeyb Cam", STUDENT_ROLE);
            createUserRecord("joey.de.kort", profileImg1, "Joey", "De Kort", STUDENT_ROLE);
            createUserRecord("irina.afanassenko", profileImg2, "Irina", "Afanassenko", STUDENT_ROLE);
            createUserRecord("ali.jamal.alwasseti", profileImg2, "Ali Jamal", "Alwasseti", STUDENT_ROLE);


        };
    }


    private void createUserRecord(String username, String profile, String firstName, String lastName, String roles) {

        User user = new User()
                .setUsername(username).setHashedPassword(passwordEncoder.encode("P@ssw0rd"))
                .setProfile(profile)
                .setFirstName(firstName).setLastName(lastName)
                .setEmail(username.concat("@").concat("intecbrussel.be"))
                .setPhone(String.format("0467334%d", username.length() * new Random().nextInt(1000)))
                .setRoles(roles);

        final var savedUser = userRepository.save(user);
        logger.info("New record is added: " + savedUser);
    }

}
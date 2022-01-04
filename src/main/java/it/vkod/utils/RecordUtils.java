package it.vkod.utils;


import com.vaadin.flow.spring.annotation.SpringComponent;
import it.vkod.models.entities.Course;
import it.vkod.models.entities.User;
import it.vkod.models.entities.Role;
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
                mock("patrick.geudens", "Patrick", "Geudens", Course.BO_JAVA_21_SEPT, TEACHER);
                mock("atilla.balin", "Atilla", "Balin", Course.BO_FRONT_END_DEV_21_NOV, TEACHER);
                mock("manuel.cogneau", "Manuel", "Cogneau", Course.BO_JAVA_21_JAN, TEACHER);
                mock("benedikt.lantsoght", "Benedikt", "Lantsoght", Course.BO_FRONT_END_DEV_20_SEPT, TEACHER);
                mock("kenan.kurda", "Kenan", "Kurda", Course.BO_DOT_NET_21_JUNI, TEACHER);
                mock("alexander.keisse", "Alexander", "Keisse", Course.BO_DOT_NET_21_SEPT, TEACHER);
                mock("quinten.declerck", "Quinten", "De Clerck", Course.BO_DOT_NET_21_JUNI, TEACHER);

                mock("hilal.demir", "Hilal", "Demir", Course.BO_JAVA_21_JUNI, STUDENT);
                mock("ahmed.faqiri", "Ahmed", "Faqiri", Course.BO_JAVA_21_JUNI, STUDENT);
                mock("alexandru.tudorache", "Alexandru", "Tudorache", Course.BO_JAVA_21_JUNI, STUDENT);
                mock("sameerun.acchukatla", "Sameerun", "Acchukatla", Course.BO_JAVA_21_JUNI, STUDENT);
                mock("rahime.yildiz", "Rahime", "Yildiz", Course.BO_JAVA_21_JUNI, STUDENT);
                mock("sinan.hubeyb.cam", "Sinan", "Hubeyb Cam", Course.BO_JAVA_21_JUNI, STUDENT);
                mock("joey.de.kort", "Joey", "De Kort", Course.BO_JAVA_21_JUNI, STUDENT);
                mock("irina.afanassenko", "Irina", "Afanassenko", Course.BO_JAVA_21_JUNI, STUDENT);
                mock("ali.jamal.alwasseti", "Ali Jamal", "Alwasseti", Course.BO_JAVA_21_JUNI, STUDENT);

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
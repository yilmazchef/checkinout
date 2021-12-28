package it.vkod.services.flow;


import it.vkod.models.entities.Check;
import it.vkod.models.entities.CheckType;
import it.vkod.repositories.CheckRepository;
import it.vkod.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.lang.Boolean.TRUE;

@RequiredArgsConstructor
@Service
public class CheckService {

    private final UserRepository userRepository;
    private final CheckRepository checkRepository;


    public List<Check> today() {

        return checkRepository.findAll(ZonedDateTime.now());
    }


    public List<Check> on(final ZonedDateTime checkedOn) {

        return checkRepository.findAll(checkedOn);
    }


    public List<Check> ofAttendee(final String username) {

        return checkRepository.findAllByAttendee(ZonedDateTime.now(), username);
    }


    public List<Check> fromOrganizer(final ZonedDateTime checkedOn, final String username) {

        return checkRepository.findAllByOrganizer(checkedOn, username);
    }


    @Transactional
    public Check createOrUpdate(Check check) {

        final var foundChecks = checkRepository.findAllByCourseAndType(
                check.getCourse(), ZonedDateTime.now(), Collections.singleton(check.getType())
        );


        final var oAttendee = userRepository.findByUsername(check.getAttendee().getUsername());
        final var oOrganizer = userRepository.findByUsername(check.getOrganizer().getUsername());

        // CREATE NEW CHECK
        if (check.isNew() && foundChecks.isEmpty() && oAttendee.isPresent() && oOrganizer.isPresent()) {

            check.setAttendee(oAttendee.get());
            check.setOrganizer(oOrganizer.get());

            return checkRepository.save(check);


        } else { // UPDATE EXISTING CHECK

            final var checkToUpdate = foundChecks.stream().filter(c -> c.getType() == check.getType()).findFirst();

            if (checkToUpdate.isPresent()) {
                return checkRepository.save(checkToUpdate.get());

            } else if (oAttendee.isPresent() && oOrganizer.isPresent()) {
                check.setAttendee(oAttendee.get());
                check.setOrganizer(oOrganizer.get());

                return checkRepository.save(check);
            }

        }

        return null;

    }


    public List<Check> fromCourse(final String course) {

        return checkRepository.findAllByCourse(course);
    }


    public List<Check> fromTodayAndCourse(final String course, CheckType... types) {

        return checkRepository.findAllByCourseAndType(course, ZonedDateTime.now(), Set.of(types));
    }

}

package it.vkod.services.flow;


import it.vkod.models.entities.Check;
import it.vkod.models.entities.CheckType;
import it.vkod.repositories.CheckRepository;
import it.vkod.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class CheckService {

    private final UserRepository userRepository;
    private final CheckRepository checkRepository;


    public List<Check> today() {

        return this.checkRepository.findAllToday();
    }


    public List<Check> on(final java.sql.Date on) {

        return this.checkRepository.findAll(on);
    }


    public List<Check> ofAttendee(final java.sql.Date on, final java.sql.Time at, final String username) {

        return this.checkRepository.findAllByAttendee(on, at, username);
    }


    public List<Check> fromOrganizer(final java.sql.Date on, final java.sql.Time at, final String username) {

        return this.checkRepository.findAllByOrganizer(on, at, username);
    }


    @Transactional
    public Check createOrUpdate(Check check) {

        return this.checkRepository.save(check);

    }


    public List<Check> fromCourse(final String course) {

        return this.checkRepository.findAllByCourse(course);
    }


    public List<Check> fromTodayAndCourse(final String course, CheckType... types) {

        final var nowDate = java.sql.Date.valueOf(LocalDate.now());
        final var nowTime = java.sql.Time.valueOf(LocalTime.now());
        return this.checkRepository.findAllByCourseAndType(course, nowDate, nowTime, Set.of(types));
    }

}

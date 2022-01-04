package it.vkod.services.flow;


import it.vkod.models.entities.Check;
import it.vkod.models.entities.CheckType;
import it.vkod.repositories.CheckRepository;
import it.vkod.repositories.UserRepository;
import it.vkod.services.exceptions.CheckinoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class CheckService {

    private final UserRepository userRepository;
    private final CheckRepository checkRepository;

    private Check generate(
            final String session, final CheckType type,
            final String organizer, final String attendee,
            final String course, final Double[] location) throws CheckinoutException {

        final var oOrganizer = this.userRepository.findByUsername(organizer);
        final var oAttendee = this.userRepository.findByUsername(attendee);

        if (oOrganizer.isEmpty()) {
            throw new CheckinoutException("Organizer not found!");
        }

        if (oAttendee.isEmpty()) {
            throw new CheckinoutException("Attendee not found!");
        }

        return new Check()
                .setOrganizer(oOrganizer.get())
                .setAttendee(oAttendee.get())
                .setCourse(course)
                .setLat(location[0])
                .setLon(location[1])
                .setValidation(new Random().nextInt(8999) + 1000)
                .setSession(session)
                .setType(type);
    }

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
    public Check checkinToday(@NotEmpty final String session, @NotEmpty final String course,
                              @NotEmpty final String organizer, @NotEmpty final String attendee,
                              @NotEmpty final Double latitude, @NotEmpty final Double longitude) {

        final var existing = this.checkRepository.findAllCheckinsByAttendee(attendee);

        return existing.isEmpty() ?
                this.checkRepository.save(
                        generate(
                                session, CheckType.PHYSICAL_IN, organizer, attendee, course, new Double[]{latitude, longitude}
                        )
                ).setDuplicated(false)
                : existing.get(0).setDuplicated(true);

    }


    @Transactional
    public Check createOrUpdate(Check check) {

        final var existing = this.checkRepository.findAllByAttendee(
                check.getOnDate(), Collections.singleton(CheckType.PHYSICAL_IN), check.getAttendee().getUsername());

        return existing.isEmpty() ?
                this.checkRepository.save(check).setDuplicated(false)
                : existing.get(0).setDuplicated(true);

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

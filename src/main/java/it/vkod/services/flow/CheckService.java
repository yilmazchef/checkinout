package it.vkod.services.flow;


import it.vkod.models.entities.Check;
import it.vkod.models.entities.Course;
import it.vkod.models.entities.Event;
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

import static it.vkod.models.entities.Event.*;
import static it.vkod.models.entities.Event.REMOTE_IN;

@RequiredArgsConstructor
@Service
public class CheckService {

    private final UserRepository userRepository;
    private final CheckRepository checkRepository;

    private Check generate(
            final String session, final Event type,
            final String organizer, final String attendee,
            final Course course, final Double[] location) throws CheckinoutException {

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
                .setEvent(type);
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
    public Check checkin(@NotEmpty final String session, @NotEmpty final Course course,
                         @NotEmpty final String organizer, @NotEmpty final String attendee,
                         @NotEmpty final Double latitude, @NotEmpty final Double longitude,
                         final boolean remote, final boolean guest) {

        final var checkins = this.checkRepository.findAllCheckinsByAttendee(attendee);
        var type = PHYSICAL_IN;
        if (remote) type = REMOTE_IN;
        if (guest) type = GUEST_IN;

        return checkins.isEmpty() ?
                this.checkRepository.save(
                        generate(
                                session, type, organizer, attendee, course, new Double[]{latitude, longitude}
                        )
                ).setDuplicated(false)
                : checkins.get(0).setDuplicated(true);

    }

    @Transactional
    public Check checkout(@NotEmpty final String session, @NotEmpty final Course course,
                          @NotEmpty final String organizer, @NotEmpty final String attendee,
                          @NotEmpty final Double latitude, @NotEmpty final Double longitude,
                          final boolean remote, final boolean guest) {

        final var checkins = this.checkRepository.findAllCheckinsByAttendee(attendee);
        final var checkouts = this.checkRepository.findAllCheckoutsByAttendee(attendee);
        var type = PHYSICAL_OUT;
        if (remote) type = REMOTE_OUT;
        if (guest) type = GUEST_OUT;

        return (!checkins.isEmpty() && checkouts.isEmpty()) ?
                this.checkRepository.save(
                        generate(
                                session, type, organizer, attendee, course, new Double[]{latitude, longitude}
                        )
                ).setDuplicated(false)
                : checkouts.get(0).setDuplicated(true);

    }


    @Transactional
    public Check createOrUpdate(Check check) {

        final var existing = this.checkRepository.findAllByAttendee(
                check.getOnDate(), Collections.singleton(PHYSICAL_IN), check.getAttendee().getUsername());

        return existing.isEmpty() ?
                this.checkRepository.save(check).setDuplicated(false)
                : existing.get(0).setDuplicated(true);

    }


    public List<Check> fromCourse(final Course course) {

        return this.checkRepository.findAllByCourse(course);
    }


    public List<Check> fetchAllByCourse(final Course course, Event... types) {

        final var nowDate = java.sql.Date.valueOf(LocalDate.now());
        final var nowTime = java.sql.Time.valueOf(LocalTime.now());
        return this.checkRepository.findAllByCourseAndType(course, nowDate, nowTime, Set.of(types));
    }

}

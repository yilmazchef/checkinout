package it.vkod.services.flow;

import it.vkod.models.entities.Check;
import it.vkod.models.entities.Course;
import it.vkod.models.entities.Event;
import it.vkod.repositories.CheckRepository;
import it.vkod.repositories.UserRepository;
import it.vkod.services.exceptions.CheckinoutException;
import lombok.experimental.NonFinal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.*;

import static it.vkod.models.entities.Event.*;

@Service
public class CheckService {

    private final UserRepository userRepository;
    private final CheckRepository checkRepository;

    public CheckService(final UserRepository userRepository, final CheckRepository checkRepository) {
        this.userRepository = userRepository;
        this.checkRepository = checkRepository;
    }

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

        return checkRepository.findAllToday();
    }

    public List<Check> on(final java.sql.Date on) {

        return checkRepository.findAll(on);
    }

    public List<Check> ofAttendee(final java.sql.Date on, final java.sql.Time at, final String username) {

        return checkRepository.findAllByAttendee(on, at, username);
    }

    public List<Check> fromOrganizer(final java.sql.Date on, final java.sql.Time at, final String username) {

        return checkRepository.findAllByOrganizer(on, at, username);
    }

    @Transactional
    public Optional<Check> checkin(@NotEmpty final String session, @NotEmpty final Course course,
                                   @NotEmpty final String organizer, @NotEmpty final String attendee,
                                   @NotEmpty final Double latitude, @NotEmpty final Double longitude,
                                   final boolean remote, final boolean guest) {

        final var checkins = checkRepository.findAllCheckinsByAttendee(attendee);
        var type = PHYSICAL_IN;
        if (remote)
            type = REMOTE_IN;
        if (guest)
            type = GUEST_IN;

        if (checkins.isEmpty()) {
            return Optional.ofNullable(checkRepository.save(
                            generate(
                                    session, type, organizer, attendee, course, new Double[]{latitude, longitude}
                            )
                    )
                    .setDuplicated(false));
        }

        return Optional.ofNullable(checkins.get(0).setDuplicated(true));

    }

    @Transactional
    public Optional<Check> checkout(@NotEmpty final String session, @NotEmpty final Course course,
                                    @NotEmpty final String organizer, @NotEmpty final String attendee,
                                    @NonFinal Double latitude, @NonFinal Double longitude,
                                    final boolean remote, final boolean guest) {

        final var checkins = checkRepository.findAllCheckinsByAttendee(attendee);
        final var checkouts = checkRepository.findAllCheckoutsByAttendee(attendee);
        var type = PHYSICAL_OUT;
        if (remote)
            type = REMOTE_OUT;
        if (guest)
            type = GUEST_OUT;

        if (latitude == null || longitude == null) {
            latitude = 0.0;
            longitude = 0.0;
            // throw new CheckinoutException("Latitude and longitude are required!");
        }

        if (!checkins.isEmpty() && checkouts.isEmpty()) {
            return Optional.ofNullable(checkRepository.save(
                            generate(
                                    session, type, organizer, attendee, course, new Double[]{latitude, longitude}
                            )
                    )
                    .setDuplicated(false));
        }

        if (!checkouts.isEmpty()) {
            return Optional.of(checkouts.get(0).setDuplicated(true));
        }

        return Optional.empty();
    }

    @Transactional
    public Optional<Check> createOrUpdate(Check check) {

        final var existing = checkRepository.findAllByAttendee(
                check.getOnDate(), Collections.singleton(PHYSICAL_IN), check.getAttendee().getUsername());

        if (existing.isEmpty()) {
            return Optional.ofNullable(checkRepository.save(check).setDuplicated(false));
        }

        // TODO: count how many checks this user have, and update here only the latest check
        return Optional.ofNullable(existing.get(0).setDuplicated(true));

    }

    public List<Check> fromCourse(final Course course) {

        return checkRepository.findAllByCourse(course);
    }

    public List<Check> fetchAllByCourse(final Course course, Event... types) {

        final var nowDate = java.sql.Date.valueOf(LocalDate.now());
        return checkRepository.findAllByCourseAndType(course, nowDate, Set.of(types));
    }

}

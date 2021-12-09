package it.vkod.services;

import com.grum.geocalc.BoundingArea;
import com.grum.geocalc.Coordinate;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;
import it.vkod.data.dto.CheckDTO;
import it.vkod.data.entity.Check;
import it.vkod.data.entity.Event;
import it.vkod.repositories.CheckRepository;
import it.vkod.repositories.CourseRepository;
import it.vkod.repositories.EventRepository;
import it.vkod.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CheckService {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final CheckRepository checkRepository;
    private final EventRepository eventRepository;
    private final CourseRepository courseRepository;

    public List<Check> fetchByDate(final Date checkedOn) {
        return checkRepository.findByCheckedOn(checkedOn);
    }

    public List<Check> fetchByToday() {
        return checkRepository.findByCheckedOnToday();
    }

    public Optional<Check> fetchByDate(final Date checkedOn, final String qrcode) {
        return checkRepository.findByCheckedOnAndQrcode(checkedOn, qrcode);
    }

    public Optional<Check> fetchByToday(final String qrcode) {
        return checkRepository.findByCheckedOnTodayAndQrcode(qrcode);
    }

    public List<CheckDTO> fetchAllDetailsToday() {
        return checkRepository.findAllChecksOfToday();
    }

    public List<CheckDTO> fetchOutDetailsToday() {
        return checkRepository.findAllCheckoutsOfToday();
    }

    public List<CheckDTO> fetchOutDetailsToday(final Long courseId) {
        return checkRepository.findAllCheckoutsOfToday(courseId);
    }

    public List<CheckDTO> fetchInDetailsToday() {
        return checkRepository.findAllCheckinsOfToday();
    }

    public List<CheckDTO> fetchInDetailsToday(final Long courseId) {
        return checkRepository.findAllCheckinsOfToday(courseId);
    }

    public List<Event> fetchEventsByOrganizerId(final Long organizerId) {
        return eventRepository.findByOrganizerId(organizerId);
    }

    public List<Event> fetchEventsByAttendeeId(final Long attendeeId) {
        return eventRepository.findByAttendeeId(attendeeId);
    }

    public List<Event> fetchByAttendeeIdAndCheckId(final Long attendeeId, final Long checkId) {
        return eventRepository.findByAttendeeIdAndCheckId(attendeeId, checkId);
    }

    public Optional<Event> fetchByAttendeeIdAndCheckId(final Long attendeeId, final Long checkId, final String checkType) {
        return eventRepository.findByAttendeeIdAndCheckIdAndCheckType(attendeeId, checkId, checkType);
    }

    @Transactional
    public void removeById(Long checkId) {
        checkRepository.deleteById(checkId);
    }

    @Transactional
    public Check createCheck(Check checkEntity) {

        final var oUser = userRepository.findByUsername(checkEntity.getQrcode());

        if (oUser.isPresent()) {

            Coordinate lat = Coordinate.fromDegrees(checkEntity.getLat());
            Coordinate lng = Coordinate.fromDegrees(checkEntity.getLon());
            Point userLocation = Point.at(lat, lng);

            Coordinate intecLat = Coordinate.fromDegrees(50.8426647248452);
            Coordinate intecLon = Coordinate.fromDegrees(4.346442528782073);
            Point intecLocation = Point.at(intecLat, intecLon);
            BoundingArea intecBoundingArea = EarthCalc.gcd.around(intecLocation, 10);

            checkEntity.setValidLocation(intecBoundingArea.contains(userLocation));

        }

        return checkRepository.save(checkEntity);
    }

    @Transactional
    public Event createEvent(Event eventEntity) {
        return eventRepository.save(eventEntity);
    }
}

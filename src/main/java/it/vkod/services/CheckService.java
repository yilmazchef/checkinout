package it.vkod.services;

import com.grum.geocalc.BoundingArea;
import com.grum.geocalc.Coordinate;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;
import it.vkod.data.dto.CheckDTO;
import it.vkod.data.entity.Check;
import it.vkod.data.entity.Course;
import it.vkod.data.entity.Event;
import it.vkod.repositories.CheckRepository;
import it.vkod.repositories.CourseRepository;
import it.vkod.repositories.EventRepository;
import it.vkod.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
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

    public List<Check> findChecksByCheckedOn(final Date checkedOn) {
        return checkRepository.findByCheckedOn(checkedOn);
    }

    public List<Check> findChecksByCheckedOnToday() {
        return checkRepository.findByCheckedOnToday();
    }

    public Optional<Check> findChecksByCheckedOnAndQrcode(final Date checkedOn, final String qrcode) {
        return checkRepository.findByCheckedOnAndQrcode(checkedOn, qrcode);
    }

    public Optional<Check> findChecksByCheckedOnTodayAndQrcode(final String qrcode) {
        return checkRepository.findByCheckedOnTodayAndQrcode(qrcode);
    }

    public List<CheckDTO> findCheckDetailsOfToday() {
        return checkRepository.findAllChecksOfToday();
    }

    public List<CheckDTO> findCheckoutDetailsOfToday() {
        return checkRepository.findAllCheckoutsOfToday();
    }

    public List<CheckDTO> findAllCheckinDetailsOfToday() {
        return checkRepository.findAllCheckinsOfToday();
    }

    public List<Event> findEventsByOrganizerId(final Long organizerId) {
        return eventRepository.findByOrganizerId(organizerId);
    }

    public List<Event> findEventsByAttendeeId(final Long attendeeId) {
        return eventRepository.findByAttendeeId(attendeeId);
    }

    public List<Event> findByAttendeeIdAndCheckId(final Long attendeeId, final Long checkId) {
        return eventRepository.findByAttendeeIdAndCheckId(attendeeId, checkId);
    }

    public Optional<Event> findByAttendeeIdAndCheckIdAndCheckType(final Long attendeeId, final Long checkId,
                                                                  final String checkType) {
        return eventRepository.findByAttendeeIdAndCheckIdAndCheckType(attendeeId, checkId, checkType);
    }

    @Transactional
    public Check createCheck(Check checkEntity) {

        final var oUser = userRepository.findByUsername(checkEntity.getQrcode());

        if (oUser.isPresent()) {

            Coordinate lat = Coordinate.fromDegrees(51.4843774);
            Coordinate lng = Coordinate.fromDegrees(-0.2912044);
            Point userLocation = Point.at(lat, lng);


            Coordinate intecLat = Coordinate.fromDegrees(50.8426647248452);
            Coordinate intecLon = Coordinate.fromDegrees(4.346442528782073);
            Point intecLocation = Point.at(intecLat, intecLon);
            BoundingArea intecBoundingArea = EarthCalc.gcd.around(intecLocation, 10);

            boolean sameLocation = intecBoundingArea.contains(userLocation);

            if (sameLocation) {
                return checkRepository.save(checkEntity);

            }

            return null;

        }

        return null;
    }

    @Transactional
    public Event createEvent(Event eventEntity) {
        return eventRepository.save(eventEntity);
    }

    @Transactional
    public Course createOrUpdateCourse(Course courseEntity) {
        return courseRepository.save(courseEntity);
    }

    public Optional<Course> fetchCourse(Long id) {
        return courseRepository.findById(id);
    }

    public List<Course> fetchCourse() {
        final var cIterator = courseRepository.findAll().iterator();
        final var cList = new ArrayList<Course>();

        while (cIterator.hasNext()) {
            cList.add(cIterator.next());
        }

        return cList;
    }

    public List<Course> fetchCourse(String keyword) {
        return courseRepository.findAllByTitleMatchesOrDescriptionContaining(keyword, keyword);
    }
}

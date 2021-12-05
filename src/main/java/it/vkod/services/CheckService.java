package it.vkod.services;

import it.vkod.data.dto.CheckDTO;
import it.vkod.data.entity.Check;
import it.vkod.data.entity.Event;
import it.vkod.repositories.CheckRepository;
import it.vkod.repositories.EventRepository;
import it.vkod.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public Check createCheck(Check checkEntity) {
        return checkRepository.save(checkEntity);
    }

    public Event createEvent(Event eventEntity) {
        return eventRepository.save(eventEntity);
    }
}

package it.vkod.repositories;


import it.vkod.models.entity.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {

    List<Event> findByOrganizerId(final Long organizerId);

    List<Event> findByAttendeeId(final Long attendeeId);

    List<Event> findByAttendeeIdAndCheckId(final Long attendeeId, final Long checkId);

    Optional<Event> findByAttendeeIdAndCheckIdAndCheckType(final Long attendeeId, final Long checkId,
                                                           final String checkType);

}

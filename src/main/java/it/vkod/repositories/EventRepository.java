package it.vkod.repositories;


import it.vkod.data.entity.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends CrudRepository< Event, Long > {

	List< Event > findByOrganizerId( final Long organizerId );

	List< Event > findByAttendeeId( final Long attendeeId );

}

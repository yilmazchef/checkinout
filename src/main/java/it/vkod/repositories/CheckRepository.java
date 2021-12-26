package it.vkod.repositories;


import it.vkod.models.entities.Check;
import it.vkod.models.entities.CheckType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.validation.constraints.NotEmpty;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CheckRepository extends JpaRepository< Check, Long >, JpaSpecificationExecutor< Check > {

	List< Check > findAllByActiveAndCreated( final Boolean active, final ZonedDateTime created );

	Optional< Check > findByActiveAndCreatedAndAttendee_Username( final Boolean active, final ZonedDateTime created, final @NotEmpty String attendee_username );

	List< Check > findAllByActiveAndCreatedAndTypeIsIn( final Boolean active, final ZonedDateTime created, final Collection< CheckType > type );

	List< Check > findByActiveAndCreatedAndTypeIsInAndAttendee_Username( final Boolean active, final ZonedDateTime created, final Collection< CheckType > type, final @NotEmpty String attendee_username );

	List< Check > findAllByActiveAndCreatedAndOrganizer_Username( final Boolean active, final ZonedDateTime created, final @NotEmpty String organizer_username );

	List< Check > findAllByActiveAndCourse( final Boolean active, final String course );

}

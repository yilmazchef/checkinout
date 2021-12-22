package it.vkod.repositories;


import it.vkod.models.entities.Check;
import it.vkod.models.entities.CheckType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.validation.constraints.NotEmpty;
import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CheckRepository extends JpaRepository< Check, Long >, JpaSpecificationExecutor< Check > {

	List< Check > findAllByActiveAndCheckedOn( final Boolean active, final Date checkedOn );

	Optional< Check > findByActiveAndCheckedOnAndAttendee_Username( final Boolean active, final Date checkedOn, final @NotEmpty String username );

	List< Check > findAllByActiveAndCheckedOnAndTypeIsIn( final Boolean active, final Date checkedOn, final Collection< CheckType > type );

	List< Check > findByActiveAndCheckedOnAndTypeIsInAndAttendee_Username( final Boolean active, final Date checkedOn, final Set< CheckType > type, final @NotEmpty String attendee_username );

	List< Check > findAllByActiveAndCheckedOnAndOrganizer_Username( final Boolean active, final Date checkedOn, final @NotEmpty String username );

	List< Check > findAllByActiveAndCourse( final Boolean active, final String course );

}

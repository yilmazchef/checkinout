package it.vkod.services.flow;


import com.grum.geocalc.Coordinate;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;
import it.vkod.models.entities.Check;
import it.vkod.models.entities.CheckType;
import it.vkod.repositories.CheckRepository;
import it.vkod.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.lang.Boolean.TRUE;

@RequiredArgsConstructor
@Service
public class CheckService {

	private final AuthenticationService authenticationService;
	private final UserRepository userRepository;
	private final CheckRepository checkRepository;


	public List< Check > today() {

		return checkRepository.findAllByActiveAndCreated( TRUE, ZonedDateTime.now() );
	}


	public List< Check > on( final ZonedDateTime checkedOn ) {

		return checkRepository.findAllByActiveAndCreated( TRUE, checkedOn );
	}


	public Optional< Check > ofAttendee( final String username ) {

		return checkRepository.findByActiveAndCreatedAndAttendee_Username( TRUE, ZonedDateTime.now(), username );
	}


	public List< Check > fromOrganizer( final ZonedDateTime checkedOn, final String username ) {

		return checkRepository.findAllByActiveAndCreatedAndOrganizer_Username( TRUE, checkedOn, username );
	}


	@Transactional
	public Check createOrUpdate( Check check ) {

		final var foundChecks = checkRepository.findAllByActiveAndCourseAndCreatedOrUpdatedAndTypeIsIn(
				TRUE, check.getCourse(), ZonedDateTime.now(), ZonedDateTime.now(), Collections.singleton( check.getType() )
		);


		final var oAttendee = userRepository.findByUsername( check.getAttendee().getUsername() );
		final var oOrganizer = authenticationService.get();

		final var lat = Coordinate.fromDegrees( check.getLatitude() );
		final var lng = Coordinate.fromDegrees( check.getLongitude() );
		final var userLocation = Point.at( lat, lng );

		final var intecLat = Coordinate.fromDegrees( 50.8426647248452 );
		final var intecLon = Coordinate.fromDegrees( 4.346442528782073 );
		final var intecLoc = Point.at( intecLat, intecLon );
		final var area = EarthCalc.gcd.around( intecLoc, 10 );

		if ( foundChecks.isEmpty() && oAttendee.isPresent() && oOrganizer.isPresent() ) {

			check.setAttendee( oAttendee.get() );
			check.setOrganizer( oOrganizer.get() );

			return checkRepository.save( check );


		} else {

			final var checkToUpdate = foundChecks.stream().filter( c -> c.getType() == check.getType() ).findFirst();

			if ( checkToUpdate.isPresent() ) {
				return checkRepository.save( checkToUpdate.get() );

			} else if ( oAttendee.isPresent() && oOrganizer.isPresent() ) {
				check.setAttendee( oAttendee.get() );
				check.setOrganizer( oOrganizer.get() );

				return checkRepository.save( check );
			}

		}
		
		// FIXME: later
		return null;

	}


	public List< Check > fromCourse( final String course ) {

		return checkRepository.findAllByActiveAndCourse( TRUE, course );
	}


	public List< Check > fromTodayAndCourse( final String course, CheckType... types ) {

		return checkRepository.findAllByActiveAndCourseAndCreatedOrUpdatedAndTypeIsIn( TRUE, course, ZonedDateTime.now(), ZonedDateTime.now(), Set.of( types ) );
	}

}

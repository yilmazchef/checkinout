package it.vkod.services.flow;


import com.grum.geocalc.Coordinate;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;
import it.vkod.models.entities.Check;
import it.vkod.repositories.CheckRepository;
import it.vkod.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

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
	public Check create( Check checkEntity ) {

		final var oAttendee = userRepository.findByUsername( checkEntity.getAttendee().getUsername() );
		final var oOrganizer = authenticationService.get();

		final var lat = Coordinate.fromDegrees( checkEntity.getLatitude() );
		final var lng = Coordinate.fromDegrees( checkEntity.getLongitude() );
		final var userLocation = Point.at( lat, lng );

		final var intecLat = Coordinate.fromDegrees( 50.8426647248452 );
		final var intecLon = Coordinate.fromDegrees( 4.346442528782073 );
		final var intecLoc = Point.at( intecLat, intecLon );
		final var area = EarthCalc.gcd.around( intecLoc, 10 );

		if ( oAttendee.isPresent() && oOrganizer.isPresent() ) {
			checkEntity.setAttendee( oAttendee.get() );
			checkEntity.setOrganizer( oOrganizer.get() );
		}

		return checkRepository.save( checkEntity );
	}


	public List< Check > fromCourse( final String course ) {

		return checkRepository.findAllByActiveAndCourse( TRUE, course );
	}

}

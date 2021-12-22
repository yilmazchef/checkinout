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

import java.sql.Date;
import java.time.LocalDate;
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

		return checkRepository.findAllByActiveAndCheckedOn( TRUE, Date.valueOf( LocalDate.now() ) );
	}


	public List< Check > on( final Date checkedOn ) {

		return checkRepository.findAllByActiveAndCheckedOn( TRUE, checkedOn );
	}


	public Optional< Check > ofAttendee( final String username ) {

		return checkRepository.findByActiveAndCheckedOnAndAttendee_Username( TRUE, Date.valueOf( LocalDate.now() ), username );
	}


	public List< Check > fromOrganizer( final Date checkedOn, final String username ) {

		return checkRepository.findAllByActiveAndCheckedOnAndOrganizer_Username( TRUE, checkedOn, username );
	}


	@Transactional
	public Check create( Check checkEntity ) {

		final var oAttendee = userRepository.findByUsername( checkEntity.getAttendee().getUsername() );
		final var oOrganizer = authenticationService.get();

		final var lat = Coordinate.fromDegrees( checkEntity.getLat() );
		final var lng = Coordinate.fromDegrees( checkEntity.getLon() );
		final var userLocation = Point.at( lat, lng );

		final var intecLat = Coordinate.fromDegrees( 50.8426647248452 );
		final var intecLon = Coordinate.fromDegrees( 4.346442528782073 );
		final var intecLoc = Point.at( intecLat, intecLon );
		final var area = EarthCalc.gcd.around( intecLoc, 10 );

		final var validLocation = area.contains( userLocation );

		if ( oAttendee.isPresent() && oOrganizer.isPresent() && validLocation ) {
			checkEntity.setValidLocation( true );
			checkEntity.setAttendee( oAttendee.get() );
			checkEntity.setOrganizer( oOrganizer.get() );
		}

		return checkRepository.save( checkEntity );
	}


	public List< Check > fromCourse( final String course ) {

		return checkRepository.findAllByActiveAndCourse( TRUE, course );
	}

}

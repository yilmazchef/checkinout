package it.vkod.services;


import it.vkod.repositories.CheckRepository;
import it.vkod.repositories.EventRepository;
import it.vkod.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

	private final CheckRepository checkRepository;
	private final EventRepository eventRepository;
	private final UserRepository userRepository;


	public AdminService( @Autowired CheckRepository checkRepository,
	                     @Autowired EventRepository eventRepository,
	                     @Autowired UserRepository userRepository ) {

		this.checkRepository = checkRepository;
		this.eventRepository = eventRepository;
		this.userRepository = userRepository;
	}


	@Transactional
	public Long flushDatabase() {

		final var totalEvents = eventRepository.count();
		final var totalChecks = checkRepository.count();
		final var totalUsers = userRepository.count();

		this.eventRepository.deleteAll();
		this.checkRepository.deleteAll();
		this.userRepository.deleteAll();

		return totalEvents + totalChecks + totalUsers;
	}

}

package it.vkod.services;


import it.vkod.repositories.CheckRepository;
import it.vkod.repositories.EventRepository;
import it.vkod.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final CheckRepository checkRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;


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

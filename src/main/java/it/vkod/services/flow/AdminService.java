package it.vkod.services.flow;

import it.vkod.repositories.CheckRepository;
import it.vkod.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final CheckRepository checkRepository;
    private final UserRepository userRepository;


    @Transactional
    public Long flushDatabase() {

        final var totalChecks = checkRepository.count();
        final var totalUsers = userRepository.count();

        this.checkRepository.deleteAll();
        this.userRepository.deleteAll();

        return 1L;
    }

}

package it.vkod.services.flow;

import org.springframework.stereotype.Service;

import it.vkod.repositories.CheckRepository;
import it.vkod.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdminService {

	private final CheckRepository checkRepository;
	private final UserRepository userRepository;

}

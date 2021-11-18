package it.vkod.data.service;


import it.vkod.data.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
@RequiredArgsConstructor( access = AccessLevel.PUBLIC )
@Getter
public class UserService extends CrudService< User, Long > {

	private final UserRepository repository;

	public User findByUsername( final String username ) {

		return repository.findByUsername( username );
	}

}

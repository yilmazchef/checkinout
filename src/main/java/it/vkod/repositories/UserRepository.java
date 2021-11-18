package it.vkod.repositories;


import it.vkod.data.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository< User, Long > {


	Optional< User > findByUsername( final String username );

	User getByUsername( final String username );

}

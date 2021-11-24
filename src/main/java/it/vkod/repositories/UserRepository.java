package it.vkod.repositories;


import it.vkod.data.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository< User, Long > {

	Optional< User > findByUsernameAndHashedPassword( final String username, final String hashedPassword );

	Optional< User > findByEmailAndHashedPassword( final String email, final String hashedPassword );

	Optional< User > findByPhoneAndHashedPassword( final String phone, final String hashedPassword );

	Optional< User > findByUsername( final String username );

	User getByUsername( final String username );

	Boolean existsByUsername( final String username );

}

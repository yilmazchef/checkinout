package it.vkod.repositories;


import it.vkod.data.entity.User;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

	@Query("SELECT u.username FROM users u")
	Set<String> findAllUsernames();

	Optional<User> findByUsernameAndHashedPassword(final String username, final String hashedPassword);

	Optional<User> findByEmailAndHashedPassword(final String email, final String hashedPassword);

	Optional<User> findByPhoneAndHashedPassword(final String phone, final String hashedPassword);

	Optional<User> findByUsername(final String username);

	Optional<User> findByUsernameOrEmailOrPhone(final String username, final String email, final String phone);

	User getByUsername(final String username);

	Boolean existsByUsername(final String username);

}

package it.vkod.repositories;


import it.vkod.models.entities.User;
import it.vkod.models.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository< User, Long >, JpaSpecificationExecutor< User > {

	@Query("select u from User u where u.roles in :roles")
	List<User> findAllByRoles(@Param("roles") final Set<UserRole> roles);

	List< User > findAllByUsernameLikeOrPhoneContaining( final @NotEmpty String username, final String phone );

	List< User > findAllByCourse( final @NotEmpty String course );

	Optional< User > findByUsernameAndPassword( final String username, final String password );

	Optional< User > findByEmailAndPassword( final String email, final String password );

	Optional< User > findByPhoneAndPassword( final String phone, final String password );

	Optional< User > findByUsername( final String username );

	Optional< User > findByUsernameOrEmailOrPhone( final String username, final String email, final String phone );

	User getByUsername( final String username );

	Boolean existsByUsername( final String username );


}

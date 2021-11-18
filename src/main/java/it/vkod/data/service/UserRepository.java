package it.vkod.data.service;


import it.vkod.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User getByUsername( final String username );

    Optional<User> findByUsername( final String username);

    Optional<User> findByPhone( final String phone );
}
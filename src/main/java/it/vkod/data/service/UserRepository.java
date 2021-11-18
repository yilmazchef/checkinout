package it.vkod.data.service;

import it.vkod.data.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername( final String username);

    User findByPhone( final String phone );
}
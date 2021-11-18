package it.vkod.data.service;


import it.vkod.data.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository< Event, Long > {


}
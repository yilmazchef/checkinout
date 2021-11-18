package it.vkod.data.service;


import it.vkod.data.entity.Check;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckRepository extends JpaRepository< Check, Long > {


}
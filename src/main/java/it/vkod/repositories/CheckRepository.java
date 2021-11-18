package it.vkod.repositories;


import it.vkod.data.entity.Check;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckRepository extends CrudRepository< Check, Long > {

}

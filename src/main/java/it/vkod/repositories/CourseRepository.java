package it.vkod.repositories;

import it.vkod.data.entity.Course;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends CrudRepository<Course, Long> {

    List<Course> findAllByTitleMatchesOrDescriptionContaining(final String title, final String description);

    Optional<Course> findByTitle(final String title);
    
}

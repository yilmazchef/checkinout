package it.vkod.services;

import it.vkod.data.entity.Course;
import it.vkod.repositories.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CourseService {

    private final CourseRepository courseRepository;

    @Transactional
    public Course createOrUpdateCourse(Course courseEntity) {
        return courseRepository.save(courseEntity);
    }

    public Optional<Course> fetchCourse(Long id) {
        return courseRepository.findById(id);
    }

    public List<Course> fetchCourse() {
        final var cIterator = courseRepository.findAll().iterator();
        final var cList = new ArrayList<Course>();

        while (cIterator.hasNext()) {
            cList.add(cIterator.next());
        }

        return cList;
    }

    public List<Course> fetchCourse(String keyword) {
        return courseRepository.findAllByTitleMatchesOrDescriptionContaining(keyword, keyword);
    }

}

package it.vkod.repositories;


import it.vkod.models.entity.User;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    @Query("SELECT u.username FROM users u")
    Set<String> findAllUsernames();

    List<User> findAllByCurrentTraining(final String currentTraining);

    Optional<User> findByUsernameAndHashedPassword(final String username, final String hashedPassword);

    Optional<User> findByEmailAndHashedPassword(final String email, final String hashedPassword);

    Optional<User> findByPhoneAndHashedPassword(final String phone, final String hashedPassword);

    Optional<User> findByUsername(final String username);

    Optional<User> findByUsernameOrEmailOrPhone(final String username, final String email, final String phone);

    User getByUsername(final String username);

    Boolean existsByUsername(final String username);

    @Query("SELECT u.* FROM checks c" +
            " INNER JOIN users u ON c.qrcode = u.username" +
            " WHERE c.checked_on = CURRENT_DATE")
    Set<User> findAllCheckedToday(@Param("courseId") final Long courseId);

    @Query("SELECT u.* FROM checks c" +
            " INNER JOIN users u ON c.qrcode = u.username" +
            " INNER JOIN events e ON e.check_id = c.id" +
            " WHERE c.checked_on = CURRENT_DATE AND e.check_type = 'IN' AND e.course_id = :courseId")
    Set<User> findAllCheckedInUsersOfToday(@Param("courseId") final Long courseId);

    @Query("SELECT u.* FROM checks c" +
            " INNER JOIN users u ON c.qrcode = u.username" +
            " INNER JOIN events e ON e.check_id = c.id" +
            " WHERE c.checked_on = CURRENT_DATE AND e.check_type = 'OUT' AND e.course_id = :courseId")
    Set<User> findAllCheckedOutUsersOfToday(@Param("courseId") final Long courseId);

}

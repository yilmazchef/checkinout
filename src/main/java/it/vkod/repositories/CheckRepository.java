package it.vkod.repositories;

import it.vkod.models.dto.CheckDetails;
import it.vkod.models.entity.Check;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface CheckRepository extends CrudRepository<Check, Long> {

    List<Check> findByCheckedOn(final Date checkedOn);

    @Query("SELECT * FROM checks WHERE checked_on = CURRENT_DATE")
    List<Check> findByCheckedOnToday();

    Optional<Check> findByCheckedOnAndQrcode(final Date checkedOn, final String qrcode);

    @Query("SELECT * FROM checks WHERE checked_on = CURRENT_DATE AND qrcode = :qrcode")
    Optional<Check> findByCheckedOnTodayAndQrcode(@Param("qrcode") final String qrcode);

    @Query(
            "SELECT c.id, u.first_name, u.last_name, u.email, c.checked_on, c.checked_in_at, c.checked_out_at FROM checks c INNER JOIN users u ON c.qrcode = u.username WHERE c.checked_on = CURRENT_DATE")
    List<CheckDetails> findAllChecksOfToday();

    @Query("SELECT c.id, u.first_name, u.last_name, u.email, c.checked_on, c.checked_in_at, c.checked_out_at FROM checks c INNER JOIN users u ON c.qrcode = u.username INNER JOIN events e ON e.check_id = c.id WHERE c.checked_on = CURRENT_DATE AND e.check_type = 'OUT'")
    List<CheckDetails> findAllCheckoutsOfToday();

    @Query("SELECT c.id, u.first_name, u.last_name, u.email, c.checked_on, c.checked_in_at FROM checks c INNER JOIN users u ON c.qrcode = u.username INNER JOIN events e ON e.check_id = c.id WHERE c.checked_on = CURRENT_DATE AND e.check_type = 'IN'")
    List<CheckDetails> findAllCheckinsOfToday();

    @Query("SELECT c.id, u.first_name, u.last_name, u.email, c.checked_on, c.checked_in_at FROM checks c" +
            " INNER JOIN users u ON c.qrcode = u.username" +
            " INNER JOIN events e ON e.check_id = c.id" +
            " WHERE c.checked_on = CURRENT_DATE AND e.check_type = 'IN' AND u.current_training = :current_training")
    List<CheckDetails> findAllCheckinsOfToday(@Param("courseId") final String course);

    @Query("SELECT c.id, u.first_name, u.last_name, u.email, c.checked_on, c.checked_in_at FROM checks c" +
            " INNER JOIN users u ON c.qrcode = u.username" +
            " INNER JOIN events e ON e.check_id = c.id" +
            " WHERE c.checked_on = CURRENT_DATE AND e.check_type = 'OUT' AND e.course_id = :courseId")
    List<CheckDetails> findAllCheckoutsOfToday(@Param("courseId") final Long courseId);

}

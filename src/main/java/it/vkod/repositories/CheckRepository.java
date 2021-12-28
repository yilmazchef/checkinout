package it.vkod.repositories;


import it.vkod.models.entities.Check;
import it.vkod.models.entities.CheckType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.validation.constraints.NotEmpty;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

public interface CheckRepository extends JpaRepository<Check, Long>, JpaSpecificationExecutor<Check> {

    @Query("select c from Check c where c.active = true and (c.created = :date or c.updated = :date)")
    List<Check> findAll(@Param("date") final ZonedDateTime date);

    @Query("select c from Check c where c.active = true and (c.created = :date or c.updated = :date) and c.attendee.username = :username")
    List<Check> findAllByAttendee(@Param("date") final ZonedDateTime date, final @Param("username") @NotEmpty String username);

    @Query("select c from Check c where c.active = true and (c.created = :date or c.updated = :date) and c.type in :type and c.attendee.username = :username")
    List<Check> findAllByAttendee(@Param("date") final ZonedDateTime date, @Param("type") final Collection<CheckType> type, final @Param("username") @NotEmpty String username);

    @Query("select c from Check c where c.active = true and (c.created = :date or c.updated = :date) and c.type in :type")
    List<Check> findAllByType(@Param("date") final ZonedDateTime date, @Param("type") final Collection<CheckType> type);

    @Query("select c from Check c where c.active = true and (c.created = :date or c.updated = :date) and c.organizer.username = :username")
    List<Check> findAllByOrganizer(@Param("date") final ZonedDateTime date, final @Param("username") @NotEmpty String username);

    @Query("select c from Check c where c.active = true and c.course = :course")
    List<Check> findAllByCourse(@Param("course") final String course);

    @Query("select c from Check c where c.active = true and c.course = :course and (c.created = :date or c.updated = :date) and c.type in :type")
    List<Check> findAllByCourseAndType(final @Param("course") @NotEmpty String course, @Param("date") final ZonedDateTime date, @Param("type") final Collection<CheckType> type);

}

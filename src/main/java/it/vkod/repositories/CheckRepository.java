package it.vkod.repositories;


import it.vkod.models.entities.Check;
import it.vkod.models.entities.Course;
import it.vkod.models.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.List;

public interface CheckRepository extends JpaRepository<Check, Long>, JpaSpecificationExecutor<Check> {

    @Query("select c from Check c where c.active = true and c.onDate = current_date")
    List<Check> findAllToday();

    @Query("select c from Check c where c.active = true and c.onDate = :onDate")
    List<Check> findAll(@Param("onDate") final java.sql.Date onDate);

    @Query("select c from Check c where c.active = true and c.onDate = :onDate and c.atTime = :atTime and c.attendee.username = :username")
    List<Check> findAllByAttendee(@Param("onDate") final java.sql.Date onDate, @Param("atTime") final java.sql.Time atTime, @Param("username") @NotEmpty final String username);

    @Query("select c from Check c where c.active = true and c.onDate = :onDate and c.event in :events and c.attendee.username = :username")
    List<Check> findAllByAttendee(@Param("onDate") final java.sql.Date onDate, @Param("events") final Collection<Event> events, final @Param("username") @NotEmpty String username);

    @Query("select c from Check c where c.active = true and c.onDate = current_date and c.event = 'PHYSICAL_IN' and c.attendee.username = :username")
    List<Check> findAllCheckinsByAttendee(@Param("username") @NotEmpty final String username);

    @Query("select c from Check c where c.active = true and c.onDate = current_date and c.event = 'PHYSICAL_OUT' and c.attendee.username = :username")
    List<Check> findAllCheckoutsByAttendee(@Param("username") @NotEmpty final String username);

    @Query("select c from Check c where c.active = true and c.onDate = :onDate and c.atTime = :atTime and c.organizer.username = :username")
    List<Check> findAllByOrganizer(@Param("onDate") final java.sql.Date onDate, @Param("atTime") final java.sql.Time atTime, final @Param("username") @NotEmpty String username);

    @Query("select c from Check c where c.active = true and c.course = :course")
    List<Check> findAllByCourse(@Param("course") final Course course);

    @Query("select c from Check c where c.active = true and c.course = :course and c.onDate = :onDate and c.atTime = :atTime and c.event in :events")
    List<Check> findAllByCourseAndType(final @Param("course") @NotEmpty Course course, @Param("onDate") final java.sql.Date onDate, @Param("atTime") final java.sql.Time atTime, @Param("events") final Collection<Event> events);

}

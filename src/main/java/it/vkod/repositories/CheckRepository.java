package it.vkod.repositories;


import it.vkod.models.entities.Check;
import it.vkod.models.entities.CheckType;
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

    @Query("select c from Check c where c.active = true and c.onDate = :onDate and c.type in :type and c.attendee.username = :username")
    List<Check> findAllByAttendee(@Param("onDate") final java.sql.Date onDate, @Param("type") final Collection<CheckType> type, final @Param("username") @NotEmpty String username);

    @Query("select c from Check c where c.active = true and c.onDate = :onDate and c.type = :type and c.attendee.username = :username")
    List<Check> findAllByAttendee(@Param("onDate") final java.sql.Date onDate, @Param("type") final CheckType type, @Param("username") @NotEmpty final String username);

    @Query("select c from Check c where c.active = true and c.onDate = current_date and c.type = :type and c.attendee.username = :username")
    List<Check> findAllByAttendee(@Param("type") final CheckType type, @Param("username") @NotEmpty final String username);

    @Query("select c from Check c where c.active = true and c.onDate = current_date and c.type = 'PHYSICAL_IN' and c.attendee.username = :username")
    List<Check> findAllCheckinsByAttendee(@Param("username") @NotEmpty final String username);


    @Query("select c from Check c where c.active = true and c.onDate = :onDate and c.atTime = :atTime and c.type in :type and c.attendee.username = :username")
    List<Check> findAllByAttendee(@Param("onDate") final java.sql.Date onDate, @Param("atTime") final java.sql.Time atTime, @Param("type") final Collection<CheckType> type, final @Param("username") @NotEmpty String username);

    @Query("select c from Check c where c.active = true and c.onDate = :onDate and c.atTime = :onDate and c.type in :type")
    List<Check> findAllByType(@Param("onDate") final java.sql.Date onDate, @Param("type") final Collection<CheckType> type);

    @Query("select c from Check c where c.active = true and c.onDate = :onDate and c.atTime = :atTime and c.organizer.username = :username")
    List<Check> findAllByOrganizer(@Param("onDate") final java.sql.Date onDate, @Param("atTime") final java.sql.Time atTime, final @Param("username") @NotEmpty String username);

    @Query("select c from Check c where c.active = true and c.onDate = :onDate and c.organizer.username = :username")
    List<Check> findAllByOrganizer(@Param("onDate") final java.sql.Date onDate, final @Param("username") @NotEmpty String username);


    @Query("select c from Check c where c.active = true and c.course = :course")
    List<Check> findAllByCourse(@Param("course") final String course);

    @Query("select c from Check c where c.active = true and c.course = :course and c.onDate = :onDate and c.atTime = :atTime and c.type in :type")
    List<Check> findAllByCourseAndType(final @Param("course") @NotEmpty String course, @Param("onDate") final java.sql.Date onDate, @Param("atTime") final java.sql.Time atTime, @Param("type") final Collection<CheckType> type);

}

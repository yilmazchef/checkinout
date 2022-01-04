package it.vkod.models.entities;


import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@ToString(onlyExplicitlyIncluded = true)
@Getter
@Setter
@RequiredArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "checks")
@Entity
public class Check implements Serializable, Cloneable, Persistable<Long> {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Min(value = 1000)
    @Max(9999)
    Integer validation;

    java.sql.Date onDate;

    java.sql.Time atTime;

    Integer gmtZone;

    @ToString.Include
    @NotEmpty
    String session;

    Boolean active = Boolean.TRUE;

    Float latitude;

    Float longitude;

    @NotEmpty
    String course;

    @Enumerated(EnumType.STRING)
    CheckType type;

    @ManyToOne(optional = false)
    @JoinColumn(name = "attendee_id", nullable = false)
    private User attendee;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;


    public Check setLatitude(@NotNull Float lat) {

        this.latitude = lat;
        return this;
    }


    public Check setLat(@NotNull Double lat) {

        this.latitude = lat.floatValue();
        return this;
    }


    public Check setLat(@NotNull String lat) {

        this.latitude = Float.parseFloat(lat);
        return this;
    }


    public Check setLongitude(@NotNull Float lon) {

        this.longitude = lon;
        return this;
    }


    public Check setLon(@NotNull Double lon) {

        this.longitude = lon.floatValue();
        return this;
    }


    public Check setLon(@NotNull String lon) {

        this.longitude = Float.parseFloat(lon);
        return this;
    }

    private boolean duplicated;

    public boolean isNew() {

        return this.id == null;
    }

    @Override
    public Check clone() {

        try {
            return ((Check) super.clone())
                    .setOrganizer(this.getOrganizer())
                    .setAttendee(this.getAttendee())
                    .setValidation(this.getValidation())
                    .setAtTime(this.getAtTime())
                    .setSession(this.getSession())
                    .setActive(this.getActive())
                    .setLatitude(this.getLatitude())
                    .setLongitude(this.getLongitude());

        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }


    @PrePersist
    public void prePersist() {

        final var zone = ZoneId.of("Europe/Brussels");
        final var current = ZonedDateTime.now(zone);

        this.gmtZone = ZonedDateTime.now(zone).getOffset().getTotalSeconds();
        this.onDate = java.sql.Date.valueOf(LocalDate.now());
        this.atTime = java.sql.Time.valueOf(LocalTime.now());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Check)) return false;

        Check check = (Check) o;

        return new EqualsBuilder().append(getOnDate(), check.getOnDate()).append(getAtTime(), check.getAtTime()).append(getActive(), check.getActive()).append(getCourse(), check.getCourse()).append(getType(), check.getType()).append(getAttendee(), check.getAttendee()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getOnDate()).append(getAtTime()).append(getActive()).append(getCourse()).append(getType()).append(getAttendee()).toHashCode();
    }
}

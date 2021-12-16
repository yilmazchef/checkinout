package it.vkod.data.entity;


import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table("events")
public class Event implements Serializable, Cloneable {

    @Id
    Long id;

    Long attendeeId;

    Long organizerId;

    Long checkId;

    String training;

    String checkType;

    public boolean isNew() {
        return this.id == null;
    }

    @Override
    public Event clone() {
        try {
            return ((Event) super.clone())
                    .setAttendeeId(this.getAttendeeId())
                    .setCheckId(this.getCheckId())
                    .setTraining(this.getTraining())
                    .setCheckType(this.getCheckType())
                    .setOrganizerId(this.getOrganizerId())
                    .setTraining(this.getTraining());
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return this.checkType;
    }
}

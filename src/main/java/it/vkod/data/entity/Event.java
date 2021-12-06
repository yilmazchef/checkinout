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
    private Long id;

    private Long attendeeId;

    private Long organizerId;

    private Long checkId;

    private String checkType;

    @Override
    public Event clone() {
        try {
            return ((Event) super.clone())
                    .setAttendeeId(this.getAttendeeId())
                    .setCheckId(this.getCheckId())
                    .setCheckType(this.getCheckType())
                    .setOrganizerId(this.getOrganizerId());
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}

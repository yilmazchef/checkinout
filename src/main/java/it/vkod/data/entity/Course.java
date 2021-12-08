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
@Table("courses")
public class Course implements Serializable, Cloneable {

    @Id
    Long id;

    String title;

    String description;

    Long parentId;

    public boolean isNew() {
        return this.id == null;
    }

    @Override
    public Course clone() {

        try {
            return ((Course) super.clone())
                    .setTitle(this.getTitle())
                    .setDescription(this.getDescription());

        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return this.title;
    }
}

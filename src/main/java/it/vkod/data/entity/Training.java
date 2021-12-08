package it.vkod.data.entity;


import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table("trainings")
public class Training implements Serializable, Cloneable {

    @Id
    Long id;

    Long courseId;

    Long studentId;

    LocalDate startDate;

    LocalDate endDate;

    public boolean isNew() {
        return this.id == null;
    }

    @Override
    public Training clone() {

        try {
            return ((Training) super.clone())
                    .setCourseId(this.getCourseId())
                    .setStudentId(this.getStudentId())
                    .setStartDate(this.getStartDate())
                    .setEndDate(this.getEndDate());

        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return this.courseId + "-" + this.studentId;
    }
}
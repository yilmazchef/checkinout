package it.vkod.models.dto;

import com.opencsv.bean.CsvBindByPosition;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CheckDetails implements Serializable, Cloneable {

    @CsvBindByPosition(position = 0)
    Long checkId;

    @CsvBindByPosition(position = 1)
    String firstName;

    @CsvBindByPosition(position = 2)
    String lastName;

    @CsvBindByPosition(position = 3)
    @EqualsAndHashCode.Include
    String email;

    @CsvBindByPosition(position = 4)
    LocalDate checkedOn;

    @CsvBindByPosition(position = 5)
    LocalTime checkedInAt;

    @CsvBindByPosition(position = 6)
    LocalTime checkedOutAt;

    @CsvBindByPosition(position = 7)
    String training;

    @Override
    public CheckDetails clone() {
        try {
            return ((CheckDetails) super.clone())
                    .setEmail(this.getEmail())
                    .setFirstName(this.getFirstName())
                    .setLastName(this.getLastName())
                    .setCheckedOn(this.getCheckedOn())
                    .setCheckedInAt(this.getCheckedInAt())
                    .setCheckedOutAt(this.getCheckedOutAt())
                    .setTraining(this.getTraining());

        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

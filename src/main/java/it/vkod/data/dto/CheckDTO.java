package it.vkod.data.dto;

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
public class CheckDTO implements Serializable, Cloneable {

    @CsvBindByPosition(position = 0)
    String firstName;
    @CsvBindByPosition(position = 1)
    String lastName;
    @CsvBindByPosition(position = 2)
    @EqualsAndHashCode.Include
    String email;
    @CsvBindByPosition(position = 3)
    LocalDate checkedOn;
    @CsvBindByPosition(position = 4)
    LocalTime checkedInAt;
    @CsvBindByPosition(position = 5)
    LocalTime checkedOutAt;

    @Override
    public CheckDTO clone() {
        try {
            return ((CheckDTO) super.clone())
                    .setEmail(this.getEmail())
                    .setFirstName(this.getFirstName())
                    .setLastName(this.getLastName())
                    .setCheckedOn(this.getCheckedOn())
                    .setCheckedInAt(this.getCheckedInAt())
                    .setCheckedOutAt(this.getCheckedOutAt());

        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

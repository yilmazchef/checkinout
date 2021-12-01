package it.vkod.data.dto;

import com.opencsv.bean.CsvBindByPosition;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ChecksGridData implements Serializable, Cloneable {

    @CsvBindByPosition(position = 0)
    private String firstName;
    @CsvBindByPosition(position = 1)
    private String lastName;
    @CsvBindByPosition(position = 2)
    @EqualsAndHashCode.Include
    private String email;
    @CsvBindByPosition(position = 3)
    private LocalDate checkedOn;
    @CsvBindByPosition(position = 4)
    private LocalTime checkedInAt;
    @CsvBindByPosition(position = 5)
    private LocalTime checkedOutAt;

    @Override
    public ChecksGridData clone() {
        try {
            ChecksGridData clone = (ChecksGridData) super.clone();
            clone.setEmail(this.getEmail());
            clone.setFirstName(this.getFirstName());
            clone.setLastName(this.getLastName());
            clone.setCheckedOn(this.getCheckedOn());
            clone.setCheckedInAt(this.getCheckedInAt());
            clone.setCheckedOutAt(this.getCheckedOutAt());

            return clone;

        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

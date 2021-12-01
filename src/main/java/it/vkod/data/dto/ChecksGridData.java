package it.vkod.data.dto;

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

  private String firstName;
  private String lastName;
  @EqualsAndHashCode.Include private String email;
  private LocalDate checkedOn;
  private LocalTime checkedInAt;
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

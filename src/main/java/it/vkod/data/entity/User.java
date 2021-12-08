package it.vkod.data.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table("users")
public class User implements Serializable, Cloneable {

    @Id
    Long id;

    String username;

    String phone;

    String email;

    String firstName;

    String lastName;

    String hashedPassword;

    String roles;

    Date registeredOn;

    Time registeredAt;

    Time updatedAt;

    String profile;

    public boolean isNew() {
        return this.id == null;
    }

    @Override
    public User clone() {

        try {
            return ((User) super.clone())
                    .setEmail(this.getEmail())
                    .setFirstName(this.getEmail())
                    .setLastName(this.getEmail())
                    .setPhone(this.getPhone())
                    .setProfile(this.getProfile());

        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return this.username;
    }
}

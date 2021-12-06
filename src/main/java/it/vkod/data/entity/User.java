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
    private Long id;

    private String username;

    private String phone;

    private String email;

    private String firstName;

    private String lastName;

    private String hashedPassword;

    private String roles;

    private Date registeredOn;

    private Time registeredAt;

    private Time updatedAt;

    private String profile;

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
}

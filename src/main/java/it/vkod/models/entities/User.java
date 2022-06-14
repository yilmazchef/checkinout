package it.vkod.models.entities;


import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

@ToString(onlyExplicitlyIncluded = true)
@Getter
@Setter
@RequiredArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
@Entity
public class User implements Serializable, Cloneable, Persistable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ToString.Include
    @NotEmpty
    String username;

    @Pattern(regexp = "^\\+[1-9]{1}[0-9]{3,14}$")
    String phone;

    @Email
    String email;

    @NotEmpty
    String firstName;

    @NotEmpty
    String lastName;

    @NotEmpty
    String password;

    @CollectionTable(name = "roles")
    @ElementCollection(fetch = FetchType.EAGER)
    Set<Role> roles;

    Timestamp registered;
    
    @URL
    String profile;

    @Enumerated(EnumType.STRING)
    private Course course;


    @Override
    public boolean isNew() {

        return this.id == null;
    }


    /**
     * @return Clone of user without ID
     */
    @Override
    public User clone() {

        try {
            return ((User) super.clone())
                    .setEmail(this.getEmail())
                    .setFirstName(this.getEmail())
                    .setLastName(this.getEmail())
                    .setPhone(this.getPhone())
                    .setProfile(this.getProfile())
                    .setCourse(this.getCourse());

        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }


    @PrePersist
    public void prePersist() {

        this.registered = java.sql.Timestamp.valueOf(LocalDateTime.now());

    }


    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof User)) {
            return false;
        }

        final User user = (User) o;

        return new EqualsBuilder().append(getUsername(), user.getUsername()).append(getPhone(), user.getPhone()).append(getEmail(), user.getEmail()).isEquals();
    }


    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 37).append(getUsername()).append(getPhone()).append(getEmail()).toHashCode();
    }

    @Override
    public String toString() {
        return this.getFirstName() + " " + this.getLastName();
    }
}

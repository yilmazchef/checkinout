package it.vkod.models.entities;


import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@ToString( onlyExplicitlyIncluded = true )
@Getter
@Setter
@RequiredArgsConstructor
@Accessors( chain = true )
@FieldDefaults( level = AccessLevel.PRIVATE )
@Table( name = "users" )
@Entity
public class User implements Serializable, Cloneable, Persistable< Long > {

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	Long id;

	@ToString.Include
	@NotEmpty
	String username;

	@Pattern( regexp = "^\\+[1-9]{1}[0-9]{3,14}$" )
	String phone;

	@Email
	String email;

	@NotEmpty
	String firstName;

	@NotEmpty
	String lastName;

	@NotEmpty
	String password;

	@CollectionTable( name = "roles" )
	@ElementCollection( fetch = FetchType.EAGER )
	Set< UserRole > roles = new LinkedHashSet<>();

	ZonedDateTime registered;

	ZonedDateTime updated;

	String profile;

	@NotEmpty
	private String course;


	@Override
	public boolean isNew() {

		return this.id == null;
	}


	@Override
	public User clone() {

		try {
			return ( ( User ) super.clone() )
					.setEmail( this.getEmail() )
					.setFirstName( this.getEmail() )
					.setLastName( this.getEmail() )
					.setPhone( this.getPhone() )
					.setProfile( this.getProfile() )
					.setCourse( this.getCourse() );

		} catch ( CloneNotSupportedException e ) {
			throw new AssertionError();
		}
	}


	@PrePersist
	public void prePersist() {

		this.registered = ZonedDateTime.now();
	}


	@PreUpdate
	public void preUpdate() {

		this.updated = ZonedDateTime.now();
	}

}

package it.vkod.models.entities;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

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

	@NotEmpty
	String username;

	String phone;

	@Email
	String email;

	@NotEmpty
	String firstName;

	@NotEmpty
	String lastName;

	@NotEmpty
	String password;

	@NotEmpty
	String roles;

	Date registeredOn;

	Time registeredAt;

	Time updatedAt;

	String profile;

	@OneToMany( fetch = FetchType.EAGER, mappedBy = "attendee", orphanRemoval = true )
	private List< Check > attendances = new ArrayList<>();

	@OneToMany( fetch = FetchType.EAGER, mappedBy = "organizer", orphanRemoval = true )
	private List< Check > organizations = new ArrayList<>();

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


	@Override
	public String toString() {

		return this.getFirstName() + " " + this.getLastName();
	}

}

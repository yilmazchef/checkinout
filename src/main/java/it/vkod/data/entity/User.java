package it.vkod.data.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Table( "users" )
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


	public User withId( final Long id ) {

		this.id = id;
		return this;
	}


	public User withUsername( final String username ) {

		this.username = username;
		return this;
	}


	public User withEmail( final String email ) {

		this.email = email;
		return this;
	}


	public User withPhone( final String phone ) {

		this.phone = phone;
		return this;
	}


	public User withFirstName( final String firstName ) {

		this.firstName = firstName;
		return this;
	}


	public User withLastName( final String lastName ) {

		this.lastName = lastName;
		return this;
	}


	public User withHashedPassword( final String hashedPassword ) {

		this.hashedPassword = hashedPassword;
		return this;
	}


	public User withRoles( final String roles ) {
		
		this.roles = roles;
		return this;
	}


	public User withRegisteredOn( final Date registeredOn ) {

		this.registeredOn = registeredOn;
		return this;
	}


	public User withRegisteredAt( final Time registeredAt ) {

		this.registeredAt = registeredAt;
		return this;
	}


	public User withUpdatedAt( final Time updatedAt ) {

		this.updatedAt = updatedAt;
		return this;
	}


	public User withProfile( final String profile ) {

		this.profile = profile;
		return this;
	}


	public boolean isNew() {

		return this.username == null;
	}


	@Override
	public User clone() {

		try {
			final User clone = ( User ) super.clone();
			// TODO: copy mutable state here, so the clone can't change the internals of the original
			return clone;
		} catch ( CloneNotSupportedException e ) {
			throw new AssertionError();
		}
	}

}



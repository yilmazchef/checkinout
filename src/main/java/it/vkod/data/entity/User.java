package it.vkod.data.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import it.vkod.data.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Entity
@Getter
@RequiredArgsConstructor
@Setter
@Accessors( chain = true )
@ToString( onlyExplicitlyIncluded = true )
public class User implements Persistable< Long >, Serializable {

	@Id
	@GeneratedValue
	private Long id;

	@ToString.Include
	@Column( nullable = false, unique = true )
	private String username;

	@Column( unique = true )
	private String phone;

	private String firstName;

	private String lastName;

	@Column( nullable = false )
	@JsonIgnore
	private String hashedPassword;

	@ElementCollection( fetch = FetchType.EAGER )
	private Set< Role > roles;

	private Date registeredOn;

	private Time registeredAt;

	private Time updatedAt;

	@Lob
	private String profile;

	@OneToMany( mappedBy = "user", cascade = { CascadeType.REMOVE, CascadeType.MERGE }, orphanRemoval = true )
	private final List< Check > checks = new ArrayList<>();


	@PrePersist
	public void prePersist() {

		if ( this.roles != null ) {
			this.roles = new HashSet<>();
			this.roles.add( Role.USER );
		}

		this.registeredOn = Objects.requireNonNull( Date.valueOf( LocalDate.now() ) );
		this.registeredAt = Objects.requireNonNull( Time.valueOf( LocalTime.now() ) );
	}


	@PreUpdate
	public void preUpdate() {

		if ( this.roles.isEmpty() ) {
			this.roles.add( Role.USER );
		}
		this.updatedAt = Objects.requireNonNull( Time.valueOf( LocalTime.now() ) );
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */


	@Override
	public int hashCode() {

		return Objects.hash( username, phone );
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */


	@Override
	public boolean equals( Object obj ) {

		if ( this == obj ) {
			return true;
		}
		if ( !( obj instanceof User ) ) {
			return false;
		}
		User other = ( User ) obj;
		return Objects.equals( username, other.username ) && Objects.equals( phone, other.phone );
	}


	@Override
	public boolean isNew() {

		return this.username == null;
	}

}

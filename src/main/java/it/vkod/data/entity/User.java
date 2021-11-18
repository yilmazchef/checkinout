package it.vkod.data.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import it.vkod.data.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.util.Set;

@Getter
@RequiredArgsConstructor
@Setter
@Accessors( chain = true )
@ToString
@Entity
public class User implements Persistable< Long >, Serializable {

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

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

	@Override
	public boolean equals( final Object o ) {

		if ( this == o ) {
			return true;
		}

		if ( !( o instanceof User ) ) {
			return false;
		}

		final User user = ( User ) o;

		return new EqualsBuilder().append( getId(), user.getId() ).append( getUsername(), user.getUsername() ).isEquals();
	}


	@Override
	public int hashCode() {

		return new HashCodeBuilder( 17, 37 ).append( getId() ).append( getUsername() ).toHashCode();
	}


	@Override
	public boolean isNew() {

		return this.username == null;
	}

}



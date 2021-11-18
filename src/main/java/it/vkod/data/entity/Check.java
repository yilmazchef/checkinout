package it.vkod.data.entity;


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

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Accessors( chain = true )
@ToString
public class Check implements Persistable< Long >, Serializable {

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	private Integer pincode;

	private Date checkedOn;

	private Time checkedInAt;

	private Time checkedOutAt;

	private String currentSession;

	private boolean isActive;

	@Column( unique = true )
	@Lob
	private String qrcode;

	private Float lat;

	private Float lon;


	@Override
	public boolean equals( final Object o ) {

		if ( this == o ) {
			return true;
		}

		if ( !( o instanceof Check ) ) {
			return false;
		}

		final Check check = ( Check ) o;

		return new EqualsBuilder().append( getId(), check.getId() ).isEquals();
	}


	@Override
	public int hashCode() {

		return new HashCodeBuilder( 17, 37 ).append( getId() ).toHashCode();
	}


	@Override
	public boolean isNew() {

		return this.id == null;
	}


	@OneToOne( optional = false )
	private Event event;


	public Event getEvent() {

		return event;
	}

}

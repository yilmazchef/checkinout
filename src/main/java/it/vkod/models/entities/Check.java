package it.vkod.models.entities;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@Accessors( chain = true )
@FieldDefaults( level = AccessLevel.PRIVATE )
@Table( name = "checks" )
@Entity
public class Check implements Serializable, Cloneable, Persistable< Long > {

	protected static final String LATITUDE_PATTERN = "^(\\+|-)?(?:90(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-8][0-9])(?:(?:\\.[0-9]{1,6})?))$";
	protected static final String LONGITUDE_PATTERN = "^(\\+|-)?(?:180(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-9][0-9]|1[0-7][0-9])(?:(?:\\.[0-9]{1,6})?))$";


	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	Long id;

	@Min( value = 1000 )
	@Max( 9999 )
	Integer validation;

	@CreationTimestamp
	ZonedDateTime in;

	@UpdateTimestamp
	ZonedDateTime out;

	@NotEmpty
	@Column( unique = true )
	String session;

	Boolean active = Boolean.TRUE;

	@Pattern( regexp = LATITUDE_PATTERN )
	Float latitude;

	@Pattern( regexp = LONGITUDE_PATTERN )
	Float longitude;

	@NotEmpty
	String course;

	@Enumerated( EnumType.STRING )
	CheckType type;

	@ManyToOne( optional = false )
	@JoinColumn( name = "attendee_id", nullable = false )
	private User attendee;


	public Check setAttendee( @NotNull final User attendee ) {

		attendee.getAttendances().add( this );
		this.attendee = attendee;
		return this;
	}


	@ManyToOne( optional = false )
	@JoinColumn( name = "organizer_id", nullable = false )
	private User organizer;


	public Check setOrganizer( @NotNull final User organizer ) {

		organizer.getOrganizations().add( this );
		this.organizer = organizer;
		return this;
	}


	public Check setLatitude( @NotNull Float lat ) {

		this.latitude = lat;
		return this;
	}


	public Check setLat( @NotNull Double lat ) {

		this.latitude = lat.floatValue();
		return this;
	}


	public Check setLat( @NotNull String lat ) {

		this.latitude = Float.parseFloat( lat );
		return this;
	}


	public Check setLongitude( @NotNull Float lon ) {

		this.longitude = lon;
		return this;
	}


	public Check setLon( @NotNull Double lon ) {

		this.longitude = lon.floatValue();
		return this;
	}


	public Check setLon( @NotNull String lon ) {

		this.longitude = Float.parseFloat( lon );
		return this;
	}


	public boolean isNew() {

		return this.id == null;
	}


	@Override
	public Check clone() {

		try {
			return ( ( Check ) super.clone() )
					.setOrganizer( this.getOrganizer() )
					.setAttendee( this.getAttendee() )
					.setValidation( this.getValidation() )
					.setIn( this.getIn() )
					.setOut( this.getOut() )
					.setSession( this.getSession() )
					.setActive( this.getActive() )
					.setLatitude( this.getLatitude() )
					.setLongitude( this.getLongitude() );

		} catch ( CloneNotSupportedException e ) {
			throw new AssertionError();
		}
	}


	@Override
	public String toString() {

		return this.getAttendee().getUsername();
	}

}

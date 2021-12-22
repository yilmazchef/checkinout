package it.vkod.models.entities;


import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

@Getter
@Setter
@RequiredArgsConstructor
@Accessors( chain = true )
@FieldDefaults( level = AccessLevel.PRIVATE )
@Table( name = "checks" )
@Entity
public class Check implements Serializable, Cloneable, Persistable< Long > {

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	Long id;

	Integer pin;

	Date checkedOn;

	Time checkedInAt;

	Time checkedOutAt;

	String session;

	Boolean active;

	Float lat;

	Float lon;

	Boolean validLocation;

	String course;

	@Enumerated( EnumType.STRING )
	CheckType type;

	@ManyToOne( optional = false )
	@JoinColumn( name = "attendee_id", nullable = false )
	private User attendee;

	@ManyToOne( optional = false )
	@JoinColumn( name = "organizer_id", nullable = false )
	private User organizer;


	public Check setLat( Float lat ) {

		this.lat = lat;
		return this;
	}


	public Check setLat( Double lat ) {

		this.lat = lat.floatValue();
		return this;
	}


	public Check setLat( String lat ) {

		this.lat = Float.parseFloat( lat );
		return this;
	}


	public Check setLon( Float lon ) {

		this.lon = lon;
		return this;
	}


	public Check setLon( Double lon ) {

		this.lon = lon.floatValue();
		return this;
	}


	public Check setLon( String lon ) {

		this.lon = Float.parseFloat( lon );
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
					.setPin( this.getPin() )
					.setCheckedOutAt( this.getCheckedOutAt() )
					.setCheckedInAt( this.getCheckedInAt() )
					.setSession( this.getSession() )
					.setCheckedOn( this.getCheckedOn() )
					.setActive( this.getActive() )
					.setLat( this.getLat() )
					.setLon( this.getLon() );

		} catch ( CloneNotSupportedException e ) {
			throw new AssertionError();
		}
	}


	@Override
	public String toString() {

		return this.getCheckedOn().toString() + " - " + this.getAttendee().getUsername();
	}

}

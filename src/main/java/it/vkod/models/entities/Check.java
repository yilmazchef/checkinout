package it.vkod.models.entities;


import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

@ToString( onlyExplicitlyIncluded = true )
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

	@Min( value = 1000 )
	@Max( 9999 )
	Integer validation;

	ZonedDateTime created;

	ZonedDateTime updated;

	@ToString.Include
	@NotEmpty
	String session;

	Boolean active = Boolean.TRUE;

	Float latitude;

	Float longitude;

	@NotEmpty
	String course;

	@Enumerated( EnumType.STRING )
	CheckType type;

	@ManyToOne( optional = false )
	@JoinColumn( name = "attendee_id", nullable = false )
	private User attendee;

	@ManyToOne( optional = false )
	@JoinColumn( name = "organizer_id", nullable = false )
	private User organizer;


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
					.setCreated( this.getCreated() )
					.setUpdated( this.getUpdated() )
					.setSession( this.getSession() )
					.setActive( this.getActive() )
					.setLatitude( this.getLatitude() )
					.setLongitude( this.getLongitude() );

		} catch ( CloneNotSupportedException e ) {
			throw new AssertionError();
		}
	}


	@PrePersist
	public void prePersist() {

		this.created = ZonedDateTime.now();
	}


	@PreUpdate
	public void preUpdate() {

		this.updated = ZonedDateTime.now();
	}

}

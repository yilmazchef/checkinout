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
@Table("checks")
public class Check implements Serializable, Cloneable {

	@Id
	private Long id;

	private Integer pincode;

	private Date checkedOn;

	private Time checkedInAt;

	private Time checkedOutAt;

	private String currentSession;

	private Boolean isActive;

	private String qrcode;

	private Float lat;

	private Float lon;

	public Check withId( final Long id ) {

		this.id = id;
		return this;
	}


	public Check withPincode( final Integer pincode ) {

		this.pincode = pincode;
		return this;
	}


	public Check withCheckedOn( final Date checkedOn ) {

		this.checkedOn = checkedOn;
		return this;
	}


	public Check withCheckedInAt( final Time checkedInAt ) {

		this.checkedInAt = checkedInAt;
		return this;
	}


	public Check withCheckedOutAt( final Time checkedOutAt ) {

		this.checkedOutAt = checkedOutAt;
		return this;
	}


	public Check withCurrentSession( final String currentSession ) {

		this.currentSession = currentSession;
		return this;
	}


	public Check withActive( final Boolean active ) {

		isActive = active;
		return this;
	}


	public Check withQrcode( final String qrcode ) {

		this.qrcode = qrcode;
		return this;
	}


	public Check withLat( final Float lat ) {

		this.lat = lat;
		return this;
	}


	public Check withLon( final Float lon ) {

		this.lon = lon;
		return this;
	}

	public boolean isNew() {

		return this.id == null;
	}


	@Override
	public Check clone() {

		try {
			final Check clone = ( Check ) super.clone();
			// TODO: copy mutable state here, so the clone can't change the internals of the original
			return clone;
		} catch ( CloneNotSupportedException e ) {
			throw new AssertionError();
		}
	}

}

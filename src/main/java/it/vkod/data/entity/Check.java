package it.vkod.data.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Accessors( chain = true )
@ToString( onlyExplicitlyIncluded = true )
public class Check implements Persistable< Long > {

	@Id
	@GeneratedValue
	private Long id;

	@Min( 100000 )
	@Max( 999999 )
	@JsonIgnore
	private Integer pincode;

	@FutureOrPresent
	private Date checkedOn;

	@ToString.Include
	private Time checkedInAt;

	@ToString.Include
	private Time checkedOutAt;

	private String currentSession;

	private boolean isActive;

	@Column( unique = true )
	@Lob
	private String qrcode;

	@PositiveOrZero
	private Float lat;

	@PositiveOrZero
	private Float lon;

	@ManyToOne
	private User user;


	@PrePersist
	public void prePersist() {

		this.setCheckedInAt( Objects.requireNonNull( Time.valueOf( LocalTime.now() ) ) )
				.setActive( true )
				.setCurrentSession( UUID.randomUUID().toString() )
				.setPincode( 100000 + new Random().nextInt( 999_999 ) );

	}


	@PreUpdate
	public void preUpdate() {

		this.setCheckedOutAt( Objects.requireNonNull( Time.valueOf( LocalTime.now() ) ) )
				.setActive( true );

	}


	@Override
	public boolean equals( final Object o ) {

		if ( this == o ) {
			return true;
		}

		if ( !( o instanceof Check ) ) {
			return false;
		}

		final Check check = ( Check ) o;

		return new EqualsBuilder().append( isActive(), check.isActive() ).append( getId(), check.getId() ).append( getUser(), check.getUser() ).append( getCheckedOn(), check.getCheckedOn() ).isEquals();
	}


	@Override
	public int hashCode() {

		return new HashCodeBuilder( 17, 37 ).append( getId() ).append( getUser() ).append( getCheckedOn() ).append( isActive() ).toHashCode();
	}


	@Override
	public boolean isNew() {

		return this.id == null;
	}

}

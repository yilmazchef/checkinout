package it.vkod.data.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Table( "events" )
public class Event implements Serializable, Cloneable {

	@Id
	private Long id;

	private Long attendeeId;

	private Long organizerId;

	private Long checkId;

	private String checkType;


	public Event withCheckType( final String checkType ) {

		this.checkType = checkType;
		return this;
	}


	public Event withId( final Long id ) {

		this.id = id;
		return this;
	}


	public Event withAttendeeId( final Long attendeeId ) {

		this.attendeeId = attendeeId;
		return this;
	}


	public Event withOrganizerId( final Long organizerId ) {

		this.organizerId = organizerId;
		return this;
	}


	public Event withCheckId( final Long checkId ) {

		this.checkId = checkId;
		return this;
	}


	public boolean isNew() {

		return this.id == null;
	}


	@Override
	public Event clone() {

		try {

			final Event clonedEvent = ( Event ) super.clone();
			clonedEvent.setAttendeeId(this.getAttendeeId());
			clonedEvent.setCheckId(this.getCheckId());
			clonedEvent.setCheckType(this.getCheckType());
			clonedEvent.setOrganizerId(this.getOrganizerId());

			return clonedEvent;
		} catch ( CloneNotSupportedException e ) {
			throw new AssertionError();
		}
	}

}

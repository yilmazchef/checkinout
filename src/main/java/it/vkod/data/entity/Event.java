package it.vkod.data.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@RequiredArgsConstructor
@Setter
@Accessors( chain = true )
@ToString
@Entity
public class Event implements Persistable< Long >, Serializable {

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;

	@ManyToOne
	private User attendee;

	@ManyToOne
	private User organizer;

	@OneToOne( mappedBy = "event" )
	private Check check;


	public Event setCheck( Check check ) {

		check.setEvent( this );
		this.setCheck( check );
		return this;
	}


	@Override
	public boolean isNew() {

		return this.id == null;
	}

}

package it.vkod.views.components;


import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.router.Route;
import it.vkod.models.entities.User;
import it.vkod.services.flow.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;

@Route( "message-list" )
public class MessageListComponent extends Div {

	public MessageListComponent( @Autowired UserService userService ) {

		User user = userService.fetchAll().iterator().next();
		MessageList list = new MessageList();
		Instant yesterday = LocalDateTime.now().minusDays( 1 )
				.toInstant( ZoneOffset.UTC );
		Instant fiftyMinsAgo = LocalDateTime.now().minusMinutes( 50 )
				.toInstant( ZoneOffset.UTC );
		MessageListItem message1 = new MessageListItem(
				"Linsey, could you check if the details with the order are okay?",
				yesterday, "Matt Mambo" );
		message1.setUserColorIndex( 1 );
		MessageListItem message2 = new MessageListItem( "All good. Ship it.",
				fiftyMinsAgo, user.getUsername(), user.getProfile() );
		message2.setUserColorIndex( 2 );
		list.setItems( Arrays.asList( message1, message2 ) );
		add( list );
	}

}


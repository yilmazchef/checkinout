package it.vkod.views.layouts;


import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import it.vkod.models.entities.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;

public class MessageLayout extends Div {

    public void addMessage() {

    }

    public MessageLayout(final User from) {

        final var list = new MessageList();

        final var yesterday = LocalDateTime.now().minusDays(1).toInstant(ZoneOffset.UTC);
        final var instant = LocalDateTime.now().minusMinutes(50).toInstant(ZoneOffset.UTC);

        final var message1 = new MessageListItem("Linsey, could you check if the details with the order are okay?", yesterday, "Matt Mambo");
        message1.setUserColorIndex(1);
        final var message2 = new MessageListItem("All good. Ship it.", instant, from.getUsername(), from.getProfile());
        message2.setUserColorIndex(2);

        list.setItems(Arrays.asList(message1, message2));
        add(list);
    }

}


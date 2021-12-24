package it.vkod.services.flow;


import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import it.vkod.models.entities.CheckType;
import it.vkod.models.entities.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
@VaadinSessionScope
@Data
@FieldDefaults( level = AccessLevel.PRIVATE )
public class SessionService {

	String session = VaadinSession.getCurrent().getSession().getId();

	User organizer;

	Set< User > attendees = new LinkedHashSet<>();

	CheckType type;

	String course;

}
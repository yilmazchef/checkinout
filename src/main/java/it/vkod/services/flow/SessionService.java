package it.vkod.services.flow;

import java.util.UUID;

import com.vaadin.flow.spring.annotation.VaadinSessionScope;

import org.springframework.stereotype.Component;

@Component
@VaadinSessionScope
public class SessionService {

    private String uid = UUID.randomUUID().toString();

    public String getText() {
        return "session " + uid;
    }
}
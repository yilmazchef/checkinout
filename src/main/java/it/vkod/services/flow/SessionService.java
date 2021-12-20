package it.vkod.services.flow;

import java.util.UUID;

import com.vaadin.flow.spring.annotation.VaadinSessionScope;

import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Component
@VaadinSessionScope
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionService {

    String uid = UUID.randomUUID().toString();

    String checkType;

    String trainingCode;

}
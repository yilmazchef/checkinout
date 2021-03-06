package it.vkod.services.flow;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import it.vkod.configs.SecurityConfiguration;
import it.vkod.models.entities.Role;
import it.vkod.models.entities.User;
import it.vkod.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;


    private Optional<Authentication> getAuthentication() {

        SecurityContext context = SecurityContextHolder.getContext();
        return Optional.ofNullable(context.getAuthentication())
                .filter(authentication -> !(authentication instanceof AnonymousAuthenticationToken));
    }


    public Optional<User> get() {

        return getAuthentication().map(authentication -> userRepository.getByUsername(authentication.getName()));
    }

    public boolean hasRole(final Role role) {
        return get().map(user -> user.getRoles().stream().anyMatch(userRole -> userRole == role)).orElse(false);
    }


    public void logout() {

        UI.getCurrent().getPage().setLocation(SecurityConfiguration.LOGOUT_URL);
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
    }

}

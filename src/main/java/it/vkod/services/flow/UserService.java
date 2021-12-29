package it.vkod.services.flow;


import it.vkod.models.entities.User;
import it.vkod.models.entities.UserRole;
import it.vkod.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        final var oUser = userRepository.findByUsername(username);
        if (oUser.isEmpty()) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        } else {
            return new org.springframework.security.core.userdetails.User(oUser.get().getUsername(), oUser.get().getPassword(),
                    getAuthorities(oUser.get()));
        }
    }


    private static List<GrantedAuthority> getAuthorities(User user) {

        return user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

    }


    public Set<User> all() {

        final var userIterator = userRepository.findAll().iterator();
        final var userSet = new LinkedHashSet<User>();

        while (userIterator.hasNext()) {
            userSet.add(userIterator.next());
        }

        return userSet;
    }


    public List<User> fromCourse(final String course) {

        return userRepository.findAllByCourse(course);
    }

    public List<User> students() {

        return userRepository.findAllByRoles(Collections.singleton(UserRole.STUDENT));
    }

    public List<User> teachers() {

        return userRepository.findAllByRoles(Collections.singleton(UserRole.TEACHER));
    }

    public List<User> managers() {

        return userRepository.findAllByRoles(Set.of(UserRole.MANAGER, UserRole.ADMIN));
    }

    public List<User> admins() {

        return userRepository.findAllByRoles(Collections.singleton(UserRole.ADMIN));
    }


    public Optional<User> findByUsernameAndHashedPassword(final String username, final String hashedPassword) {

        return userRepository.findByUsernameAndPassword(username, hashedPassword);
    }


    public Optional<User> findByEmailAndHashedPassword(final String email, final String hashedPassword) {

        return userRepository.findByEmailAndPassword(email, hashedPassword);
    }


    public Optional<User> findByPhoneAndHashedPassword(final String phone, final String hashedPassword) {

        return userRepository.findByPhoneAndPassword(phone, hashedPassword);
    }


    public Optional<User> findByUsername(final String username) {

        return userRepository.findByUsername(username);
    }


    public Optional<User> findByUsernameOrEmailOrPhone(final String username, final String email, final String phone) {

        return userRepository.findByUsernameOrEmailOrPhone(username, email, phone);
    }


    public User getByUsername(final String username) {

        return userRepository.getByUsername(username);
    }


    public Boolean existsByUsername(final String username) {

        return userRepository.existsByUsername(username);
    }


    public Optional<User> findUserById(Long userId) {

        return userRepository.findById(userId);
    }


    @Transactional
    public User createUser(User entity) {

        return userRepository.save(entity);
    }


    public List<String> fetchAllUsernames(final String keyword) {

        return userRepository.findAllByUsernameLikeOrPhoneContaining(keyword, keyword).stream().map(User::getUsername).collect(Collectors.toUnmodifiableList());
    }


    public List<String> fetchAllUsernamesByCourse(final String course) {

        return userRepository.findAllByCourse(course).stream().map(User::getUsername).collect(Collectors.toUnmodifiableList());
    }

}

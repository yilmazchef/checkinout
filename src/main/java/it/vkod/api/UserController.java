package it.vkod.api;

import it.vkod.models.entities.User;
import it.vkod.repositories.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@RestController
@RequestMapping(path = UserController.BASE_ENDPOINT)
public class UserController {

    public static final String BASE_ENDPOINT = "/api/v1/users";

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserController(final UserRepository userRepository, final BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(path = "/add")
    public String createOne(@RequestParam final String firstName, @RequestParam String lastName, @RequestParam String email, @RequestParam final String password) {

        if (userRepository.existsByUsername(email.replace("@intecbrussel.be", "")) == Boolean.TRUE) {
            return "User with requested username or email does already EXIST.";
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);

        return "User with email " + user.getEmail() + " is created successfully.";
    }

    @PutMapping(path = "/update_password")
    public String updatePassword(@RequestParam final String email, @RequestParam final String oldPassword, @RequestParam final String newPassword) {

        if (userRepository.existsByUsername(email.replace("@intecbrussel.be", "")) == Boolean.FALSE) {
            return "User with requested username or email does NOT EXIST. Therefore CAN NOT be updated.";
        }

        User user = userRepository.getByUsername(email.replace("@intecbrussel.be", ""));
        
        if(user.getPassword().equals(passwordEncoder.encode(oldPassword))){
            return "User password is NOT correct!";
        }

        userRepository.save(user);

        return "User with email " + user.getEmail() + " is created successfully.";
    }

    @GetMapping(path = "/{id}")
    public List<User> fetchById(@PathVariable final Long id) {

        return userRepository.findAll();
    }

    @GetMapping(path = "/all")
    public List<User> fetchAll() {

        return userRepository.findAll();
    }

}

package it.vkod.api;


import it.vkod.models.entities.User;
import it.vkod.repositories.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @PostMapping(path = "/one")
    @ResponseBody
    public String createOne(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String email) {

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        userRepository.save(user);
        return "User Created";
    }


    @GetMapping(path = "/all")
    @ResponseBody
    public List<User> fetchAll() {

        return userRepository.findAll();
    }

}

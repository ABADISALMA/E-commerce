package org.example.userservice.Controllers;



import org.example.userservice.dtos.UserDTO;
import org.example.userservice.entities.User;
import org.example.userservice.services.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
//@CrossOrigin(origins = "http://localhost:4200") // si tu utilises Angular
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // CREATE
   @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    //all
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(userService::toDTO)
                .collect(Collectors.toList());
    }


    //by id
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userService.toDTO(user);
    }


    // UPDATE
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User updated) {
        return userService.updateUser(id, updated, false); // CUSTOMER → false temporairement avant jwt
    }


    // DELETE
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}

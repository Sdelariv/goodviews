package be.svend.goodviews.controller;

import be.svend.goodviews.models.User;
import be.svend.goodviews.services.users.UserService;
import be.svend.goodviews.services.users.UserValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    UserService userService;
    UserValidator userValidator;

    public UserController(UserService userService,
                          UserValidator userValidator) {
        this.userService = userService;
        this.userValidator = userValidator;
    }

    // LOGGING IN AND OUT METHODS



    // FIND METHODS

    @GetMapping("/{username}")
    public ResponseEntity findUserByUsername(@PathVariable String username) {
        System.out.println("FIND USER BY USERNAME called for: " + username);

        Optional<User> foundUser = userService.findByUsername(username);

        if (foundUser.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(foundUser.get());
    }

    // CREATE METHODS

    @PostMapping()
    public ResponseEntity addUser(@RequestBody User user) {
        System.out.println("ADDING USER called for:");
        System.out.println(user.toString());

        if (!userValidator.hasValidNewUsername(user)) return ResponseEntity.status(405).body("Username already exists");
        if (!userValidator.hasValidPassword(user)) return ResponseEntity.status(400).body("Invalid password supplied");
        user.setPassword(user.getPasswordHash()); // Let the hashing be done

        Optional<User> savedUser = userService.createNewUser(user);

        if (savedUser.isEmpty()) return ResponseEntity.status(500).body("Was unable to save the new user");

        System.out.println("USER ADDED");
        return ResponseEntity.ok(savedUser.get());
    }

    // UPDATE METHODS



    // DELETE METHODS

    @DeleteMapping("/{username}")
    public ResponseEntity deleteUserByUsername(@PathVariable String username) {
        System.out.println("DELETE USER called for: " + username);

        // See if user exists
        Optional<User> existingUser = userValidator.isExistingUserWithUsername(username);
        if (existingUser.isEmpty()) return ResponseEntity.notFound().build();

        // TODO: check if current user is that user or an admin

        if (!userService.deleteUserByUsername(username)) return ResponseEntity.status(500).body("Was unable to delete user");

        return ResponseEntity.ok("Succesful operation");
    }
}

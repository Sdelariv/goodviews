package be.svend.goodviews.controller;

import be.svend.goodviews.models.User;
import be.svend.goodviews.services.StringValidator;
import be.svend.goodviews.services.users.UserScrubber;
import be.svend.goodviews.services.users.UserService;
import be.svend.goodviews.services.users.UserValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static be.svend.goodviews.services.StringValidator.isValidString;

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

    @CrossOrigin
    @GetMapping("/{username}")
    public ResponseEntity findUserByUsername(@PathVariable String username) {
        System.out.println("FIND USER BY USERNAME called for: " + username);

        if (!isValidString(username)) return ResponseEntity.badRequest().body("Not a valid string input");

        Optional<User> foundUser = userService.findByUsername(username);

        if (foundUser.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(UserScrubber.scrubAllExceptUsername(foundUser.get()));
    }

    @GetMapping("/findAll")
    public ResponseEntity findAllUsers() {
        System.out.println("FIND ALL USERS CALLED");

        List<User> foundUsers = userService.findAllUsers();

        return ResponseEntity.ok(foundUsers);
    }

    @CrossOrigin
    @RequestMapping("checkPassword")
    public ResponseEntity checkPasswordAndUsernameMatch(@RequestBody User user) {
        System.out.println("CHECK PASSWORD AND USERNAME MATCH CALLED FOR " + user.toString());

        user.setPassword(user.getPasswordHash()); // Hashing the password
        String password = user.getPasswordHash();

        Optional<User> foundUser = userService.findByUsername(user.getUsername());
        if (foundUser.isEmpty()) return ResponseEntity.status(404).body("No such user");

        if (!foundUser.get().getPasswordHash().equals(password)) return ResponseEntity.status(401).body("Wrong password");

        // TODO: Save login

        return ResponseEntity.ok().body("Logged in");
    }

    // CREATE METHODS

    @CrossOrigin
    @PostMapping("/add")
    public ResponseEntity addUser(@RequestBody User user) {
        System.out.println("ADDING USER called for: " + user.toString());

        if (!userValidator.hasValidNewUsername(user)) return ResponseEntity.status(405).body("Username already exists");
        if (!userValidator.hasValidPassword(user)) return ResponseEntity.status(400).body("Invalid password supplied");


        Optional<User> savedUser = userService.createNewUser(user);

        if (savedUser.isEmpty()) return ResponseEntity.status(500).body("Was unable to save the new user");

        System.out.println("USER ADDED");
        return ResponseEntity.ok(savedUser.get());
    }

    // UPDATE METHODS TODO: Decide whether to have a general one or update each individual element

    @PostMapping("/update")
    public ResponseEntity updateUserGenerally(User user) {
        System.out.println("UPDATE USER GENERALLY CALLED for " + user);

        Optional<User> existingUser = userValidator.isExistingUser(user);
        if (existingUser.isEmpty()) return ResponseEntity.notFound().build();

        // TODO: check if current user is that user or an admin

        Optional<User> replacedUser = userService.updateUserByAdding(existingUser.get(), user);
        if (replacedUser.isEmpty()) return ResponseEntity.status(500).body("Something went wrong updating");

        return ResponseEntity.ok(replacedUser.get());
    }


    // DELETE METHODS

    @DeleteMapping("/{username}")
    public ResponseEntity deleteUserByUsername(@PathVariable String username) {
        System.out.println("DELETE USER called for: " + username);

        // See if user exists
        Optional<User> existingUser = userValidator.isExistingUserWithUsername(username);
        if (existingUser.isEmpty()) return ResponseEntity.notFound().build();

        // TODO: check if current user is that user or an admin

        if (!userService.deleteUser(existingUser.get())) return ResponseEntity.status(500).body("Something went wrong trying to delete the user.");

        return ResponseEntity.ok("User deleted");
    }
}

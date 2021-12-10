package be.svend.goodviews.services;

import be.svend.goodviews.models.User;
import be.svend.goodviews.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserService {
    UserRepository userRepo;
    UserValidator userValidator;

    public UserService(UserRepository userRepo, UserValidator userValidator) {
        this.userRepo = userRepo;
        this.userValidator = userValidator;
    }

    // CREATE METHODS

    public Optional<User> createUser(User user) {

        if (!userValidator.validNewId(user)) {
            System.out.println("Can't create user. Invalid id present");
            return Optional.empty();
        }

        if (!userValidator.validNewUsername(user)) {
            System.out.println("Can't create user. Username already exists.");
            return Optional.empty();
        }

        if (!userValidator.validPassword(user)) {
            System.out.println("Can't create user. No valid password.");
            return Optional.empty();
        }

        userRepo.save(user);
        System.out.println("Created new user: " + user.getUsername());

        return Optional.of(user);
    }
}

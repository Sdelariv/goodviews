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

    private Optional<User> saveUser(User user) {
        userRepo.save(user);
        System.out.println("Saving " + user.getUsername());
        return userRepo.findByUsername(user.getUsername());
    }

    public Optional<User> createNewUser(User user) {

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

        Optional<User> createdUser = saveUser(user);
        if (createdUser.isPresent()) System.out.println("Created " + createdUser.get().getUsername());
        return createdUser;
    }
}

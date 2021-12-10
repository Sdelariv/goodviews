package be.svend.goodviews.services;

import be.svend.goodviews.models.User;
import be.svend.goodviews.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class UserService {
    UserRepository userRepo;
    UserValidator userValidator;

    public UserService(UserRepository userRepo, UserValidator userValidator) {
        this.userRepo = userRepo;
        this.userValidator = userValidator;
    }

    // FIND METHODS

    public Optional<User> findByUsername(String username) {
        Optional<User> foundUser = userRepo.findByUsername(username);

        if (foundUser.isPresent()) System.out.println("Found user: " + foundUser.get().getUsername());

        return foundUser;
    }

    public Optional<User> findById(Long id) {
        Optional<User> foundUser = userRepo.findById(id);

        if (foundUser.isPresent()) System.out.println("Found user: " + foundUser.get().getUsername());

        return foundUser;
    }

    public Optional<User> findByUserObject(User user) {
        return findByUsername(user.getUsername());
    }

    // CREATE METHODS

    private Optional<User> saveUser(User user) {
        userRepo.save(user);
        System.out.println("Saving " + user.getUsername());
        return userRepo.findByUsername(user.getUsername());
    }

    public Optional<User> createNewUser(User user) {
        System.out.println("Trying to create a new user");

        if (!userValidator.isValidNewUser(user)) return Optional.empty();

        Optional<User> createdUser = saveUser(user);
        if (createdUser.isPresent()) System.out.println("Created " + createdUser.get().getUsername());
        else System.out.println("Couldn't create new user");

        return createdUser;
    }

    public List<User> createNewUsers(List<User> users) {
        List<User> createdUsers = new ArrayList<>();

        for (User user: users) {
            Optional<User> createdUser = createNewUser(user);
            if (createdUser.isPresent()) createdUsers.add(createdUser.get());
        }

        return createdUsers;
    }

    //
}

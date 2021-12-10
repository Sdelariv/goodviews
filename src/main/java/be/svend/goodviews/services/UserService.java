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

    /**
     * Uses the username in the object to find the user in the db and returns it if present
     * @param user user of which the username will be used to search
     * @return Optional<User>
     */
    public Optional<User> findByUserObject(User user) {
        return findByUsername(user.getUsername());
    }

    public List<User> findAllUsers() {
        return userRepo.findAll();
    }

    // CREATE METHODS

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

    /**
     * This method is intended for recreating users that got lost in the db but saved elsewhere
     * @param user
     * @return
     */
    public User recreateOldUser(User user) {
        User initialisedUser = initialise(user);

        userRepo.save(initialisedUser);
        return initialisedUser;
    }


    // UPDATE METHODS

    public Optional<User> updateUser(User user) {

        Optional<User> existingUser = userValidator.isExistingUser(user);
        if (existingUser.isEmpty()) return Optional.empty();

        userRepo.save(existingUser.get());

        return existingUser;
    }

    // DELETE METHODS

    public boolean deleteUser(User user) {
        // See if user exists
        Optional<User> existingUser = userValidator.isExistingUser(user);
        if (existingUser.isEmpty()) return false;

        // Delete and check whether it worked
        userRepo.delete(existingUser.get());
        if (findById(user.getId()).isEmpty()) {
            System.out.println(user.getUsername() + " succesfully deleted");
            return true;
        }
        System.out.println("Something went wrong while deleting");
        return true;
    }

    public void deleteUsers(List<User> users) {
        for (User user: users) {
            deleteUser(user);
        }
    }

    // INTERNAL METHODS

    private User initialise(User user) {

        // TODO: fill in with initialising of Rating

        return user;
    }

    private Optional<User> saveUser(User user) {
        userRepo.save(user);
        System.out.println("Saving " + user.getUsername());
        return userRepo.findByUsername(user.getUsername());
    }

}

package be.svend.goodviews.services;

import be.svend.goodviews.models.Admin;
import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.User;
import be.svend.goodviews.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static be.svend.goodviews.services.UserMerger.mergeUserWithNewData;


@Service
public class UserService {
    UserRepository userRepo;
    UserValidator userValidator;
    RatingService ratingService;

    public UserService(UserRepository userRepo, UserValidator userValidator, RatingService ratingService) {
        this.userRepo = userRepo;
        this.userValidator = userValidator;
        this.ratingService = ratingService;
    }

    // FIND METHODS

    public Optional<User> findByUsername(String username) {
        Optional<User> foundUser = userRepo.findByUsername(username);

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

    // UPDATE METHODS

    public Optional<User> updateUserByAdding(User user) {

        Optional<User> existingUser = userValidator.isExistingUser(user);
        if (existingUser.isEmpty()) return Optional.empty();

        Optional<User> userToUpdate = mergeUserWithNewData(existingUser.get(), user);
        if (userToUpdate.isEmpty()) return Optional.empty();

        return saveUser(userToUpdate.get());
    }

    public Optional<User> updateUserByReplacing(User user) {
        Optional<User> existingUser = userValidator.isExistingUser(user);
        if (existingUser.isEmpty()) return Optional.empty();

        return saveUser(existingUser.get());
    }

    // TODO: clean up this method
    public Optional<User> changeUsername(User user, String newUsername) {
        Optional<User> existingUser = userValidator.isExistingUser(user);
        if (existingUser.isEmpty()) return Optional.empty();

        if (findByUsername(newUsername).isPresent()) {
            System.out.println("Username already exists");
            return Optional.empty();
        }

        // Creating new user object
        User newUser = makeCopyOf(existingUser.get());
        newUser.setUsername(newUsername);
        if (saveUser(newUser).isEmpty()) return Optional.empty();

        // Fetching old ratings, deleting them and saving new ones
        List<Rating> ratings = ratingService.findByUsername(user.getUsername());
        List<String> ratingIdsToDelete = ratings.stream().map(r -> r.getId()).collect(Collectors.toList());

        for (Rating newRating: ratings) {
            newRating.setUser(newUser);
            newRating.updateId();
        }

        ratingService.deleteRatingsById(ratingIdsToDelete);
        deleteUser(existingUser.get());

        ratingService.createNewRatings(ratings);

        return Optional.of(newUser);
    }

    public Optional<Admin> upgradeUserToAdmin(User user) {
        // Look for user
        Optional<User> foundUser = findByUsername(user.getUsername());
        if (foundUser.isEmpty()) return Optional.empty();

        // Create admin
        Admin createdAdmin = createAdmin(user);
        if (findAdmin(createdAdmin)) deleteUser(user);

        return Optional.of(createdAdmin);
    }

    // DELETE METHODS

    public boolean deleteUser(User user) {
        // See if user exists
        Optional<User> existingUser = userValidator.isExistingUser(user);
        if (existingUser.isEmpty()) return false;

        // Delete and check whether it worked
        userRepo.delete(existingUser.get());
        if (findByUsername(user.getUsername()).isEmpty()) {
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

    private Optional<User> saveUser(User user) {
        userRepo.save(user);
        System.out.println("Saving " + user.getUsername());
        return userRepo.findByUsername(user.getUsername());
    }


    private User makeCopyOf(User existingUser) {
        User newUser = new User();
        newUser.setProfileUrl(existingUser.getProfileUrl());
        newUser.setFirstName(existingUser.getFirstName());
        newUser.setLastName(existingUser.getLastName());
        newUser.setUsername(existingUser.getUsername());

        return newUser;
    }

}

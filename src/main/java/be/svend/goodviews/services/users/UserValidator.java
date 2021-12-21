package be.svend.goodviews.services.users;

import be.svend.goodviews.models.User;
import be.svend.goodviews.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserValidator {
    UserRepository userRepo;

    public UserValidator(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public boolean isValidNewUser(User user) {
        if (user == null) return false;

        if (!hasValidNewUsername(user)) {
            System.out.println("Username already exists.");
            return false;
        }

        if (!hasValidPassword(user)) {
            System.out.println("No valid password.");
            return false;
        }

        return true;
    }

    public boolean hasValidNewUsername(User user) {
        if (user == null) return false;

        if (user.getUsername() == null) return false;

        if (user.getUsername().equals("findAll")) return false;

        if (userRepo.findByUsername(user.getUsername()).isPresent()) return false;

        return true;
    }

    private boolean hasInvalidCharacter(String username) {
        List<String> invalidCharacters = new ArrayList<>(
                List.of(";","\\"));

        for (String invalidCharacter: invalidCharacters) {
            if (username.contains(invalidCharacter)) return true;
        }

        return false;
    }


    public boolean hasValidPassword(User user) {
        if (user == null) return false;

        if (user.getPasswordHash() == null) return false;

        if (!isValidPassword(user.getPasswordHash())) return false;

        return true;
    }

    public boolean isValidPassword(String password) {
        if (password == null) return false;

        if (hasInvalidCharacter(password)) return false;

        return true;
    }

    /**
     * Checks whether the user is null and found in the db, based on its username
     * @param user
     * @return Optional<User> - returns the user (optional) if found, empty (optional) if not
     */
    public Optional<User> isExistingUser(User user) {
        if (user == null) return Optional.empty();
        if (user.getUsername() == null) return Optional.empty();

        Optional<User> existingUser = userRepo.findByUsername(user.getUsername());
        if (existingUser.isEmpty()) return Optional.empty();

        return existingUser;
    }

    /**
     * Checks whether the user is null and found in the db, based on its username
     * @param username
     * @return Optional<User> - returns the user (optional) if found, empty (optional) if not
     */
    public Optional<User> isExistingUserWithUsername(String username) {
        if (username == null) return Optional.empty();

        Optional<User> existingUser = userRepo.findByUsername(username);
        if (existingUser.isEmpty()) return Optional.empty();

        return existingUser;
    }


}

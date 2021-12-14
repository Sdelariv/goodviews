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

        if (hasInvalidCharacter(user.getUsername())) return false;

        return true;
    }

    public Optional<User> isExistingUser(User user) {
        Optional<User> existingUser = userRepo.findByUsername(user.getUsername());
        if (existingUser.isEmpty()) return Optional.empty();

        return existingUser;
    }

}
package be.svend.goodviews.services;

import be.svend.goodviews.models.User;
import be.svend.goodviews.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserValidator {
    UserRepository userRepo;

    public UserValidator(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public boolean isValidNewUser(User user) {
        if (!hasValidNewId(user)) {
            System.out.println("Invalid id present");
            return false;
        }

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


    public boolean hasValidNewId(User user) {
        if (user == null) return false;

        if (user.getId() == null) return true;

        return false;
    }

    public boolean hasValidNewUsername(User user) {
        if (user == null) return false;

        if (user.getUsername() == null) return false;

        if (userRepo.findByUsername(user.getUsername()).isPresent()) return false;

        return true;
    }


    public boolean hasValidPassword(User user) {
        if (user == null) return false;

        // TODO: fill in

        return true;
    }
}

package be.svend.goodviews.services;

import be.svend.goodviews.models.User;
import be.svend.goodviews.repositories.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {
    UserRepository userRepo;

    public UserValidator(UserRepository userRepo) {
        this.userRepo = userRepo;
    }


    public boolean validNewId(User user) {
        if (user == null) return false;

        if (user.getId() == null) return true;

        return false;
    }

    public boolean validNewUsername(User user) {
        if (user == null) return false;

        if (user.getUsername() == null) return false;

        if (userRepo.findByUsername(user.getUsername()).isPresent()) return false;

        return true;
    }


    public boolean validPassword(User user) {
        if (user == null) return false;

        // TODO: fill in

        return true;
    }
}

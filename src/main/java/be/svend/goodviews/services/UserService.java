package be.svend.goodviews.services;

import be.svend.goodviews.models.User;
import be.svend.goodviews.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // CREATE METHODS

    public Optional<User> createUser(User user) {

        if (!validAsNew(user)) return Optional.empty();
        userRepo.save(user);

        return Optional.of(user);
    }
}

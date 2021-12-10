package be.svend.goodviews.services;

import be.svend.goodviews.models.User;
import be.svend.goodviews.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }


}

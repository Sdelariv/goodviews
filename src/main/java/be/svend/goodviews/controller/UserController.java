package be.svend.goodviews.controller;

import be.svend.goodviews.services.users.UserService;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
}

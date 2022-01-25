package be.svend.goodviews.services.users;

import be.svend.goodviews.models.User;

public class UserScrubber {

    public static User scrubAllExceptUsername(User user) {
        if (user == null) return null;

        User newUser = new User();
        newUser.setUsername(user.getUsername());

        return newUser;
    }
}

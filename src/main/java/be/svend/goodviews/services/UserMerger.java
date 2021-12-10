package be.svend.goodviews.services;

import be.svend.goodviews.models.User;

import java.util.Optional;

public class UserMerger {

    public static Optional<User> mergeUserWithNewData(User existingUser, User user) {
        User mergedUser = existingUser;

        if (user.getFirstName() != null) existingUser.setFirstName(user.getFirstName());
        if (user.getLastName() != null) existingUser.setLastName(user.getLastName());
        if (user.getProfileUrl() != null) existingUser.setProfileUrl(user.getProfileUrl());

        return Optional.of(mergedUser);
    }


}

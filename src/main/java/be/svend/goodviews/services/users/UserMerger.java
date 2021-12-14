package be.svend.goodviews.services.users;

import be.svend.goodviews.models.User;

import java.util.Optional;

public class UserMerger {

    public static Optional<User> mergeUserWithNewData(User existingUser, User user) {
        User mergedUser = existingUser;

        if (user.getFirstName() != null) mergedUser.setFirstName(user.getFirstName());
        if (user.getLastName() != null) mergedUser.setLastName(user.getLastName());
        if (user.getProfileUrl() != null) mergedUser.setProfileUrl(user.getProfileUrl());
        if (user.getProfileUrl() != null) mergedUser.setPasswordHash(user.getPasswordHash());
        if (user.getTypeOfUser() != null) mergedUser.setTypeOfUser(user.getTypeOfUser());
        // TODO: add friends when you've added it

        return Optional.of(mergedUser);
    }


}

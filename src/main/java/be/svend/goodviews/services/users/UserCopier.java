package be.svend.goodviews.services.users;

import be.svend.goodviews.models.User;

import java.util.Optional;

public class UserCopier {

    public static User makeExactCopyOfUser(User existingUser) {
        User newUser = new User();
        newUser.setUsername(existingUser.getUsername());
        newUser.setProfileUrl(existingUser.getProfileUrl());
        newUser.setFirstName(existingUser.getFirstName());
        newUser.setLastName(existingUser.getLastName());
        newUser.setUsername(existingUser.getUsername());
        newUser.setPasswordHash(existingUser.getPasswordHash());
        newUser.setTypeOfUser(existingUser.getTypeOfUser());

        return newUser;
    }

    public static Optional<User> mergeUserWithNewData(User existingUser, User user) {
        User mergedUser = existingUser;

        if (user.getFirstName() != null) mergedUser.setFirstName(user.getFirstName());
        if (user.getLastName() != null) mergedUser.setLastName(user.getLastName());
        if (user.getProfileUrl() != null) mergedUser.setProfileUrl(user.getProfileUrl());
        if (user.getPasswordHash() != null) mergedUser.setPasswordHash(user.getPasswordHash());
        if (user.getTypeOfUser() != null) mergedUser.setTypeOfUser(user.getTypeOfUser());

        return Optional.of(mergedUser);
    }


}

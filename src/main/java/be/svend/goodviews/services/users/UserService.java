package be.svend.goodviews.services.users;

import be.svend.goodviews.models.*;
import be.svend.goodviews.repositories.UserRepository;
import be.svend.goodviews.services.comment.CommentService;
import be.svend.goodviews.services.notification.NotificationService;
import be.svend.goodviews.services.rating.RatingService;
import be.svend.goodviews.services.rating.WantToSeeService;
import be.svend.goodviews.services.update.LogUpdateService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static be.svend.goodviews.services.StringValidator.isValidString;
import static be.svend.goodviews.services.users.UserCopier.mergeUserWithNewData;


@Service
public class UserService {
    UserRepository userRepo;
    UserValidator userValidator;

    LogUpdateService logUpdateService; // Used to delete the log when deleting user
    NotificationService notificationService; // Used to change the notifications when deleting user
    FriendshipService friendshipService; // Used to delete the user or change the username from existing friendships
    RatingService ratingService; // Need RatingService to migrate the ratings of a username change
    CommentService commentService; // Need CommentService to delete username from comments of a deleted user
    WantToSeeService wantToSeeService; // Needed to delete the user's want to see list

    // CONSTRUCTORS

    public UserService(UserRepository userRepo,
                       UserValidator userValidator,
                       RatingService ratingService,
                       CommentService commentService,
                       FriendshipService friendshipService,
                       NotificationService notificationService,
                       LogUpdateService logUpdateService,
                       WantToSeeService wantToSeeService) {
        this.userRepo = userRepo;
        this.userValidator = userValidator;
        this.ratingService = ratingService;
        this.commentService = commentService;
        this.friendshipService = friendshipService;
        this.notificationService = notificationService;
        this.logUpdateService = logUpdateService;
        this.wantToSeeService = wantToSeeService;
    }

    // FIND METHODS

    public Optional<User> findByUsername(String username) {
        Optional<User> foundUser = userRepo.findByUsername(username);

        if (foundUser.isPresent()) System.out.println("Found user: " + foundUser.get().getUsername());

        return foundUser;
    }

    public List<User> findAllUsers() {
        return userRepo.findAll();
    }

    public List<User> findAllAdmins() {
        return userRepo.findByTypeOfUser(TypeOfUser.ADMIN);
    }

    public List<User> findAllRegularUsers() {
        return userRepo.findByTypeOfUser(TypeOfUser.USER);
    }

    public List<User> findAllArchitects() {
        return userRepo.findByTypeOfUser(TypeOfUser.ARCHITECT);
    }


    // CREATE METHODS

    public Optional<User> createNewUser(User user) {
        if (user.getTypeOfUser() == null) user.setTypeOfUser(TypeOfUser.USER);

        user.setPassword(user.getPasswordHash()); // Let the hashing be done

        Optional<User> createdUser = saveUser(user);
        if (createdUser.isEmpty()) {
            System.out.println("Couldn't create new user");
            return Optional.empty();
        }

        logUpdateService.createGeneralLog("Created user: " + createdUser.get().getUsername());

        System.out.println("Created " + createdUser.get().getUsername());
        return createdUser;
    }

    public List<User> createNewUsers(List<User> users) {
        List<User> createdUsers = new ArrayList<>();

        for (User user: users) {
            Optional<User> createdUser = createNewUser(user);
            if (createdUser.isPresent()) createdUsers.add(createdUser.get());
        }

        return createdUsers;
    }

    // UPDATE METHODS

    public Optional<User> updateUserByAdding(User existingUser, User newUser) {

        Optional<User> userToUpdate = mergeUserWithNewData(existingUser, newUser);
        if (userToUpdate.isEmpty()) return Optional.empty();

        return saveUser(userToUpdate.get());
    }

    public Optional<User> updateUserByReplacing(User userToReplace) {
        return saveUser(userToReplace);
    }

    public Optional<User> changeUserType(User user, TypeOfUser typeOfUser) {
        Optional<User> userInDb = findByUsername(user.getUsername());
        if (userInDb.isEmpty()) return Optional.empty();

        userInDb.get().setTypeOfUser(typeOfUser);

        return Optional.of(userRepo.save(userInDb.get()));
    }

    public Optional<User> updatePassword(User user, String password) {
        userValidator.isValidPassword(password); // TODO: Move to controller?

        user.setPassword(password);
        return saveUser(user);
    }

    public Optional<User> updateFirstName(User user, String firstName) {
        if (!isValidString(firstName)) return Optional.empty(); // TODO: move to controller?

        user.setFirstName(firstName);
        return saveUser(user);
    }

    public Optional<User> updateLastName(User user, String lastName) {
        if (!isValidString(lastName)) return Optional.empty(); // TODO: move to controller?

        user.setLastName(lastName);
        return saveUser(user);
    }

    public Optional<User> updatePosterUrl(User user, String profileUrl) {
        if (!isValidString(profileUrl)) return Optional.empty(); // TODO: move to controller?

        user.setProfileUrl(profileUrl);
        return saveUser(user);
    }

    public Optional<User> upgradeUserToAdmin(User user) {
        return changeUserType(user,TypeOfUser.ADMIN);
    }

    public Optional<User> upgradeUserToArchitect(User user) {
        return changeUserType(user,TypeOfUser.ARCHITECT);
    }

    public Optional<User> downgradeUserToUser(User user) {
        return changeUserType(user,TypeOfUser.USER);
    }

    // DELETE METHODS

    public boolean deleteUserByUsername(String username) {
        Optional<User> existingUser = userValidator.isExistingUserWithUsername(username);
        if (existingUser.isEmpty()) return false;

        return deleteUser(existingUser.get());
    }

    public boolean deleteUser(User user) {
        // TODO: Move this to controller?
        // Delete their logs
        logUpdateService.deleteUserFromLogByUser(user);
        // Delete their name from their comments
        commentService.deleteUserFromCommentsByUsername(user.getUsername());
        // Delete their ratings (and its comments)
        ratingService.deleteRatingsByUser(user);
        ratingService.removeLikesByUser(user);
        wantToSeeService.deleteByUser(user);
        // Delete their friendships
        friendshipService.deleteFriendshipsByUser(user);
        // Delete their notifications
        notificationService.deleteNotificationsInvolvingUser(user);
        // Delete their likes


        // Delete
        userRepo.delete(user);

        // Check whether it worked
        if (findByUsername(user.getUsername()).isPresent()) {
            System.out.println("Something went wrong deleting");
            return false;
        } else {
            System.out.println(user.getUsername() + " succesfully deleted");
            logUpdateService.createGeneralLog("User deleted: " + user.getUsername());
            return true;
        }
    }

    public void deleteUsers(List<User> users) {
        for (User user: users) {
            deleteUser(user);
        }
    }

    // INTERNAL METHODS

    private Optional<User> saveUser(User user) {
        userRepo.save(user);
        System.out.println("Saving " + user.getUsername());
        return userRepo.findByUsername(user.getUsername());
    }


}

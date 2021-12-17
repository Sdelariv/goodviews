package be.svend.goodviews.services.users;

import be.svend.goodviews.models.*;
import be.svend.goodviews.models.notification.Notification;
import be.svend.goodviews.repositories.notification.NotificationRepository;
import be.svend.goodviews.repositories.UserRepository;
import be.svend.goodviews.services.comment.CommentService;
import be.svend.goodviews.services.rating.RatingService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static be.svend.goodviews.services.users.UserMerger.mergeUserWithNewData;


@Service
public class UserService {
    UserRepository userRepo;
    UserValidator userValidator;
    NotificationRepository notificationRepo;

    FriendshipService friendshipService; // Used to delete the user or change the username from existing friendships
    RatingService ratingService; // Need RatingService to migrate the ratings of a username change
    CommentService commentService; // Need CommentService to delete username from comments of a deleted user

    // CONSTRUCTORS

    public UserService(UserRepository userRepo,
                       UserValidator userValidator,
                       RatingService ratingService,
                       CommentService commentService,
                       FriendshipService friendshipService,
                       NotificationRepository notificationRepo) {
        this.userRepo = userRepo;
        this.userValidator = userValidator;
        this.ratingService = ratingService;
        this.commentService = commentService;
        this.friendshipService = friendshipService;
        this.notificationRepo = notificationRepo;
    }

    // FIND METHODS

    public Optional<User> findByUsername(String username) {
        Optional<User> foundUser = userRepo.findByUsername(username);

        if (foundUser.isPresent()) System.out.println("Found user: " + foundUser.get().getUsername());

        return foundUser;
    }

    /**
     * Uses the username in the object to find the user in the db and returns it if present
     * @param user user of which the username will be used to search
     * @return Optional<User>
     */
    public Optional<User> findByUserObject(User user) {
        return findByUsername(user.getUsername());
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
        System.out.println("Trying to create a new user");

        if (!userValidator.isValidNewUser(user)) return Optional.empty();

        if (user.getTypeOfUser() == null) user.setTypeOfUser(TypeOfUser.USER);
        // TODO: hash the password?

        Optional<User> createdUser = saveUser(user);
        if (createdUser.isPresent()) System.out.println("Created " + createdUser.get().getUsername());
        else System.out.println("Couldn't create new user");

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

    public Optional<User> updateUserByAdding(User user) {

        Optional<User> existingUser = userValidator.isExistingUser(user);
        if (existingUser.isEmpty()) return Optional.empty();

        Optional<User> userToUpdate = mergeUserWithNewData(existingUser.get(), user);
        if (userToUpdate.isEmpty()) return Optional.empty();

        return saveUser(userToUpdate.get());
    }

    public Optional<User> updateUserByReplacing(User user) {
        Optional<User> existingUser = userValidator.isExistingUser(user);
        if (existingUser.isEmpty()) return Optional.empty();

        return saveUser(existingUser.get());
    }

    // TODO: Clean up and fix
    public Optional<User> changeUsername(User user, String newUsername) {
        // Check if new username exists already
        if (findByUsername(newUsername).isPresent()) {
            System.out.println("Username already exists");
            return Optional.empty();
        }

        // Find user to update
        Optional<User> existingUser = userValidator.isExistingUser(user);
        if (existingUser.isEmpty()) return Optional.empty();

        // Creating and saving new user object
        User newUser = makeCopyOf(existingUser.get());
        newUser.setUsername(newUsername);
        if (saveUser(newUser).isEmpty()) return Optional.empty();

        // Changing friendships // TODO: make this work
        List<Friendship> friendships = friendshipService.findAllFriendshipsAndRequestsByUser(existingUser.get());
        for (Friendship friendship: friendships) {
            if (friendship.getFriendA().equals(existingUser.get().getUsername())) friendship.setFriendA(newUser);
            if (friendship.getFriendB().equals(existingUser.get().getUsername())) friendship.setFriendB(newUser);
            friendshipService.updateFriendship(friendship);
            System.out.println("Friendship updated");
        }

        // Fetching old ratings, fetching their ids (to delete later), and updating them to the new User
        // TODO: make this work
        List<Rating> ratings = ratingService.findByUsername(user.getUsername());
        List<String> ratingIdsToDelete = ratings.stream().map(r -> r.getId()).collect(Collectors.toList());

        for (Rating newRating: ratings) {
            newRating.setUser(newUser);
            newRating.updateId();
        }

        // Changing id in comments
        // TODO: make this work
        List<Comment> comments = commentService.findByUsername(user.getUsername());
        for (Comment comment: comments) {
            comment.setUser(newUser);
        }

        // Transport notifications
        // TODO: make this work

        // Deleting the old ratings & saving the new ones
        ratingService.deleteRatingsById(ratingIdsToDelete);
        deleteUser(existingUser.get());

        ratingService.createNewRatings(ratings);

        return Optional.of(newUser);
    }

    public Optional<User> changeUserType(User user, TypeOfUser typeOfUser) {
        Optional<User> userInDb = findByUsername(user.getUsername());
        if (userInDb.isEmpty()) return Optional.empty();

        userInDb.get().setTypeOfUser(typeOfUser);

        return Optional.of(userRepo.save(userInDb.get()));
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

    /*
    public Optional<User> addFriend(User user, User friend) {
        Optional<User> userInDb = findByUsername(user.getUsername());
        if (userInDb.isEmpty()) return Optional.empty();
        User userToAddFriend = userInDb.get();

        if (!userToAddFriend.addFriend(friend)) return Optional.empty();

        return saveUser(userToAddFriend);
    }

     */

    // DELETE METHODS

    public boolean deleteUser(User user) {
        // See if user exists
        Optional<User> existingUser = userValidator.isExistingUser(user);
        if (existingUser.isEmpty()) return false;

        // Delete their ratings and their comments (or replace comments with deletedUser)
        commentService.deleteUserFromCommentsByUsername(user.getUsername());

        // Delete their friendships
        List<Friendship> friendships = friendshipService.findAllFriendshipsAndRequestsByUser(existingUser.get());
        for (Friendship friendship: friendships) {
            friendshipService.deleteFriendship(friendship);
        }

        // Delete their notifications
        List<Notification> allNotifications = notificationRepo.findByTargetUser(existingUser.get());
        for (Notification notification: allNotifications) {
            notificationRepo.delete(notification);
        }

        // Delete and check whether it worked
        userRepo.delete(existingUser.get());
        if (findByUsername(user.getUsername()).isEmpty()) {
            System.out.println(user.getUsername() + " succesfully deleted");
            return true;
        }
        System.out.println("Something went wrong while deleting");
        return true;
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


    private User makeCopyOf(User existingUser) {
        User newUser = new User();
        newUser.setProfileUrl(existingUser.getProfileUrl());
        newUser.setFirstName(existingUser.getFirstName());
        newUser.setLastName(existingUser.getLastName());
        newUser.setUsername(existingUser.getUsername());
        newUser.setPasswordHash(existingUser.getPasswordHash());
        newUser.setTypeOfUser(existingUser.getTypeOfUser());

        return newUser;
    }

}

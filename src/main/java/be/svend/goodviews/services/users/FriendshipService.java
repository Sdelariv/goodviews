package be.svend.goodviews.services.users;

import be.svend.goodviews.models.Friendship;
import be.svend.goodviews.models.User;
import be.svend.goodviews.repositories.FriendshipRepository;
import be.svend.goodviews.repositories.UserRepository;
import be.svend.goodviews.services.notification.FriendRequestService;
import be.svend.goodviews.services.notification.NotificationService;
import be.svend.goodviews.services.update.LogUpdateService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class FriendshipService {
    FriendshipRepository friendshipRepo;
    UserRepository userRepo;
    UserValidator userValidator;

    FriendRequestService friendRequestService; // For accepting requests
    LogUpdateService logUpdateService;

    // CONSTRUCTORS

    public FriendshipService(FriendshipRepository friendshipRepo,
                             UserRepository userRepo,
                             UserValidator userValidator,
                             FriendRequestService friendRequestService,
                             LogUpdateService logUpdateService) {
        this.friendshipRepo = friendshipRepo;
        this.userRepo = userRepo;
        this.userValidator = userValidator;
        this.friendRequestService = friendRequestService;
        this.logUpdateService = logUpdateService;
    }

    // FIND METHODS

    /**
     * Finds every kind of Friendship in the db
     * @return List<Friendship> list of all Friendships in the db
     */
    public List<Friendship> findAllFriendshipsAndRequests() {
        return friendshipRepo.findAll();
    }

    /**
     * Finds all accepted Friendships existing in the db
     * @return List<Friendship> list of all accepted Friendships in the db
     */
    public List<Friendship> findAllFriendships() {
        return friendshipRepo.findAllByAcceptedTrue();
    }

    /**
     * Finds all unaccepted Friendships existing in the db
     * @return List<Friendship> list of all unaccepted Friendships in the db
     */
    public List<Friendship> findAllRequests() {
        return friendshipRepo.findAllByAcceptedFalse();
    }

    /**
     * Finds the user in the db based on the username, and then returns all accepted Friendships
     * @param user - user whose friends you are looking for
     * @return List<Friendship> list of all accepted Friendships in the db where the given user is one of the friends
     * */
    public List<Friendship> findAllFriendsByUser(User user) {
        Optional<User> friendInDb = userValidator.isExistingUser(user);
        if (friendInDb.isEmpty()) return Collections.emptyList();

        List<Friendship> friendsOfUser = new ArrayList<>();
        friendsOfUser.addAll(friendshipRepo.findAllByFriendAAndAcceptedTrue(friendInDb.get()));
        friendsOfUser.addAll(friendshipRepo.findAllByFriendBAndAcceptedTrue(friendInDb.get()));

        return friendsOfUser;
    }

    /**
     * Finds the user in the db based on the username, and then returns all Friendships requested by that user
     * @param user - user whose friendrequests you are looking for
     * @return List<Friendship> list of all requested Friendships in the db requested by the user
     * */
    public List<Friendship> findAllFriendRequestsByUser(User user) {
        Optional<User> friendInDb = userValidator.isExistingUser(user);
        if (friendInDb.isEmpty()) return Collections.emptyList();

        List<Friendship> friendRequestsByUser = new ArrayList<>();
        friendRequestsByUser.addAll(friendshipRepo.findAllByFriendAAndAcceptedFalse(friendInDb.get()));

        return friendRequestsByUser;
    }

    /**
     * Finds the user in the db based on the username, and then returns all open friend requests for that user
     * @param user - user whose open requests you are looking for
     * @return List<Friendship> list of all unaccepted friendships in the db for the given user
     * */
    public List<Friendship> findAllFriendRequestsOfUser(User user) {
        Optional<User> friendInDb = userValidator.isExistingUser(user);
        if (friendInDb.isEmpty()) return Collections.emptyList();


        List<Friendship> friendRequestsOfUser = new ArrayList<>();
        friendRequestsOfUser.addAll(friendshipRepo.findAllByFriendBAndAcceptedFalse(friendInDb.get()));

        return friendRequestsOfUser;
    }

    /**
     * Finds all the Friendships for the given user, accepted or not (and receiver or sender of request)
     * @param user - user whose Friendships are requested
     * @return List<Friendship> - A list of all Friendships of that user (be they accepted or not, received or requested)
     */
    public List<Friendship> findAllFriendshipsAndRequestsByUser(User user) {
        List<Friendship> friendships = new ArrayList<>();
        friendships.addAll(findAllFriendsByUser(user));
        friendships.addAll(findAllFriendRequestsByUser(user));
        friendships.addAll(findAllFriendRequestsOfUser(user));

        return friendships;
    }

    // CREATE METHODS

    /**
     * Creates a new pending Friendship (unless the users don't exists, or are the same) and sends a notification to the receiver
     * @param requester - the user requesting the Friendship (will be found based on usernameOfRequested)
     * @param usernameOfRequested - the username of the User whose Friendship is requested
     * @return true if it worked, false if it didn't
     */
    public boolean requestFriendship (User requester, String usernameOfRequested) {
        Optional<User> friendA = userValidator.isExistingUser(requester);
        Optional<User> friendB = userValidator.isExistingUserWithUsername(usernameOfRequested);
        if (friendA.isEmpty() || friendB.isEmpty()) return false;

        if (requester.getUsername().equals(usernameOfRequested)) return false;

        // Create request
        Optional<Friendship> createdFriendship = createRequestedFriendship(friendA.get(),friendB.get());
        if (createdFriendship.isEmpty()) return false;

        // Add notification + Log
        friendRequestService.sendFriendRequestNotification(createdFriendship.get(),friendB.get());
        logUpdateService.createGeneralLog(friendA.get(),friendA.get().getUsername() + " has requested friendship with " + friendB.get().getUsername());

        return true;
    }

    /**
     * Checks whether users exists, and if so - creates a Friendship (without any requests or accepting on the user part,
     * and no notifications to them) and updates it to the db.
     * @param userA
     * @param userB
     * @return Optional<Friendship> = returns a optional of a created Friendship, or empty Optional if users didn't exist,
     * are the same, or was otherwise unable to create the friendship in the db
     */
    public Optional<Friendship> createFriendship(User userA, User userB) {
        Optional<User> friendA = userValidator.isExistingUser(userA);
        Optional<User> friendB = userValidator.isExistingUser(userB);
        if (friendA.isEmpty() || friendB.isEmpty()) return Optional.empty();

        if (friendA.equals(friendB)) return Optional.empty();

        Optional<Friendship> requestedFriendship = createRequestedFriendship(friendA.get(),friendB.get());
        if (requestedFriendship.isEmpty()) return Optional.empty();

        return acceptFriendship(requestedFriendship.get());
    }

    // UPDATE METHODS

    /**
     * Cehcks whether the Friendship is in the db, updates its status and date of acceptance, and notifies the sender
     * that their request was accepted
     * @param friendship - the friendship to be fetched (based on id) and updated
     * @return Optional<Friendship> - Returns the updated friendship (optional) if found, empty (optional) if not
     */
    public Optional<Friendship> acceptFriendship(Friendship friendship) {
        Optional<Friendship> acceptedFriendship = acceptFriendshipWithoutNotification(friendship);
        if (acceptedFriendship.isEmpty()) return Optional.empty();

        // Notification + Log
        friendRequestService.acceptFriendRequest(friendship);
        logUpdateService.createFriendshipUpdate(acceptedFriendship.get());

        return acceptedFriendship;
    }

    /**
     * Checks whether the Friendship is in the db, then sets its acceptance status to true, and updates the time of acceptance
     * @param friendship - the friendship to be fetched (based on id) and updated
     * @return Optional<Friendship> - Returns the updated friendship (optional) if found, empty (optional) if not
     */
    public Optional<Friendship> acceptFriendshipWithoutNotification(Friendship friendship) {
        System.out.println("Friendship accepted");
        Optional<Friendship> friendshipInDb = friendshipRepo.findById(friendship.getId());
        if (friendshipInDb.isEmpty()) return Optional.empty();

        friendshipInDb.get().setAccepted(true);
        friendshipInDb.get().setDateCreated(LocalDate.now());

        // Log
        logUpdateService.createFriendshipUpdate(friendshipInDb.get());

        return saveFriendship(friendshipInDb.get());
    }

    /** Deletes the friendrequest (if found)
     * @param friendship - the Friendship to be denied/deleted
     * @return true if found and succesfully deleted, false if not
     */
    public boolean denyFriendship(Friendship friendship) {
        System.out.println("Friendship denied");

        friendRequestService.deleteFriendRequest(friendship);

        return deleteFriendship(friendship);
    }

    /**
     * Looks for the firendship and updates it found, doesn't if not
     * @param friendship
     * @return Optional<Friendship> returns the updated Friendship (optional) if found, empty (optional) if not
     */
    public Optional<Friendship> updateFriendship(Friendship friendship) {
        Optional<Friendship> friendshipInDb = friendshipRepo.findById(friendship.getId());
        if (friendshipInDb.isEmpty()) return Optional.empty();

        return saveFriendship(friendship);
    }

    // DELETE METHODS

    /**
     * Looks for Friendship in db based on id, and then deletes it from the db
     * @param friendship
     * @return boolean - false if not found, true if found and deleted
     */
    public boolean deleteFriendship(Friendship friendship) {
        Optional<Friendship> friendshipInDb = friendshipRepo.findById(friendship.getId());
        if (friendshipInDb.isEmpty()) return false;

        friendRequestService.deleteNotificationsByFriendship(friendship);

        friendshipRepo.delete(friendshipInDb.get());
        System.out.println("Friendship deleted");
        return true;
    }

    public boolean deleteFriendship(User user1, User user2) {
        Optional<Friendship> foundFriendship;

        foundFriendship = friendshipRepo.findByFriendAAndFriendB(user1, user2);
        if (foundFriendship.isPresent()) return deleteFriendship(foundFriendship.get());

        foundFriendship = friendshipRepo.findByFriendAAndFriendB(user2, user1);
        if (foundFriendship.isPresent()) return deleteFriendship(foundFriendship.get());

        return false;
    }

    // INTERNAL METHODS

    /**
     * Private method that saves the friendship and returns the saved object
     * @param friendship
     * @return Optional<Friendship> returns the friendship object from the db (based on the id of the object that needed saving)
     */
    private Optional<Friendship> saveFriendship(Friendship friendship) {
        System.out.println("Saving friendship: " + friendship);
        Friendship savedFriendship = friendshipRepo.save(friendship);
        return friendshipRepo.findById(friendship.getId());
    }

    /**
     * Checks to see whether the User objects aren't null, equal or a friendship already exists
     * @param friendA
     * @param friendB
     * @return boolean - true if valid new friendship, false if one is null, the same or the friendship already exists
     */
    private boolean isValidNewFriendship(User friendA, User friendB) {
        if (friendA == null || friendB == null) return false;

        if (friendA.equals(friendB)) {
            System.out.println("Can't be friends with oneself");
            return false;
        }
        if (friendshipRepo.existsByFriendAAndFriendB(friendA,friendB)) {
            System.out.println("Friendship already exists");
            return false;
        }
        if (friendshipRepo.existsByFriendAAndFriendB(friendB,friendA)) {
            System.out.println("Friendship already exists");
            return false;
        }
        return true;
    }

    /**
     * Private method to create a new Friendship object of the two users (presumed to be fetched from the db already),
     * unless the friendship already exists
     * @param friendA user to become requester in the Friendship object
     * @param friendB user to become receiver in the Friendship object
     * @return Optional<Friendship> - A new Friendship object (unless the friendship already exists)
     */
    private Optional<Friendship> createRequestedFriendship(User friendA, User friendB) {
        // Check if the Friendship already exists
        if (!isValidNewFriendship(friendA,friendB)) return Optional.empty();

        // Create
        System.out.println("Creating friendship request between " + friendA.getUsername() + " and " + friendB.getUsername());
        Friendship friendship = new Friendship();
        friendship.setFriendA(friendA);
        friendship.setFriendB(friendB);

        return saveFriendship(friendship);
    }

}

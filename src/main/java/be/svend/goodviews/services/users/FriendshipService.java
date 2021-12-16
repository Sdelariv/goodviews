package be.svend.goodviews.services.users;

import be.svend.goodviews.models.Friendship;
import be.svend.goodviews.models.User;
import be.svend.goodviews.repositories.FriendshipRepository;
import be.svend.goodviews.repositories.UserRepository;
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

    // CONSTRUCTORS

    public FriendshipService(FriendshipRepository friendshipRepo,
                             UserRepository userRepo) {
        this.friendshipRepo = friendshipRepo;
        this.userRepo = userRepo;
    }

    // FIND METHODS

    public List<Friendship> findAllFriendshipsAndRequests() {
        return friendshipRepo.findAll();
    }

    public List<Friendship> findAllFriendships() {
        return friendshipRepo.findAllByAcceptedTrue();
    }

    public List<Friendship> findAllRequests() {
        return friendshipRepo.findAllByAcceptedFalse();
    }

    public List<Friendship> findAllFriendsByUser(User user) {
        if (user == null) return Collections.emptyList();
        if (user.getUsername() == null) return Collections.emptyList();

        Optional<User> friendInDb = userRepo.findByUsername(user.getUsername());
        if (friendInDb.isEmpty()) return Collections.emptyList();

        List<Friendship> friendsOfUser = new ArrayList<>();
        friendsOfUser.addAll(friendshipRepo.findAllByFriendAAndAcceptedTrue(friendInDb.get()));
        friendsOfUser.addAll(friendshipRepo.findAllByFriendBAndAcceptedTrue(friendInDb.get()));

        return friendsOfUser;
    }

    public List<Friendship> findAllFriendRequestsByUser(User user) {
        if (user == null) return Collections.emptyList();
        if (user.getUsername() == null) return Collections.emptyList();

        Optional<User> friendInDb = userRepo.findByUsername(user.getUsername());
        if (friendInDb.isEmpty()) return Collections.emptyList();

        List<Friendship> friendRequestsByUser = new ArrayList<>();
        friendRequestsByUser.addAll(friendshipRepo.findAllByFriendAAndAcceptedFalse(friendInDb.get()));

        return friendRequestsByUser;
    }

    public List<Friendship> findAllFriendRequestsOfUser(User user) {
        if (user == null) return Collections.emptyList();
        if (user.getUsername() == null) return Collections.emptyList();

        Optional<User> friendInDb = userRepo.findByUsername(user.getUsername());
        if (friendInDb.isEmpty()) return Collections.emptyList();

        List<Friendship> friendRequestsOfUser = new ArrayList<>();
        friendRequestsOfUser.addAll(friendshipRepo.findAllByFriendBAndAcceptedFalse(friendInDb.get()));

        return friendRequestsOfUser;
    }

    // CREATE METHODS

    public boolean requestFriendship (User userA, String username) {
        Optional<User> requestedFriend = userRepo.findByUsername(username);
        if (requestedFriend.isEmpty()) return false;

        createRequestedFriendship(userA,requestedFriend.get());
        return true;
    }

    public Optional<Friendship> acceptFriendship(Friendship friendship) {
        Optional<Friendship> friendshipInDb = friendshipRepo.findById(friendship.getId());
        if (friendshipInDb.isEmpty()) return Optional.empty();

        friendshipInDb.get().setAccepted(true);
        friendshipInDb.get().setDateCreated(LocalDate.now());
        return saveFriendship(friendshipInDb.get());
    }

    public Optional<Friendship> createRequestedFriendship(User userA, User userB) {
        System.out.println("Creating friendship request between:");
        System.out.println(userA);
        System.out.println("and");
        System.out.println(userB);

        // Check if users exist
        Optional<User> friendA = userRepo.findByUsername(userA.getUsername());
        if (friendA.isEmpty()) return Optional.empty();

        Optional<User> friendB = userRepo.findByUsername(userB.getUsername());
        if (friendB.isEmpty()) return Optional.empty();

        // Check if it already exists
        if (friendshipRepo.existsByFriendAAndFriendB(friendA.get(),friendB.get())) return Optional.empty();
        if (friendshipRepo.existsByFriendAAndFriendB(friendB.get(),friendA.get())) return Optional.empty();

        // Create
        Friendship friendship = new Friendship();
        friendship.setFriendA(friendA.get());
        friendship.setFriendB(friendB.get());

        // Add notification
        // TODO: fill in

        return saveFriendship(friendship);
    }

    public Optional<Friendship> createFriendship(User userA, User userB) {
        Optional<Friendship> requestedFriendship = createRequestedFriendship(userA,userB);
        if (requestedFriendship.isEmpty()) return Optional.empty();

        return acceptFriendship(requestedFriendship.get());
    }

    // INTERNAL METHODS

    private Optional<Friendship> saveFriendship(Friendship friendship) {
        System.out.println("Saving friendship: " + friendship);
        Friendship savedFriendship = friendshipRepo.save(friendship);
        return friendshipRepo.findById(friendship.getId());
    }
}

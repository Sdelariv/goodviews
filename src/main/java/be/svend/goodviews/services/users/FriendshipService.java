package be.svend.goodviews.services.users;

import be.svend.goodviews.models.Friendship;
import be.svend.goodviews.models.User;
import be.svend.goodviews.repositories.FriendshipRepository;
import be.svend.goodviews.repositories.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

public class FriendshipService {
    FriendshipRepository friendshipRepo;
    UserRepository userRepo;

    // CONSTRUCTORS

    public FriendshipService(FriendshipRepository friendshipRepo,
                             UserRepository userRepo) {
        this.friendshipRepo = friendshipRepo;
        this.userRepo = userRepo;
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
        // Check if users exist
        Optional<User> friendA = userRepo.findByUsername(userA.getUsername());
        if (friendA.isEmpty()) return Optional.empty();

        Optional<User> friendB = userRepo.findByUsername(userB.getUsername());
        if (friendB.isEmpty()) return Optional.empty();

        // Check if it already exists
        if (friendshipRepo.existsByFriendAAndFriendB(friendA.get(),friendB.get())) return Optional.empty();

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
        Friendship savedFriendship = friendshipRepo.save(friendship);
        return friendshipRepo.findById(friendship.getId());
    }
}

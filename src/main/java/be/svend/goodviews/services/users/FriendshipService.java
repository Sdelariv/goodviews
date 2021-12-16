package be.svend.goodviews.services.users;

import be.svend.goodviews.models.Friendship;
import be.svend.goodviews.models.User;
import be.svend.goodviews.repositories.FriendshipRepository;
import be.svend.goodviews.repositories.UserRepository;

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

    public void requestFriendship (User userA, String username) {
        // TODO: fill in
    }


    public Optional<Friendship> createFriendship(User userA, User userB) {
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

        return saveFriendship(friendship);
    }

    // INTERNAL METHODS

    private Optional<Friendship> saveFriendship(Friendship friendship) {
        Friendship savedFriendship = friendshipRepo.save(friendship);
        return friendshipRepo.findById(friendship.getId());
    }
}

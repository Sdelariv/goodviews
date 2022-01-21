package be.svend.goodviews.services.users;

import be.svend.goodviews.models.Friendship;
import be.svend.goodviews.models.User;
import be.svend.goodviews.repositories.FriendshipRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class FriendFinder {
    UserValidator userValidator;

    FriendshipRepository friendshipRepo;

    public FriendFinder(UserValidator userValidator, FriendshipRepository friendshipRepo) {
        this.userValidator = userValidator;
        this.friendshipRepo = friendshipRepo;
    }

    public List<User> findAllFriendsByUser(User user) {
        Optional<User> friendInDb = userValidator.isExistingUser(user);
        if (friendInDb.isEmpty()) return Collections.emptyList();

        List<User> friendsOfUser = new ArrayList<>();

        List<Friendship> friendshipsWhereUserIsA = friendshipRepo.findAllByFriendAAndAcceptedTrue(friendInDb.get());
        for (Friendship friendship: friendshipsWhereUserIsA) {
            friendsOfUser.add(friendship.getFriendB());
        }
        List<Friendship> friendshipsWhereUserIsB = friendshipRepo.findAllByFriendBAndAcceptedTrue(friendInDb.get());
        for (Friendship friendship: friendshipsWhereUserIsB) {
            friendsOfUser.add(friendship.getFriendA());
        }

        return friendsOfUser;
    }
}

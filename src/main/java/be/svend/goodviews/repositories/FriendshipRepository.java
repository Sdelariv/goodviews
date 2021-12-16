package be.svend.goodviews.repositories;

import be.svend.goodviews.models.Friendship;
import be.svend.goodviews.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, String> {

    boolean existsByFriendAAndFriendB(User first, User second);

    List<Friendship> findByFriendAOrFriendB(User user);
}

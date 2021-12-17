package be.svend.goodviews.repositories;

import be.svend.goodviews.models.Friendship;
import be.svend.goodviews.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, String> {

    boolean existsByFriendAAndFriendB(User first, User second);

    List<Friendship> findAllByAcceptedTrue();

    List<Friendship> findAllByAcceptedFalse();

    Optional<Friendship> findByFriendAAndFriendB(User friendA, User friendB);

    List<Friendship> findAllByFriendAAndAcceptedTrue(User user);

    List<Friendship> findAllByFriendBAndAcceptedTrue(User user);

    List<Friendship> findAllByFriendAAndAcceptedFalse(User user);

    List<Friendship> findAllByFriendBAndAcceptedFalse(User user);
}

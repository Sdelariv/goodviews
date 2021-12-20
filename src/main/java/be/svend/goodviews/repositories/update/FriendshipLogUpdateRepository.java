package be.svend.goodviews.repositories.update;

import be.svend.goodviews.models.Friendship;
import be.svend.goodviews.models.update.FriendshipLogUpdate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendshipLogUpdateRepository extends JpaRepository<FriendshipLogUpdate, Long> {

    List<FriendshipLogUpdate> findByFriendship(Friendship friendship);
}

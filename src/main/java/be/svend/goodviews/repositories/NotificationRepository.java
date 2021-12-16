package be.svend.goodviews.repositories;

import be.svend.goodviews.models.Friendship;
import be.svend.goodviews.models.TypeOfUser;
import be.svend.goodviews.models.User;
import be.svend.goodviews.models.notification.FriendRequest;
import be.svend.goodviews.models.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

    List<Notification> findAllByTypeOfUser(TypeOfUser type);

    Optional<FriendRequest> findByFriendRequest(Friendship friendship);

    List<Notification> findByUser(User user);

}

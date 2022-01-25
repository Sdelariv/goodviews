package be.svend.goodviews.repositories.notification;

import be.svend.goodviews.models.Friendship;
import be.svend.goodviews.models.TypeOfUser;
import be.svend.goodviews.models.User;
import be.svend.goodviews.models.notification.FriendRequestNotification;
import be.svend.goodviews.models.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

    List<Notification> findAllByTypeOfUser(TypeOfUser type);

    Optional<FriendRequestNotification> findByFriendRequest(Friendship friendship);

    List<Notification> findByTargetUser(User user);

    List<Notification> findByTargetUser_Username(String username);

    List<Notification> findByOriginUser(User user);

    List<Notification> findByOriginUserAndTargetUser(User origin, User target);

    Integer countNotificationsByTargetUser_UsernameAndSeenFalse(String username);

}

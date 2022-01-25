package be.svend.goodviews.repositories.notification;

import be.svend.goodviews.models.User;
import be.svend.goodviews.models.notification.FriendRequestNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRequestNotificationRepository extends JpaRepository<FriendRequestNotification,Long> {

    List<FriendRequestNotification> findByTargetUser(User targetUser);

    Integer countFriendRequestNotificationByTargetUser_UsernameAndSeenFalse(String username);
}

package be.svend.goodviews.repositories.notification;

import be.svend.goodviews.models.User;
import be.svend.goodviews.models.notification.CommentNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentNotificationRepository extends JpaRepository<CommentNotification, Long> {

    List<CommentNotification> findByTargetUser(User targetUser);
}

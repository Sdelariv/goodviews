package be.svend.goodviews.repositories.notification;

import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.User;
import be.svend.goodviews.models.notification.CommentNotification;
import be.svend.goodviews.models.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentNotificationRepository extends JpaRepository<CommentNotification, Long> {

    List<CommentNotification> findByTargetUser(User targetUser);

    List<CommentNotification> findByOriginUser(User commenter);

    List<CommentNotification> findByRating(Rating rating);
}

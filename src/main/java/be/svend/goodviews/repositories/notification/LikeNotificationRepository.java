package be.svend.goodviews.repositories.notification;

import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.User;
import be.svend.goodviews.models.notification.LikeNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeNotificationRepository extends JpaRepository<LikeNotification,Long> {

    Optional<LikeNotification> findByRatingAndOriginUser(Rating rating, User user);

    List<LikeNotification> findByRating(Rating rating);
}

package be.svend.goodviews.services.notification;

import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.User;
import be.svend.goodviews.models.notification.LikeNotification;
import be.svend.goodviews.models.notification.Notification;
import be.svend.goodviews.repositories.notification.LikeNotificationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeNotificationService {
    LikeNotificationRepository likeNotificationRepo;

    public LikeNotificationService(LikeNotificationRepository likeNotificationRepo) {
        this.likeNotificationRepo= likeNotificationRepo;
    }

// CREATE

    public Notification createLikeNotification(User originUser, Rating rating) {
        LikeNotification likeNotification = new LikeNotification();
        likeNotification.setMessage(originUser.getUsername() + " has liked your rating of " + rating.getFilm().getTitle());
        likeNotification.setTargetUser(rating.getUser());
        likeNotification.setOriginUser(originUser);
        likeNotification.setRating(rating);

        likeNotificationRepo.save(likeNotification);
        return likeNotification;
    }

    // DELETE

    public void deleteLikeNotification(User user, Rating rating) {
        Optional<LikeNotification> foundLikeNotification = likeNotificationRepo.findByRatingAndOriginUser(rating, user);

        if (foundLikeNotification.isEmpty()) return;

        likeNotificationRepo.delete(foundLikeNotification.get());
    }
}

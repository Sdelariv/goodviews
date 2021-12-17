package be.svend.goodviews.services.notification;

import be.svend.goodviews.models.Comment;
import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.User;
import be.svend.goodviews.models.notification.CommentNotification;
import be.svend.goodviews.repositories.RatingRepository;
import be.svend.goodviews.repositories.notification.CommentNotificationRepository;
import be.svend.goodviews.repositories.notification.NotificationRepository;
import be.svend.goodviews.services.comment.CommentValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentNotificationService {
    NotificationRepository notificationRepo;
    CommentNotificationRepository commentNotificationRepo;
    CommentValidator commentValidator;

    RatingRepository ratingRepo;

    NotificationService notificationService; // For deleting

    // CONSTRUCTOR

    public CommentNotificationService(CommentNotificationRepository commentNotificationRepo,
                                      CommentValidator commentValidator,
                                      NotificationRepository notificationRepo,
                                      RatingRepository ratingRepo,
                                      NotificationService notificationService) {
        this.commentNotificationRepo = commentNotificationRepo;
        this.commentValidator = commentValidator;
        this.notificationRepo = notificationRepo;
        this.ratingRepo = ratingRepo;
        this.notificationService = notificationService;
    }

    // FIND METHODS

    public List<CommentNotification> findByTargetUser(User targetUser) {
        return commentNotificationRepo.findByTargetUser(targetUser);
    }

    // CREATE METHODS

    public boolean sendCommentNotification(Comment comment) {
        // Check if comment exists
        Optional<Comment> foundComment = commentValidator.isExistingComment(comment);
        if (foundComment.isEmpty()) return false;

        // Create Comment Notification (should work)
        Optional<CommentNotification> commentNotification = createCommentNotification(comment);
        if (commentNotification.isEmpty()) return false;

        // Send(Save)
        notificationRepo.save(commentNotification.get());
        System.out.println("Notified about comment");
        return true;
    }

    // INTERNAL METHODS

    private Optional<CommentNotification> createCommentNotification(Comment comment) {
        CommentNotification commentNotification = new CommentNotification();
        commentNotification.setComment(comment);

        commentNotification.setOriginUser(comment.getUser());

        Optional<Rating> ratingWithComment = ratingRepo.findRatingByCommentListContaining(comment);
        if (ratingWithComment.isEmpty()) {
            System.out.println("Something went wrong fetching the comment");
            return Optional.empty();
        }
        commentNotification.setTargetUser(ratingWithComment.get().getUser());

        return Optional.of(commentNotification);
    }

    // DELETE METHODS

    public void deleteCommentNotification(CommentNotification commentNotification) {
        notificationService.deleteNotification(commentNotification);
    }
}

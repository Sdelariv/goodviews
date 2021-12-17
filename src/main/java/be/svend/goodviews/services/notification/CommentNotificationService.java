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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<CommentNotification> findByCommenter(User commenter) {
        return commentNotificationRepo.findByOriginUser(commenter);
    }

    // CREATE METHODS

    public boolean sendCommentNotification(Comment comment) {
        // Check if comment exists
        Optional<Comment> foundComment = commentValidator.isExistingComment(comment);
        if (foundComment.isEmpty()) return false;

        // Create Comment Notification (should work)
        Optional<CommentNotification> commentNotification = createCommentNotification(comment);
        if (commentNotification.isEmpty()) return false;

        // Create Reply Notifications
        sendReplyCommentNotification(comment);

        // Send(Save)
        notificationRepo.save(commentNotification.get());
        System.out.println("Notified about comment");
        return true;
    }

   private boolean sendReplyCommentNotification(Comment comment) {
        List<CommentNotification> replyCommentNotifications = createReplyNotifications(comment);

        for (CommentNotification replyNotification: replyCommentNotifications) {
            notificationRepo.save(replyNotification);
        }
        return true;
    }

    // UPDATE METHODS

    public void deleteUserFromCommentNotifications(User user) {
        List<CommentNotification> notificationsByUser = findByCommenter(user);
        for (CommentNotification commentNotification:  notificationsByUser) {
            commentNotification.setOriginUser(null);
            notificationRepo.save(commentNotification);
        }

        List<CommentNotification> notificationsOfUser = findByTargetUser(user);

        for (CommentNotification commentNotification: notificationsOfUser ) {
            deleteCommentNotification(commentNotification);
        }
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
        commentNotification.setRating(ratingWithComment.get());
        commentNotification.setTargetUser(ratingWithComment.get().getUser());

        return Optional.of(commentNotification);
    }

    private List<CommentNotification> createReplyNotifications(Comment comment) {
        // Find rating-thread
        Optional<Rating> ratingWithComment = ratingRepo.findRatingByCommentListContaining(comment);
        if (ratingWithComment.isEmpty()) {
            System.out.println("Something went wrong fetching the comment");
            return Collections.emptyList();
        }

        // Make list of ReplyNotifications for people who already commented (not the commenter itself or the thread-owner)
        List<CommentNotification> replyNotifications = new ArrayList<>();
        List<User> usersAlreadyDone = new ArrayList<>();

        for (Comment commentInThread: ratingWithComment.get().getCommentList()) {
            // Check if it's the thread-owner
            if (commentInThread.getUser() == null || ratingWithComment.get().getUser() == null) continue;
            if (commentInThread.getUser().equals(ratingWithComment.get().getUser())) continue;

            // Check if it's the commenter themselves
            if (commentInThread.getUser() == null || comment.getUser() == null) continue;
            if (commentInThread.getUser().equals(comment.getUser())) continue;

            // Check if we already had that user
            if (usersAlreadyDone.contains(commentInThread.getUser())) continue;

            CommentNotification replyNotification = new CommentNotification();
            replyNotification.setOriginUser(commentInThread.getUser());
            replyNotification.setRating(ratingWithComment.get());
            replyNotification.setComment(commentInThread);
            replyNotification.setMessage(comment.getUser().getUsername() + " has replied to a conversation you are in");

            replyNotifications.add(replyNotification);
            usersAlreadyDone.add(commentInThread.getUser());
            System.out.println("Notified about reply");
        }

        return replyNotifications.stream().distinct().collect(Collectors.toList());
    }

    // DELETE METHODS

    public void deleteCommentNotification(CommentNotification commentNotification) {
        notificationService.deleteNotification(commentNotification);
    }


}

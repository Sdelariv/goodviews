package be.svend.goodviews.services.notification;

import be.svend.goodviews.models.Comment;
import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.User;
import be.svend.goodviews.models.notification.CommentNotification;
import be.svend.goodviews.repositories.CommentRepository;
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
    CommentRepository commentRepo;

    RatingRepository ratingRepo;

    NotificationService notificationService; // For deleting

    // CONSTRUCTOR

    public CommentNotificationService(CommentNotificationRepository commentNotificationRepo,
                                      CommentValidator commentValidator,
                                      CommentRepository commentRepo,
                                      NotificationRepository notificationRepo,
                                      RatingRepository ratingRepo,
                                      NotificationService notificationService) {
        this.commentNotificationRepo = commentNotificationRepo;
        this.commentValidator = commentValidator;
        this.notificationRepo = notificationRepo;
        this.ratingRepo = ratingRepo;
        this.notificationService = notificationService;
        this.commentRepo = commentRepo;
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
        // Create Reply Notifications
        sendReplyCommentNotification(comment);

        // Create Comment Notification (should work)
        Optional<CommentNotification> commentNotification = createCommentNotification(comment);
        if (commentNotification.isEmpty()) return false;

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
        if (comment.getUser().equals(comment.getRating().getUser())) return Optional.empty();

        CommentNotification commentNotification = new CommentNotification();

        commentNotification.setComment(comment);
        commentNotification.setRating(comment.getRating());
        commentNotification.setMessage(comment.getUser().getUsername() + " has commented on your rating of " + comment.getRating().getFilm().getTitle());

        commentNotification.setOriginUser(comment.getUser());
        commentNotification.setTargetUser(comment.getRating().getUser());

        return Optional.of(commentNotification);
    }

    private List<CommentNotification> createReplyNotifications(Comment comment) {
        // Find all Commenters of the thread
        List<Comment> ratingComments = commentRepo.findAllByRating(comment.getRating());
        List<User> threadCommenters = ratingComments.stream().map(c -> c.getUser()).distinct().collect(Collectors.toList());
        User threadOwner = comment.getRating().getUser();
        User newCommenter = comment.getUser();

        // Create notification for everyone else
        List<CommentNotification> replyNotifications = new ArrayList<>();
        for (User threadCommenter: threadCommenters) {
            if (threadCommenter.equals(threadOwner)) continue;
            if (threadCommenter.equals(newCommenter)) continue;
                CommentNotification replyNotification = new CommentNotification();
                replyNotification.setOriginUser(comment.getUser());
                replyNotification.setTargetUser(threadCommenter);
                replyNotification.setRating(comment.getRating());
                replyNotification.setComment(comment);
                replyNotification.setMessage(comment.getUser().getUsername() + " has replied to a conversation you are in");

                replyNotifications.add(replyNotification);
                System.out.println("Notified about reply");
        }

        return replyNotifications.stream().distinct().collect(Collectors.toList());
    }

    // DELETE METHODS

    public void deleteCommentNotification(CommentNotification commentNotification) {
        notificationService.deleteNotification(commentNotification);
    }


}

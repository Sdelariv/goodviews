package be.svend.goodviews.services.notification;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.Friendship;
import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.User;
import be.svend.goodviews.models.notification.CommentNotification;
import be.svend.goodviews.models.notification.FriendRequestNotification;
import be.svend.goodviews.models.notification.Notification;

import be.svend.goodviews.repositories.notification.CommentNotificationRepository;
import be.svend.goodviews.repositories.notification.LikeNotificationRepository;
import be.svend.goodviews.repositories.notification.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for general notification-methods
 */
@Service
public class NotificationService {
    NotificationRepository notificationRepo;
    CommentNotificationRepository commentNotificationRepo;
    LikeNotificationRepository likeNotificationRepo;

    // CONSTRUCTORS

    public NotificationService(NotificationRepository notificationRepo,
                               CommentNotificationRepository commentNotificationRepo,
                               LikeNotificationRepository likeNotificationRepo) {
        this.notificationRepo = notificationRepo;
        this.commentNotificationRepo = commentNotificationRepo;
        this.likeNotificationRepo = likeNotificationRepo;

    }

    // FIND METHODS
    public List<Notification> findByTargetUsername(String username) {
        return notificationRepo.findByTargetUser_Username(username);
    }

    public List<Notification> findByTargetUser(User targetUser) {
        return notificationRepo.findByTargetUser(targetUser);
    }

    public List<Notification> findByOriginUser(User originUser) {
        return notificationRepo.findByOriginUser(originUser);
    }


    // CREATE METHODS

    public Notification createGeneralNotification(String message, User originUser, User targetUser) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setTargetUser(targetUser);
        notification.setOriginUser(originUser);

        notificationRepo.save(notification);
        return notification;
    }

    // UPDATE METHODS



    // DELETE

    public boolean deleteNotification(Notification notification) {
        if (notification == null || notification.getId() == null) return false;
        if (notificationRepo.findById(notification.getId()).isEmpty()) return false;

        notificationRepo.delete(notification);
        System.out.println("Notification deleted");
        return true;
    }

    public void deleteNotifications(List<Notification> notifications) {
        for (Notification notification: notifications) {
            deleteNotification(notification);
        }
    }

    public void deleteNotificationsInvolvingUser(User user) {
        List<Notification> allNotificationsOf = findByTargetUser(user);
        for (Notification notification: allNotificationsOf) {
            deleteNotification(notification);
        }
        List<Notification> allNotificationsBy = findByOriginUser(user);
        for (Notification notification: allNotificationsBy) {
            removeOriginUserFromNotification(notification);
        }
    }


    public void deleteNotificationsByRating(Rating rating) {
        List<Notification> allNotifications = new ArrayList<>();
        allNotifications.addAll(commentNotificationRepo.findByRating(rating));
        allNotifications.addAll(likeNotificationRepo.findByRating(rating));
        deleteNotifications(allNotifications);
    }


    public void removeOriginUserFromNotification(Notification notification) {
        notification.setOriginUser(null);
        notificationRepo.save(notification);
    }
}

package be.svend.goodviews.services.notification;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.Friendship;
import be.svend.goodviews.models.User;
import be.svend.goodviews.models.notification.FriendRequestNotification;
import be.svend.goodviews.models.notification.Notification;

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

    // CONSTRUCTORS

    public NotificationService(NotificationRepository notificationRepo) {
        this.notificationRepo = notificationRepo;

    }

    // FIND METHODS

    public List<Notification> findByTargetUser(User targetUser) {
        return notificationRepo.findByTargetUser(targetUser);
    }

    public List<Notification> findByOriginUser(User originUser) {
        return notificationRepo.findByOriginUser(originUser);
    }


    // CREATE METHODS



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
        List<Notification> allNotifications = new ArrayList<>();
        allNotifications.addAll(notificationRepo.findByTargetUser(user));
        allNotifications.addAll(notificationRepo.findByOriginUser(user));

        deleteNotifications(allNotifications);
    }


    public void removeOriginUserFromNotification(Notification notification) {
        notification.setOriginUser(null);
        notificationRepo.save(notification);
    }
}

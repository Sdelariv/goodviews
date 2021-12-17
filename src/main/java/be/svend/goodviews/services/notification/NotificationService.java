package be.svend.goodviews.services.notification;

import be.svend.goodviews.models.Friendship;
import be.svend.goodviews.models.User;
import be.svend.goodviews.models.notification.FriendRequestNotification;
import be.svend.goodviews.models.notification.Notification;

import be.svend.goodviews.repositories.notification.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    NotificationRepository notificationRepo;


    // CONSTRUCTORS

    public NotificationService(NotificationRepository notificationRepo) {
        this.notificationRepo = notificationRepo;

    }

    // FIND METHODS

    public Optional<FriendRequestNotification> findFriendRequestNotificationByFriendship(Friendship friendship) {
        return notificationRepo.findByFriendRequest(friendship);
    }


    // CREATE METHODS

    public void createFriendRequestNotification(Friendship friendship, User userToNotify) {
        FriendRequestNotification friendRequestNotification = new FriendRequestNotification();
        friendRequestNotification.setTargetUser(userToNotify);
        friendRequestNotification.setFriendRequest(friendship);
        notificationRepo.save(friendRequestNotification);

        System.out.println("Friend request sent");
    }

    // UPDATE METHODS

    public void acceptFriendRequest(Friendship friendship) {
        Notification acceptedFriendNotification = new Notification();
        acceptedFriendNotification.setTargetUser(friendship.getFriendA());
        acceptedFriendNotification.setOriginUser(friendship.getFriendB());
        acceptedFriendNotification.setMessage(friendship.getFriendB().getUsername() + " has accepted your friendrequest");
        notificationRepo.save(acceptedFriendNotification);

        System.out.println("Friend request accepted");

        deleteFriendRequest(friendship);
    }

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

    public void deleteNotificationsByFriendship(Friendship friendship) {
        List<Notification> allNotifications = new ArrayList<>();
        allNotifications.addAll(notificationRepo.findByOriginUserAndTargetUser(friendship.getFriendA(), friendship.getFriendB()));
        allNotifications.addAll(notificationRepo.findByOriginUserAndTargetUser(friendship.getFriendB(), friendship.getFriendA()));

        deleteNotifications(allNotifications);
    }

    public void deleteFriendRequest(Friendship friendship) {
        Optional<FriendRequestNotification> request = findFriendRequestNotificationByFriendship(friendship);
        if (request.isPresent()) notificationRepo.delete(request.get());
    }

}

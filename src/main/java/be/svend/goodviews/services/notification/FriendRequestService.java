package be.svend.goodviews.services.notification;

import be.svend.goodviews.models.Friendship;
import be.svend.goodviews.models.User;
import be.svend.goodviews.models.notification.FriendRequestNotification;
import be.svend.goodviews.models.notification.Notification;
import be.svend.goodviews.repositories.notification.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FriendRequestService {
    NotificationRepository notificationRepo;

    NotificationService notificationService; // For general notificationservices like deleting

    // CONSTRUCTOR

    public FriendRequestService(NotificationRepository notificationRepo, NotificationService notificationService) {
        this.notificationRepo = notificationRepo;
        this.notificationService = notificationService;
    }

    // FIND METHODS
    public Optional<FriendRequestNotification> findFriendRequestNotificationByFriendship(Friendship friendship) {
        return notificationRepo.findByFriendRequest(friendship);
    }

    // CREATE METHODS
    public void sendFriendRequestNotification(Friendship friendship, User userToNotify) {
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

    // DELETE METHODS

    public void deleteFriendRequest(Friendship friendship) {
        Optional<FriendRequestNotification> request = findFriendRequestNotificationByFriendship(friendship);
        if (request.isPresent()) notificationRepo.delete(request.get());
    }

    public void deleteNotificationsByFriendship(Friendship friendship) {
        List<Notification> allNotifications = new ArrayList<>();
        allNotifications.addAll(notificationRepo.findByOriginUserAndTargetUser(friendship.getFriendA(), friendship.getFriendB()));
        allNotifications.addAll(notificationRepo.findByOriginUserAndTargetUser(friendship.getFriendB(), friendship.getFriendA()));

        notificationService.deleteNotifications(allNotifications);
    }
}

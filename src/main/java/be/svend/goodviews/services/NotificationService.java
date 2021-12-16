package be.svend.goodviews.services;

import be.svend.goodviews.models.Friendship;
import be.svend.goodviews.models.User;
import be.svend.goodviews.models.notification.FriendRequest;
import be.svend.goodviews.models.notification.Notification;

import be.svend.goodviews.repositories.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationService {
    NotificationRepository notificationRepo;


    // CONSTRUCTORS

    public NotificationService(NotificationRepository notificationRepo) {
        this.notificationRepo = notificationRepo;

    }

    // FIND METHODS

    public Optional<FriendRequest> findRequestByFriendship(Friendship friendship) {
        return notificationRepo.findByFriendRequest(friendship);
    }


    // CREATE METHODS

    public void createFriendRequest(Friendship friendship, User userToNotify) {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setTargetUser(userToNotify);
        friendRequest.setFriendRequest(friendship);
        notificationRepo.save(friendRequest);

        System.out.println("Friend request sent");
    }

    public void createFriendAcceptance(User userWhoAccepted, User userToNotify) {
        Notification acceptedFriendNotification = new Notification();
        acceptedFriendNotification.setTargetUser(userToNotify);
        acceptedFriendNotification.setMessage(userWhoAccepted.getUsername() + "accepted your friendrequest");

        System.out.println("Friend request accepted");
    }

    // DELETE

    public boolean deleteNotification(Notification notification) {
        if (notification == null || notification.getId() == null) return false;
        if (notificationRepo.findById(notification.getId()).isEmpty()) return false;

        notificationRepo.delete(notification);
        System.out.println("Notification deleted");
        return true;
    }

    public void deleteNotificationsInvolvingUser(User user) {

        // TODO: fill in
    }

    public void deleteNotificationsByFriendship(Friendship friendship) {
        Optional<FriendRequest> request = findRequestByFriendship(friendship);
        if (request.isPresent()) notificationRepo.delete(request.get());

        // TODO: fill in other notifications of the friendship
    }

    // TODO: Delete all finished notifications
}

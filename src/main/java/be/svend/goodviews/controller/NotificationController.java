package be.svend.goodviews.controller;

import be.svend.goodviews.models.notification.FriendRequestNotification;
import be.svend.goodviews.models.notification.Notification;
import be.svend.goodviews.repositories.notification.FriendRequestNotificationRepository;
import be.svend.goodviews.repositories.notification.NotificationRepository;
import be.svend.goodviews.services.notification.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static be.svend.goodviews.services.StringValidator.isValidString;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    NotificationRepository notificationRepo;
    NotificationService notificationService;
    FriendRequestNotificationRepository friendRequestNotificationRepo;

    public NotificationController(NotificationRepository notificationRepo,
                                  NotificationService notificationService,
                                  FriendRequestNotificationRepository friendRequestNotificationRepo) {
        this.notificationRepo = notificationRepo;
        this.notificationService = notificationService;
        this.friendRequestNotificationRepo = friendRequestNotificationRepo;
    }


    // FIND METHODS

    @CrossOrigin
    @GetMapping("/findNumberByUsername")
    public ResponseEntity findNumberOfNotificationsByUsername(@RequestParam String username) {
        System.out.println("FIND NUMBER OF NOTIFICATIONS BY USERNAME CALLED for " + username);

        if (!isValidString(username)) return ResponseEntity.badRequest().body("Invalid input format");

        Integer numberOfNotifications = notificationRepo.countNotificationsByTargetUser_UsernameAndSeenFalse(username);
        Integer numberOfFriendRequests = friendRequestNotificationRepo.countFriendRequestNotificationByTargetUser_UsernameAndSeenFalse(username);

        return ResponseEntity.ok(numberOfNotifications - numberOfFriendRequests);
    }

    @CrossOrigin
    @GetMapping("/findNumberOfFriendRequestsByUsername")
    public ResponseEntity findNumberOfNFriendRequestsByUsername(@RequestParam String username) {
        System.out.println("FIND NUMBER OF FRIEND REQUESTS BY USERNAME CALLED for " + username);

        if (!isValidString(username)) return ResponseEntity.badRequest().body("Invalid input format");

        Integer numberOfFriendRequests = friendRequestNotificationRepo.countFriendRequestNotificationByTargetUser_UsernameAndSeenFalse(username);

        return ResponseEntity.ok(numberOfFriendRequests);
    }

    @CrossOrigin
    @GetMapping("/findNotifications")
        public ResponseEntity findNotificationsByUsername(@RequestParam String username) {
            System.out.println("FIND NOTIFICATIONS BY USERNAME CALLED for " + username);

            if (!isValidString(username)) return ResponseEntity.badRequest().body("Invalid input format");

            List<Notification> notifications = notificationService.findByTargetUsername(username);

            notifications = notifications.stream().filter(n -> !(n instanceof FriendRequestNotification)).collect(Collectors.toList());

            return ResponseEntity.ok(notifications);
    }

    @CrossOrigin
    @GetMapping("/findTenNotifications")
    public ResponseEntity findTenNotificationsByUsername(@RequestParam String username) {
        System.out.println("FIND TEN NOTIFICATIONS BY USERNAME CALLED for " + username);

        if (!isValidString(username)) return ResponseEntity.badRequest().body("Invalid input format");

        List<Notification> notifications = notificationService.findByTargetUsername(username);

        notifications = notifications.stream().filter(n -> !(n instanceof FriendRequestNotification)).collect(Collectors.toList());

        Integer maxNumber = 10;
        if (notifications.size() < 10) maxNumber = notifications.size();
        return ResponseEntity.ok(notifications.subList(0,maxNumber));
    }

    @CrossOrigin
    @GetMapping("/findFriendRequests")
    public ResponseEntity findFriendRequestsByUsername(@RequestParam String username) {
        System.out.println("FIND FRIENDREQUESTS BY USERNAME CALLED for " + username);

        if (!isValidString(username)) return ResponseEntity.badRequest().body("Invalid input format");

        List<Notification> friendRequests = notificationService.findByTargetUsername(username);

        friendRequests = friendRequests.stream().filter(n -> (n instanceof FriendRequestNotification)).collect(Collectors.toList());


        return ResponseEntity.ok(friendRequests);
    }
}

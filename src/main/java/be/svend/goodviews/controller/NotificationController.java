package be.svend.goodviews.controller;

import be.svend.goodviews.repositories.notification.FriendRequestNotificationRepository;
import be.svend.goodviews.repositories.notification.NotificationRepository;
import be.svend.goodviews.services.notification.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}

package be.svend.goodviews.services.notification;

import be.svend.goodviews.models.notification.CommentNotification;
import be.svend.goodviews.models.notification.FriendRequestNotification;
import be.svend.goodviews.models.notification.LikeNotification;
import be.svend.goodviews.models.notification.Notification;
import be.svend.goodviews.services.rating.RatingScrubber;
import be.svend.goodviews.services.users.UserScrubber;

import java.util.List;

public class NotificationScrubber {

    public static List<Notification> scrubUsers(List<Notification> notificationList) {

        for (Notification notification: notificationList) {
            notification.setOriginUser(UserScrubber.scrubAllExceptUsername(notification.getOriginUser()));
            notification.setTargetUser(UserScrubber.scrubAllExceptUsername(notification.getTargetUser()));

            if (notification instanceof CommentNotification) {
                CommentNotification commentNotification = (CommentNotification) notification;
                commentNotification.getComment().setUser(UserScrubber.scrubAllExceptUsername(commentNotification.getComment().getUser()));
                commentNotification.setRating(RatingScrubber.scrubRatingOfUserInfo(commentNotification.getRating()));
            }

            if (notification instanceof FriendRequestNotification) {
                FriendRequestNotification friendRequestNotification = (FriendRequestNotification) notification;
                friendRequestNotification.getFriendRequest().setFriendA(UserScrubber.scrubAllExceptUsername(friendRequestNotification.getFriendRequest().getFriendA()));
                friendRequestNotification.getFriendRequest().setFriendB(UserScrubber.scrubAllExceptUsername(friendRequestNotification.getFriendRequest().getFriendB()));
            }

            if (notification instanceof LikeNotification) {
                LikeNotification likeNotification = (LikeNotification) notification;
                likeNotification.setRating(RatingScrubber.scrubRatingOfUserInfo(likeNotification.getRating()));
            }
        }

        return notificationList;
    }
}

package be.svend.goodviews.models.notification;

import be.svend.goodviews.models.Friendship;
import be.svend.goodviews.models.notification.Notification;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class FriendRequest extends Notification {

    @OneToOne
    private Friendship friendRequest;

    // GETTERS & SETTERS

    public Friendship getFriendRequest() {
        return friendRequest;
    }

    public void setFriendRequest(Friendship friendRequest) {
        this.friendRequest = friendRequest;
    }

    // OTHER METHODS

    @Override
    public String toString() {
        return "FriendRequest{" +
                "friendRequest=" + friendRequest +
                "} " + super.toString();
    }
}

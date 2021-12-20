package be.svend.goodviews.models.update;

import be.svend.goodviews.models.Friendship;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class FriendshipLogUpdate extends LogUpdate{

    @OneToOne
    Friendship friendship;

    // CONSTRUCTORS

    public FriendshipLogUpdate(Friendship friendship) {
        this.friendship = friendship;
        if (friendship == null) return;
        super.setUser(friendship.getFriendA());
        super.setOtherUser(friendship.getFriendB());
        updateUpdateString();
    }

    public FriendshipLogUpdate() {
        this(null);
    }

    // GETTERS & SETTERS

    public Friendship getFriendship() {
        return friendship;
    }

    public void setFriendship(Friendship friendship) {
        this.friendship = friendship;
        updateUpdateString();
    }

    public void updateUpdateString() {
        if (super.getUser() == null || super.getUser() .getUsername() == null) return;
        if (super.getOtherUser() == null || super.getOtherUser().getUsername() == null) return;
        super.setUpdateString(super.getUser().getUsername() + " has become friends with " + super.getOtherUser().getUsername() + ".");
    }

    // TO STRING

    @Override
    public String toString() {
        return "FriendshipLogUpdate{" +
                "friendship=" + friendship +
                "} " + super.toString();
    }
}



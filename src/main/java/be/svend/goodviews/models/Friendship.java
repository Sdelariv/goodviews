package be.svend.goodviews.models;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.LocalDate;

@Entity
public class Friendship {

    @Id
    private String id;

    private LocalDate dateCreated;

    @OneToOne
    private User friendA;

    @OneToOne
    private User friendB;


    // GETTERS & SETTERS

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void updateId() {
        if (this.friendA == null) return;
        if (this.friendA.getUsername() == null) return;

        if (this.friendB == null) return;
        if (this.friendB.getUsername() == null) return;

        this.id = friendA.getUsername() + friendB.getUsername();
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public User getFriendA() {
        return friendA;
    }

    public void setFriendA(User friendA) {
        this.friendA = friendA;
        updateId();
    }

    public User getFriendB() {
        return friendB;
    }

    public void setFriendB(User friendB) {
        this.friendB = friendB;
        updateId();
    }


}

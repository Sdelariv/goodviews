package be.svend.goodviews.models.notification;

import be.svend.goodviews.models.TypeOfUser;
import be.svend.goodviews.models.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User targetUser;

    @ManyToOne
    private User originUser;

    @Enumerated
    private TypeOfUser typeOfUser;

    private String message;

    private boolean seen;

    private LocalDateTime dateTime;

    // CONSTRUCTORS

    public Notification() {
        this.seen = false;
        this.dateTime = LocalDateTime.now();
    }

    // GETTERS & SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User user) {
        this.targetUser = user;
    }

    public TypeOfUser getTypeOfUser() {
        return typeOfUser;
    }

    public void setTypeOfUser(TypeOfUser typeOfUser) {
        this.typeOfUser = typeOfUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public User getOriginUser() {
        return originUser;
    }

    public void setOriginUser(User originUser) {
        this.originUser = originUser;
    }

    // OTHER METHODS


    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", targetUser=" + targetUser +
                ", originUser=" + originUser +
                ", typeOfUser=" + typeOfUser +
                ", message='" + message + '\'' +
                ", seen=" + seen +
                ", dateTime=" + dateTime +
                '}';
    }
}

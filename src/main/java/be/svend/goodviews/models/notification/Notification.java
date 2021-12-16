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

    @Enumerated
    private TypeOfUser typeOfUser;

    private String message;

    private boolean seen;

    private boolean finished;

    private LocalDateTime dateTime;

    // CONSTRUCTORS

    public Notification() {
        this.seen = false;
        this.finished = false;
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

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    // OTHER METHODS

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", targetUser=" + targetUser +
                ", typeOfUser=" + typeOfUser +
                ", message='" + message + '\'' +
                ", seen=" + seen +
                ", finished=" + finished +
                ", dateTime=" + dateTime +
                '}';
    }
}

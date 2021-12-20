package be.svend.goodviews.models.update;

import be.svend.goodviews.models.User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
public class LogUpdate {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private User user;

    @OneToOne
    private User otherUser;

    private LocalDateTime dateTime;

    private String updateString;

    private boolean isClassified;

    // CONSTRUCTOR

    public LogUpdate() {
        this.dateTime = LocalDateTime.now();
        this.setClassified(false);
    }

    // GETTERS & SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getUpdateString() {
        return updateString;
    }

    public void setUpdateString(String updateString) {
        this.updateString = updateString;
    }

    public boolean isClassified() {
        return isClassified;
    }

    public void setClassified(boolean classified) {
        isClassified = classified;
    }

    public User getOtherUser() {
        return otherUser;
    }

    public void setOtherUser(User otherUser) {
        this.otherUser = otherUser;
    }

    // OTHER

    @Override
    public String toString() {
        return "LogUpdate{" +
                "id=" + id +
                ", user=" + user +
                ", otherUser=" + otherUser +
                ", dateTime=" + dateTime +
                ", updateString='" + updateString + '\'' +
                ", isClassified=" + isClassified +
                '}';
    }
}

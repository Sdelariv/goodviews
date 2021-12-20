package be.svend.goodviews.models.update;

import be.svend.goodviews.models.User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
public class Update {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private User user;

    private LocalDateTime dateTime;

    private String updateString;

    // CONSTRUCTOR

    public Update() {
        this.dateTime = LocalDateTime.now();
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

    // OTHER

    @Override
    public String toString() {
        return "Update{" +
                "id=" + id +
                ", user=" + user +
                ", dateTime=" + dateTime +
                ", updateString='" + updateString + '\'' +
                '}';
    }
}

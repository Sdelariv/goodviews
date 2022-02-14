package be.svend.goodviews.DTOs;

import be.svend.goodviews.models.User;
import be.svend.goodviews.services.users.UserScrubber;

import java.time.LocalDateTime;

public class TimelineDTO {

    private Long id;

    private User user;

    private User otherUser;

    private LocalDateTime dateTime;

    private String updateString;

    private UpdateType type;


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
        this.user = UserScrubber.scrubAllExceptUsername(user);
    }

    public User getOtherUser() {
        return otherUser;
    }

    public void setOtherUser(User otherUser) {
        this.otherUser = otherUser;
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

    public UpdateType getType() {
        return type;
    }

    public void setType(UpdateType type) {
        this.type = type;
    }

}

package be.svend.goodviews.models.update;

import be.svend.goodviews.models.Comment;
import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.User;

import java.time.LocalDateTime;
import java.util.List;

public class TimelineDTO {

    private Long id;

    private User user;

    private User otherUser;

    private LocalDateTime dateTime;

    private String updateString;

    private String type;

    private Film film;

    private Rating rating;

    private List<Comment> commentList;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }
}

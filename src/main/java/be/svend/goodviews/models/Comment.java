package be.svend.goodviews.models;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Comment {

    @Id
    @GeneratedValue
    private Long id;

    private String comment;

    private LocalDateTime dateTime;

    private LocalDateTime updated;

    @ManyToOne
    private User user;

    @ManyToOne
    private Rating rating;


    // GETTERS & SETTERS


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    // OTHER

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", date=" + dateTime +
                ", user=" + user + '\'' +
                ", rating=" + rating +
                ", comment='" + comment +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment commentToCompare = (Comment) o;
        if (commentToCompare.getId() == this.getId()) return true;
        return false;
    }

}

package be.svend.goodviews.models.notification;

import be.svend.goodviews.models.Comment;
import be.svend.goodviews.models.Rating;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.Objects;

@Entity
public class CommentNotification extends Notification {

    @OneToOne
    private Comment comment;

    @OneToOne
    private Rating rating;

    // CONSTRUCTOR

    public CommentNotification() {
        this(null, null);
    }

    public CommentNotification(Comment comment, Rating rating) {
        this.comment = comment;
        this.rating = rating;
        if (comment != null && comment.getUser() != null) super.setOriginUser(comment.getUser());
        if (rating != null && rating.getUser() != null) super.setTargetUser(rating.getUser());
    }

    // GETTERS & SETTERS

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
        updateMessage();
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
        updateMessage();
    }

    public void updateMessage() {
        if (comment == null || comment.getUser() == null || comment.getUser().getUsername() == null) return;
        if (rating == null || rating.getFilm() == null || rating.getFilm().getTitle() == null) return;

        super.setMessage(comment.getUser().getUsername() + " commented on your rating of " + rating.getFilm().getTitle());
    }

    // OTHER METHODS

    @Override
    public String toString() {
        return "CommentNotification{" +
                "comment=" + comment +
                ", rating=" + rating +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentNotification that = (CommentNotification) o;
        return Objects.equals(comment, that.comment) && Objects.equals(rating, that.rating);
    }

}

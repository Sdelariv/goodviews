package be.svend.goodviews.models.notification;

import be.svend.goodviews.models.Comment;
import be.svend.goodviews.models.Rating;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

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
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    // OTHER METHODS

    @Override
    public String toString() {
        return "CommentNotification{" +
                "comment=" + comment +
                ", rating=" + rating +
                "} " + super.toString();
    }
}

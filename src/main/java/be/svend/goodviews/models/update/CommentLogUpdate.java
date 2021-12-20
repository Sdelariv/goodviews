package be.svend.goodviews.models.update;

import be.svend.goodviews.models.Comment;
import be.svend.goodviews.models.Rating;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class CommentLogUpdate extends LogUpdate {

    @OneToOne
    Comment comment;

    @OneToOne
    Rating rating;

    // CONSTRUCTORS

    public CommentLogUpdate() {
        this(null,null);
    }

    public CommentLogUpdate(Rating rating, Comment comment) {
        if (comment == null) return;
        this.comment = comment;
        if (rating == null) return;
        this.rating = rating;
        if (rating.getUser() == null) return;
        super.setUser(rating.getUser());
        updateUpdateString();
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

    public void updateUpdateString() {
        if (super.getUser() == null || super.getUser() .getUsername() == null) return;
        if (rating == null || rating.getFilm() == null || rating.getFilm().getTitle() == null) return;
        super.setUpdateString(super.getUser().getUsername() + " has commented on " + rating.getUser().getUsername() + "'s rating of " + rating.getFilm().getTitle() + ".");
    }

    // TO STRING

    @Override
    public String toString() {
        return "CommentLogUpdate{" +
                "comment=" + comment +
                ", rating=" + rating +
                "} " + super.toString();
    }
}

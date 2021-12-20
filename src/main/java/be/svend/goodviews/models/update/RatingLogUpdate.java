package be.svend.goodviews.models.update;

import be.svend.goodviews.models.Rating;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class RatingLogUpdate extends LogUpdate {

    @OneToOne
    Rating rating;

    public RatingLogUpdate(Rating rating) {
        if (rating == null) return;
        this.rating = rating;
        if (rating.getUser() == null) return;
        super.setUser(rating.getUser());
        updateUpdateString();
    }

    public RatingLogUpdate() {
        this(null);
    }

    // GETTERS & SETTERS

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
        updateUpdateString();
    }


    public void updateUpdateString() {
        if (super.getUser() == null || super.getUser() .getUsername() == null) return;
        if (rating == null || rating.getFilm() == null || rating.getFilm().getTitle() == null) return;
        super.setUpdateString(super.getUser().getUsername() + " has rated " + rating.getFilm().getTitle() + ".");
    }

    // TO STRING

    @Override
    public String toString() {
        return "RatingUpdate{" +
                "rating=" + rating +
                "} " + super.toString();
    }
}

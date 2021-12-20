package be.svend.goodviews.models.update;

import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.User;

import javax.persistence.OneToOne;

public class RatingUpdate extends Update {

    @OneToOne
    Rating rating;

    public RatingUpdate(Rating rating, User user) {
        this.rating = rating;
        super.setUser(user);
        updateUpdateString();
    }

    // GETTERS & SETTERS

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
        updateUpdateString();
    }

    @Override
    public void setUser(User user) {
        super.setUser(user);
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

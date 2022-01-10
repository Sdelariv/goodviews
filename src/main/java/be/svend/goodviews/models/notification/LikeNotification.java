package be.svend.goodviews.models.notification;

import be.svend.goodviews.models.Rating;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class LikeNotification extends Notification{

    @OneToOne
    Rating rating;

    // GETTERS & SETTERS

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }
}

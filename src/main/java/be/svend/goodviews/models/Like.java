package be.svend.goodviews.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Like {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private User user;

    @OneToOne
    private Rating rating;

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

    public void setUser(User username) {
        this.user = username;
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
        String username = "/";
        if (user != null && user.getUsername() != null) username = user.getUsername();

        String ratingString = "/";
        if (rating != null && rating.getUser() != null && rating.getUser().getUsername() != null) {
            if (rating.getFilm() != null && rating.getFilm().getTitle() != null) {
                ratingString = rating.getUser().getUsername() + "'s rating of " + rating.getFilm().getTitle();
            }
        }

        return "Like{" +
                "id=" + id +
                ", username=" + username +
                ", rating=" + ratingString +
                '}';
    }
}

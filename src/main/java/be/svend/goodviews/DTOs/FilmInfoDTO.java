package be.svend.goodviews.DTOs;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.WantToSee;

import java.util.List;

public class FilmInfoDTO {

    private Film film;

    private List<Rating> ratings;

    private Rating userRating;

    private Long userWtsId;


    // GETTERS & SETTERS

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public Rating getUserRating() {
        return userRating;
    }

    public void setUserRating(Rating userRating) {
        this.userRating = userRating;
    }

    public Long getUserWtsId() {
        return userWtsId;
    }

    public void setUserWtsId(Long userWtsId) {
        this.userWtsId = userWtsId;
    }
}

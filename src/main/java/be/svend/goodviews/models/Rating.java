package be.svend.goodviews.models;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Rating {

    @Id
    @GeneratedValue
    private Long id;

    private Integer ratingValue;

    private LocalDate dateOfRating;

    private String review;

    private LocalDate dateOfReview;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    private Film film;

    // GETTERS & SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(Integer ratingValue) {
        this.ratingValue = ratingValue;

    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public LocalDate getDateOfReview() {
        return dateOfReview;
    }

    public void setDateOfReview(LocalDate dateOfReview) {
        this.dateOfReview = dateOfReview;
    }

    public LocalDate getDateOfRating() {
        return dateOfRating;
    }

    public void setDateOfRating(LocalDate dateOfRating) {
        this.dateOfRating = dateOfRating;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }
}

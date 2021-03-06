package be.svend.goodviews.models;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
public class Rating {

    @Id
    private String id;

    private Integer ratingValue;

    private LocalDate dateOfRating;

    private String review;

    private LocalDate dateOfReview;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    private Film film;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<User> userLikes;

    // GETTERS & SETTERS

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public Optional<String> updateId() {
        if (this.getFilm() == null) return Optional.empty();
        if (this.getFilm().getId() == null) return Optional.empty();

        if (this.getUser() == null) return Optional.empty();
        if (this.getUser().getUsername() == null) return Optional.empty();

        this.id = this.getUser().getUsername() + this.getFilm().getId();
        return Optional.of(this.id);
    }

    public List<User> getUserLikes() {
        return userLikes;
    }

    public void setUserLikes(List<User> userLikes) {
        this.userLikes = userLikes;
    }

    public boolean addUserLike(User user) {
        if (this.userLikes == null) userLikes = new ArrayList<>();

        if (this.getUserLikes().contains(user)) return false;
        this.userLikes.add(user);
        return true;
    }

    public boolean deleteUserLike(User user) {
        if (this.userLikes == null) return false;

        if (!this.getUserLikes().contains(user)) return false;
        this.userLikes.remove(user);
        return true;
    }

    // OTHER METHODS


    @Override
    public String toString() {
        String userName = "/";
        if (user != null) if (user.getUsername() != null) userName = user.getUsername();

        String filmTitle = "/";
        if (film != null) if (film.getTitle() != null) filmTitle = film.getTitle();

        String likeUsernames = "/";
        if (userLikes != null) likeUsernames = userLikes.stream().map(ul -> ul.getUsername()).toString();

        return "Rating{" +
                "id='" + id + '\'' +
                ", ratingValue=" + ratingValue +
                ", dateOfRating=" + dateOfRating +
                ", review='" + review + '\'' +
                ", dateOfReview=" + dateOfReview +
                ", user=" + userName +
                ", film=" + filmTitle +
                ", likes=" + likeUsernames +
                '}';
    }



}

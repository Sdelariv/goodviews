package be.svend.goodviews.models;

import javax.persistence.*;
import java.time.LocalDate;
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

    @OneToMany(fetch = FetchType.EAGER)
    private List<Comment> commentList;

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

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public void addComment(Comment comment) {
        if (this.getCommentList().contains(comment)) return;

        this.commentList.add(comment);
    }

    public void deleteComment(Comment comment) {
        System.out.println("COMMENTS");
        this.getCommentList().forEach(System.out::println);
        System.out.println("");
        System.out.println("COMMENT TO DELETE:");
        System.out.println(comment);

        if (!this.getCommentList().contains(comment)) {
            System.out.println("Can't find the comment to delete");
            return;
        }
        this.commentList.remove(comment);
    }

    // OTHER METHODS


    @Override
    public String toString() {
        String userName = "/";
        if (user != null) if (user.getUsername() != null) userName = user.getUsername();

        String filmTitle = "/";
        if (film != null) if (film.getTitle() != null) filmTitle = film.getTitle();

        return "Rating{" +
                "id='" + id + '\'' +
                ", ratingValue=" + ratingValue +
                ", dateOfRating=" + dateOfRating +
                ", review='" + review + '\'' +
                ", dateOfReview=" + dateOfReview +
                ", user=" + userName +
                ", film=" + filmTitle +
                '}';
    }



}

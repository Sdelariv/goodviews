package be.svend.goodviews.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
public class WantToSee {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private User user;

    @OneToOne
    private Film film;

    private LocalDateTime dateCreated;

    // CONSTRUCTORS

    public WantToSee() {
        this.dateCreated = LocalDateTime.now();
    }


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

    public void setUser(User user) {
        this.user = user;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    // OTHER METHODS


    @Override
    public String toString() {
        return "WantToSee{" +
                "id=" + id +
                ", user=" + user +
                ", film=" + film +
                ", dateCreated=" + dateCreated +
                '}';
    }
}

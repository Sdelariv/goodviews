package be.svend.goodviews.models.update;

import be.svend.goodviews.models.Film;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class WtsLogUpdate extends LogUpdate {

    @OneToOne
    Film film;

    public WtsLogUpdate() {
        super();
    }

    public WtsLogUpdate(String updateString) {
        super(updateString);
    }

    // GETTERS & SETTERS

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }
}

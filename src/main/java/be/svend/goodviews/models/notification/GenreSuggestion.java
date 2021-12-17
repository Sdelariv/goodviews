package be.svend.goodviews.models.notification;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.TypeOfUser;
import be.svend.goodviews.models.User;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
public class GenreSuggestion extends Notification {

    private String suggestedGenreName;

    @OneToOne
    private Film film;

    // CONSTRUCTOR

    public GenreSuggestion(){
        this(null,null,null);
    }

    public GenreSuggestion(String suggestedGenreName, Film film, User suggester) {
        this.suggestedGenreName = suggestedGenreName;
        this.film = film;
        super.setOriginUser(suggester);
        super.setTypeOfUser(TypeOfUser.ADMIN);
        super.setDateTime(LocalDateTime.now());

    }

    // GETTERS & SETTERS

    public String getSuggestedGenreName() {
        return suggestedGenreName;
    }

    public void setSuggestedGenreName(String suggestedGenreName) {
        this.suggestedGenreName = suggestedGenreName;
        updateMessage();
    }

    public User getSuggester() {
        return getOriginUser();
    }

    public void setSuggester(User suggester) {
        super.setOriginUser(suggester);
        updateMessage();
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
        updateMessage();
    }

    public void updateMessage() {
        if (getSuggester() == null || getSuggester().getUsername() == null) return;
        if (film == null || film.getId() == null) return;

        super.setMessage(getSuggester().getUsername() + " has suggested the genre \"" + this.suggestedGenreName + "\" for (" + film.getId() + ")");
        if (film.getTitle() == null) super.setMessage(super.getMessage() + " " + film.getTitle());
    }

    // OTHER METHODS

    @Override
    public String toString() {
        return "GenreSuggestionNotification{" +
                "suggestedGenreName='" + suggestedGenreName + '\'' +
                ", suggester=" + getSuggester() +
                ", film=" + film +
                "} " + super.toString();
    }
}

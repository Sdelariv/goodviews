package be.svend.goodviews.models.notification;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.TypeOfUser;
import be.svend.goodviews.models.User;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class GenreSuggestion extends Notification {

    private String suggestedGenreName;

    @OneToOne
    private User suggester;

    @OneToOne
    private Film film;

    // CONSTRUCTOR

    public GenreSuggestion(){
        super.setTypeOfUser(TypeOfUser.ADMIN);
    }

    public GenreSuggestion(String suggestedGenreName, Film film, User suggester) {
        this.film = film;
        this.suggester = suggester;
        this.suggestedGenreName = suggestedGenreName;
        super.setTypeOfUser(TypeOfUser.ADMIN);
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
        return suggester;
    }

    public void setSuggester(User suggester) {
        this.suggester = suggester;
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
        if (suggester == null || suggester.getUsername() == null) return;
        if (film == null || film.getTitle() == null || film.getId() == null) return;

        super.setMessage(suggester.getUsername() + " has suggested the genre \"" + this.suggestedGenreName + "\" for " + film.getTitle() + " (" + film.getId() + ")");
    }

    // OTHER METHODS


    @Override
    public String toString() {
        return "GenreSuggestionNotification{" +
                "suggestedGenreName='" + suggestedGenreName + '\'' +
                ", suggester=" + suggester +
                ", film=" + film +
                "} " + super.toString();
    }
}

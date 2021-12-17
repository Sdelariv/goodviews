package be.svend.goodviews.models.notification;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.TypeOfUser;
import be.svend.goodviews.models.User;

import javax.persistence.Entity;
import javax.persistence.OneToOne;


@Entity
public class TagSuggestion extends Notification {

    private String suggestedTagName;

    @OneToOne
    private Film film;

    // CONSTRUCTOR

    public TagSuggestion() {
        this(null,null,null);
    }

    public TagSuggestion(String suggestedTagName, Film film, User suggester) {
        this.suggestedTagName = suggestedTagName;
        this.film = film;
        super.setOriginUser(suggester);
        super.setTypeOfUser(TypeOfUser.ADMIN);
    }

    // GETTERS & SETTERS


    public String getSuggestedTagName() {
        return suggestedTagName;
    }

    public void setSuggestedTagName(String suggestedTag) {
        this.suggestedTagName = suggestedTag;
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
        if (super.getOriginUser() == null || super.getOriginUser().getUsername() == null) return;
        if (film == null || film.getId() == null) return;

        super.setMessage(super.getOriginUser().getUsername() + " has suggested the tag \"" + this.suggestedTagName + "\" for (" + film.getId() + ")");
        if (film.getTitle() == null) super.setMessage(super.getMessage() + " " + film.getTitle());
    }

    @Override
    public void setOriginUser(User user) {
        super.setOriginUser(user);
        updateMessage();
    }

    // OTHER METHODS

    @Override
    public String toString() {
        return "TagSuggestion{" +
                "suggestedTag='" + suggestedTagName + '\'' +
                ", film=" + film +
                "} " + super.toString();
    }
}

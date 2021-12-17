package be.svend.goodviews.models.notification;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.TypeOfUser;
import be.svend.goodviews.models.User;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
public class TagSuggestion extends Notification {

    private String suggestedTag;

    @OneToOne
    private Film film;

    // CONSTRUCTOR

    public TagSuggestion() {
        this(null,null,null);
    }

    public TagSuggestion(String suggestedTag, Film film, User suggester) {
        this.suggestedTag = suggestedTag;
        this.film = film;
        super.setOriginUser(suggester);
        super.setTypeOfUser(TypeOfUser.ADMIN);
        super.setDateTime(LocalDateTime.now());
    }

    // GETTERS & SETTERS


    public String getSuggestedTag() {
        return suggestedTag;
    }

    public void setSuggestedTag(String suggestedTag) {
        this.suggestedTag = suggestedTag;
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

        super.setMessage(super.getOriginUser().getUsername() + " has suggested the tag \"" + this.suggestedTag + "\" for (" + film.getId() + ")");
        if (film.getTitle() == null) super.setMessage(super.getMessage() + " " + film.getTitle());
    }

    // OTHER METHODS

    @Override
    public String toString() {
        return "TagSuggestion{" +
                "suggestedTag='" + suggestedTag + '\'' +
                ", film=" + film +
                "} " + super.toString();
    }
}

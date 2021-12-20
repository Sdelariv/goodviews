package be.svend.goodviews.models.notification;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.TypeOfUser;
import be.svend.goodviews.models.User;

import javax.persistence.Entity;


@Entity
public class FilmSuggestion extends Notification{

    private String suggestedFilmId;

    private String filmTitle;

    // CONSTRUCTORS

    public FilmSuggestion() {
        this(null,null);
    }

    public FilmSuggestion(String suggestedFilmId, User suggester) {
        this.suggestedFilmId = suggestedFilmId;
        super.setOriginUser(suggester);
        super.setTypeOfUser(TypeOfUser.ADMIN);
    }

    // GETTERS & SETTERS


    public String getSuggestedFilmId() {
        return suggestedFilmId;
    }

    public void setSuggestedFilmId(String suggestedFilmId) {
        this.suggestedFilmId = suggestedFilmId;
        updateMessage();
    }

    public User getSuggester() {
        return super.getOriginUser();
    }

    public void setSuggester(User suggester) {
        super.setOriginUser(suggester);
        updateMessage();
    }

    public void setFilmTitle(String title) {
        this.filmTitle = title;
        updateMessage();
    }

    public String getFilmTitle() {
        return filmTitle;
    }

// OTHER

    public void updateMessage() {
        if (getSuggester() == null || getSuggester().getUsername() == null) return;
        if (suggestedFilmId == null || filmTitle == null) return;

        super.setMessage(getSuggester().getUsername() + " has suggested to add to \"" + filmTitle + "\" (" + suggestedFilmId + ") to the film database");
    }


}

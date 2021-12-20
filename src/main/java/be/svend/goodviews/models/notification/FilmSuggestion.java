package be.svend.goodviews.models.notification;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.TypeOfUser;
import be.svend.goodviews.models.User;

import javax.persistence.Entity;


@Entity
public class FilmSuggestion extends Notification{

    private String suggestedFilmId;

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


    // OTHER

    public void updateMessage() {
        if (getSuggester() == null || getSuggester().getUsername() == null) return;
        if (suggestedFilmId == null ) return;

        super.setMessage(getSuggester().getUsername() + " has suggested to add \"" + suggestedFilmId + " to the film database");
    }


}

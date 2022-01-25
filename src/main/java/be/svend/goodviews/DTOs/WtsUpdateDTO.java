package be.svend.goodviews.DTOs;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.update.WtsLogUpdate;
import be.svend.goodviews.services.users.UserScrubber;

public class WtsUpdateDTO extends TimelineDTO {

    private Film film;

    public WtsUpdateDTO(WtsLogUpdate wtsLogUpdate) {
        super.setType(UpdateType.WTS);
        super.setDateTime(wtsLogUpdate.getDateTime());
        super.setUpdateString(wtsLogUpdate.getUpdateString());
        super.setId(wtsLogUpdate.getId());
        super.setUser(UserScrubber.scrubAllExceptUsername(wtsLogUpdate.getUser()));
        this.setFilm(wtsLogUpdate.getFilm());
    }

    // GETTERS & SETTERS


    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }
}

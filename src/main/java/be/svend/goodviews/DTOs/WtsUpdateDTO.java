package be.svend.goodviews.DTOs;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.update.WtsLogUpdate;
import be.svend.goodviews.services.users.UserScrubber;

public class WtsUpdateDTO extends TimelineDTO {

    private Film film;

    private boolean userWantsToSee;

    private Integer userHasRated;

    public WtsUpdateDTO(WtsLogUpdate wtsLogUpdate, Integer userHasRated, boolean userWantsToSee) {
        super.setType(UpdateType.WTS);
        super.setDateTime(wtsLogUpdate.getDateTime());
        super.setUpdateString(wtsLogUpdate.getUpdateString());
        super.setId(wtsLogUpdate.getId());
        super.setUser(UserScrubber.scrubAllExceptUsername(wtsLogUpdate.getUser()));
        this.setFilm(wtsLogUpdate.getFilm());
        this.setUserWantsToSee(userWantsToSee);
        this.setUserHasRated(userHasRated);
    }

    // GETTERS & SETTERS


    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public boolean isUserWantsToSee() {
        return userWantsToSee;
    }

    public void setUserWantsToSee(boolean userWantsToSee) {
        this.userWantsToSee = userWantsToSee;
    }

    public Integer getUserHasRated() {
        return userHasRated;
    }

    public void setUserHasRated(Integer userHasRated) {
        this.userHasRated = userHasRated;
    }
}

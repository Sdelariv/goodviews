package be.svend.goodviews.DTOs;

import be.svend.goodviews.models.Comment;
import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.update.CommentLogUpdate;
import be.svend.goodviews.services.rating.RatingScrubber;
import be.svend.goodviews.services.users.UserScrubber;

public class CommentUpdateDTO extends TimelineDTO{

    private Comment comment;

    private Rating rating;

    public CommentUpdateDTO(CommentLogUpdate commentLogUpdate) {
        super.setType(UpdateType.COMMENT);
        super.setId(commentLogUpdate.getId());

        super.setDateTime(commentLogUpdate.getDateTime());
        super.setUpdateString(commentLogUpdate.getUpdateString());

        super.setUser(UserScrubber.scrubAllExceptUsername(commentLogUpdate.getUser()));
        super.setOtherUser(UserScrubber.scrubAllExceptUsername(commentLogUpdate.getOtherUser()));

        this.setComment(commentLogUpdate.getComment());
        this.setRating(RatingScrubber.scrubRatingOfUserInfo(commentLogUpdate.getRating()));
    }

    // GETTERS & SETTERS


    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }
}

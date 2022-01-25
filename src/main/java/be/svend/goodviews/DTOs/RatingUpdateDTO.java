package be.svend.goodviews.DTOs;

import be.svend.goodviews.models.Comment;
import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.update.RatingLogUpdate;
import be.svend.goodviews.services.rating.RatingScrubber;
import be.svend.goodviews.services.users.UserScrubber;

import java.util.List;

public class RatingUpdateDTO extends TimelineDTO {

    private Rating rating;

    private List<Comment> commentList;

    public RatingUpdateDTO(RatingLogUpdate ratingLogUpdate, List<Comment> commentList) {
        super.setType(UpdateType.RATING);
        super.setId(ratingLogUpdate.getId());

        super.setUpdateString(ratingLogUpdate.getUpdateString());
        super.setDateTime(ratingLogUpdate.getDateTime());
        super.setUser(UserScrubber.scrubAllExceptUsername(ratingLogUpdate.getUser()));

        this.setRating(RatingScrubber.scrubRatingOfUserInfo(ratingLogUpdate.getRating()));
        this.setCommentList(commentList);
    }

    // GETTERS & SETTERS


    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }
}

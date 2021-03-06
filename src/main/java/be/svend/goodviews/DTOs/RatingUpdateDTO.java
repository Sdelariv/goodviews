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

    private boolean userWantsToSee;

    private Integer userHasRated;

    public RatingUpdateDTO(RatingLogUpdate ratingLogUpdate, List<Comment> commentList, boolean userWantsToSee, int userHasRated) {
        super.setType(UpdateType.RATING);
        super.setId(ratingLogUpdate.getId());

        super.setUpdateString(ratingLogUpdate.getUpdateString());
        super.setDateTime(ratingLogUpdate.getDateTime());
        super.setUser(ratingLogUpdate.getUser());

        this.setRating(ratingLogUpdate.getRating());
        this.setCommentList(commentList);

        this.setUserWantsToSee(userWantsToSee);
        this.setUserHasRated(userHasRated);
    }

    // GETTERS & SETTERS


    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = RatingScrubber.scrubRatingOfUserInfo(rating);
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        for (Comment comment: commentList) {
            comment.setUser(UserScrubber.scrubAllExceptUsername(comment.getUser()));
        }
        this.commentList = commentList;
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

    public void setUserHasRated(int userHasRated) {
        this.userHasRated = userHasRated;
    }
}

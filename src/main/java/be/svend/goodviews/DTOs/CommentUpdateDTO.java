package be.svend.goodviews.DTOs;

import be.svend.goodviews.models.Comment;
import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.update.CommentLogUpdate;
import be.svend.goodviews.services.rating.RatingScrubber;
import be.svend.goodviews.services.users.UserScrubber;

import java.util.List;

public class CommentUpdateDTO extends TimelineDTO{

    private Comment comment;

    private Rating rating;

    private List<Comment> commentList;

    private boolean userWantsToSee;

    private Integer userHasRated;

    public CommentUpdateDTO(CommentLogUpdate commentLogUpdate, List<Comment> commentList, boolean userWantsToSee, int userHasRated) {
        super.setType(UpdateType.COMMENT);
        super.setId(commentLogUpdate.getId());

        super.setDateTime(commentLogUpdate.getDateTime());
        super.setUpdateString(commentLogUpdate.getUpdateString());

        super.setUser(UserScrubber.scrubAllExceptUsername(commentLogUpdate.getUser()));
        super.setOtherUser(UserScrubber.scrubAllExceptUsername(commentLogUpdate.getOtherUser()));

        this.setComment(commentLogUpdate.getComment());
        this.setRating(RatingScrubber.scrubRatingOfUserInfo(commentLogUpdate.getRating()));
        this.setCommentList(commentList);

        this.setUserWantsToSee(userWantsToSee);
        this.setUserHasRated(userHasRated);
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

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
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

    @Override
    public String toString() {
        return "CommentUpdateDTO{" +
                "comment=" + comment +
                ", rating=" + rating +
                ", commentList=" + commentList +
                ", userWantsToSee=" + userWantsToSee +
                ", userHasRated=" + userHasRated +
                "} " + super.toString();
    }
}

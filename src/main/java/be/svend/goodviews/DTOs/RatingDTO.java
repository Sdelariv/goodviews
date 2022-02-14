package be.svend.goodviews.DTOs;


import be.svend.goodviews.models.Comment;
import be.svend.goodviews.models.Rating;
import be.svend.goodviews.services.rating.RatingScrubber;
import be.svend.goodviews.services.users.UserScrubber;

import java.util.List;

public class RatingDTO {

    private Long id;

    private Rating rating;

    private List<Comment> commentList;

    private boolean userWantsToSee;

    private Integer userHasRated;

    public RatingDTO(Rating rating, List<Comment> commentList, boolean userWantsToSee, int userHasRated) {
        this.setRating(rating);

        this.setCommentList(commentList);

        this.setUserWantsToSee(userWantsToSee);
        this.setUserHasRated(userHasRated);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public void setUserHasRated(Integer userHasRated) {
        this.userHasRated = userHasRated;
    }
}

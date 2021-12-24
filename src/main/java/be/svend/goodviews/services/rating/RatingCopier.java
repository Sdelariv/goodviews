package be.svend.goodviews.services.rating;

import be.svend.goodviews.models.Rating;

import java.time.LocalDate;

public class RatingCopier {

    public static Rating mergeWithNewData(Rating ratingToUpdate, Rating updatedRating) {

        if (updatedRating.getRatingValue() != null
                && !ratingToUpdate.getRatingValue().equals(updatedRating.getRatingValue())){
            ratingToUpdate.setRatingValue(updatedRating.getRatingValue());
            ratingToUpdate.setDateOfRating(LocalDate.now());
        }

        if (updatedRating.getReview() != null
                && !ratingToUpdate.getReview().equals(updatedRating.getReview())) {
            ratingToUpdate.setReview(updatedRating.getReview());
            ratingToUpdate.setDateOfReview(LocalDate.now());
        }

        if (updatedRating.getCommentList() != null
                && !ratingToUpdate.getCommentList().containsAll(updatedRating.getCommentList())) {
            ratingToUpdate.setCommentList(updatedRating.getCommentList());
        }

        return ratingToUpdate;
    }
}

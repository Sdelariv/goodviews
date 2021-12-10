package be.svend.goodviews.services;

import be.svend.goodviews.models.Rating;
import be.svend.goodviews.repositories.RatingRepository;
import org.springframework.stereotype.Component;

@Component
public class RatingValidator {
    RatingRepository ratingRepo;

    public RatingValidator(RatingRepository ratingRepo) {
        this.ratingRepo = ratingRepo;
    }


    public boolean isValidNewRating(Rating rating) {
        if (rating == null) return false;


        // TODO: fill in

        return true;

    }
}

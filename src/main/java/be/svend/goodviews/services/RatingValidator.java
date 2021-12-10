package be.svend.goodviews.services;

import be.svend.goodviews.repositories.RatingRepository;
import org.springframework.stereotype.Component;

@Component
public class RatingValidator {
    RatingRepository ratingRepo;

    public RatingValidator(RatingRepository ratingRepo) {
        this.ratingRepo = ratingRepo;
    }


}

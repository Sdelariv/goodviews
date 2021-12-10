package be.svend.goodviews.services;

import be.svend.goodviews.repositories.RatingRepository;

public class RatingService {
    RatingRepository ratingRepo;

    public RatingService(RatingRepository ratingRepo) {
        this.ratingRepo = ratingRepo;
    }


}

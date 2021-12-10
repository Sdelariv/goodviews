package be.svend.goodviews.services;

import be.svend.goodviews.models.Rating;
import be.svend.goodviews.repositories.RatingRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RatingService {
    RatingRepository ratingRepo;

    public RatingService(RatingRepository ratingRepo) {
        this.ratingRepo = ratingRepo;
    }

    // CREATE METHODS

    public Optional<Rating> createNewRating(Rating rating) {
        System.out.println("Trying to create a new rating");

        if (!ratingValidator.isValidNewRating(rating)) return Optional.empty();

        Optional<Rating> createdRating = saveRating(rating);
        if (createdRating.isPresent()) System.out.println("Created " + createdRating.get());
        else System.out.println("Couldn't create this new rating " + rating);

        return createdRating;
    }

    // INTERNAL METHODS

    private Optional<Rating> saveRating(Rating rating) {
        Rating savedRating = ratingRepo.save(rating);
        System.out.println("Saving " + rating);
        return ratingRepo.findById(savedRating.getId());
    }


}

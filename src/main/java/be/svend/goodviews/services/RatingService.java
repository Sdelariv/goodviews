package be.svend.goodviews.services;

import be.svend.goodviews.models.Rating;
import be.svend.goodviews.repositories.RatingRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RatingService {
    RatingRepository ratingRepo;
    RatingValidator ratingValidator;

    public RatingService(RatingRepository ratingRepo, RatingValidator ratingValidator) {
        this.ratingRepo = ratingRepo;
        this.ratingValidator = ratingValidator;
    }

    // FIND METHODS

    public Optional<Rating> findById(String filmId) {
        Optional<Rating> foundRating = ratingRepo.findById(filmId);

        if (foundRating.isPresent()) System.out.println("Found rating: " + foundRating);

        return foundRating;
    }

    public List<Rating> findByFilmId(String filmId) {
        List<Rating> foundRatings = ratingRepo.findByFilm_Id(filmId);

        if (!foundRatings.isEmpty()) System.out.println("Found this many ratings: " + foundRatings.size());

        return foundRatings;
    }

    public List<Rating> findByUserId(String userId) {
        List<Rating> foundRatings = ratingRepo.findByUser_Username(userId);

        if (!foundRatings.isEmpty()) System.out.println("Found this many ratings: " + foundRatings.size());

        return foundRatings;
    }

    // CREATE METHODS

    public Optional<Rating> createNewRating(Rating rating) {
        System.out.println("Trying to create a new rating");

        // Check whether the rating is new and valid
        if (rating.updateId().isEmpty()) {
            System.out.println("Can't create new Rating without a user or film");
            return Optional.empty();
        }
        if (!ratingValidator.isValidNewRating(rating)) return Optional.empty();

        // Saving Rating
        Optional<Rating> createdRating = saveRating(rating);
        if (createdRating.isPresent()) System.out.println("Created " + createdRating.get());
        else System.out.println("Couldn't create this new rating " + rating);

        return createdRating;
    }

    public List<Rating> createNewRatings(List<Rating> ratings) {
        List<Rating> createdRatings = new ArrayList<>();

        for (Rating rating: ratings) {
            Optional<Rating> createdRating = createNewRating(rating);
            if (createdRating.isPresent()) createdRatings.add(createdRating.get());
        }

        return createdRatings;
    }

    /**
     * Only meant to be used when recreating ratings that got lost in db but are saved elsewhere
     * @param rating
     * @return
     */
    public Rating recreateOldRating(Rating rating) {
        ratingRepo.save(rating);
        return rating;
    }


    // INTERNAL METHODS

    private Optional<Rating> saveRating(Rating rating) {
        Rating savedRating = ratingRepo.save(rating);
        System.out.println("Saving " + rating);
        return ratingRepo.findById(savedRating.getId());
    }




}

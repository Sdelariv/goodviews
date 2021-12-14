package be.svend.goodviews.services.rating;

import be.svend.goodviews.models.Comment;
import be.svend.goodviews.models.Rating;
import be.svend.goodviews.repositories.RatingRepository;
import be.svend.goodviews.services.film.FilmService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RatingService {
    RatingRepository ratingRepo;
    RatingValidator ratingValidator;
    FilmService filmService;

    public RatingService(RatingRepository ratingRepo, RatingValidator ratingValidator, FilmService filmService) {
        this.ratingRepo = ratingRepo;
        this.ratingValidator = ratingValidator;
        this.filmService = filmService;
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

    public List<Rating> findByUsername(String userId) {
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
        rating.setDateOfRating(LocalDate.now());
        Optional<Rating> createdRating = saveRating(rating);
        if (createdRating.isPresent()) System.out.println("Created " + createdRating.get());
        else System.out.println("Couldn't create this new rating " + rating);

        filmService.calculateAndUpdateAverageRatingByFilmId(rating.getFilm().getId());

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
     * Only meant to be used when recreating ratings that got lost in db but are saved elsewhere.
     * Only use after users and films have been recreated too.
     * @param ratings - The list of ratings that need saving again
     * @return List<Rating> - The list of ratings that have been saved
     */
    public List<Rating> recreateOldRatings(List<Rating> ratings) {
        List<Rating> savedRatings = new ArrayList<>();

        for (Rating rating: ratings) {
            savedRatings.add(ratingRepo.save(rating));
        }

        return savedRatings;
    }

    // UPDATE METHODS

    public Optional<Rating> updateRating(Rating rating) {

        Optional<Rating> existingRating = ratingValidator.ratingInDatabase(rating);
        if (existingRating.isEmpty()) return Optional.empty();

        if (ratingValidator.isValidRating(rating)) {
            System.out.println("Made invalid changes to rating");
            return Optional.empty();
        }
        return saveRating(rating);
    }

    // TODO: Figure out whether this is the best place and tactic:

    public Optional<Rating> updateRatingValueByUserAndFilmId(String userId, String filmId, Integer ratingValue) {
        if (!ratingValidator.isValidRatingValue(ratingValue)) return Optional.empty();

        Optional<Rating> existingRating = ratingValidator.ratingIdInDatabase(userId+filmId);
        if (existingRating.isEmpty()) return Optional.empty();

        Rating ratingToUpdate = existingRating.get();;
        ratingToUpdate.setRatingValue(ratingValue);
        ratingToUpdate.setDateOfRating(LocalDate.now());

        filmService.calculateAndUpdateAverageRatingByFilmId(filmId);

        return updateRating(ratingToUpdate);
    }

    public Optional<Rating> updateReviewByUserAndFilmId(String userId, String filmId, String review) {
        Optional<Rating> existingRating = ratingValidator.ratingIdInDatabase(userId+filmId);
        if (existingRating.isEmpty()) return Optional.empty();

        Rating ratingToUpdate = existingRating.get();;
        ratingToUpdate.setReview(review);
        ratingToUpdate.setDateOfReview(LocalDate.now());

        return updateRating(ratingToUpdate);
    }

    public Optional<Rating> addCommentToRating(Rating rating, Comment comment) {
        rating.addComment(comment);
        return saveRating(rating);
    }


    // DELETE METHODS

    public boolean deleteRatingById(String ratingId) {
        System.out.println("Trying to delete a rating with id:" + ratingId);
        if (ratingId == null) return false;

        // Find the rating
        Optional<Rating> foundRating = findById(ratingId);
        if (foundRating.isEmpty()) System.out.println("Failed");

        // Delete the rating
        String filmId = foundRating.get().getFilm().getId();

        ratingRepo.delete(foundRating.get());
        System.out.println("Succesfully deleted the rating");

        filmService.calculateAndUpdateAverageRatingByFilmId(filmId);
        return true;
    }

    public void deleteRatingsById(List<String> ratingIds) {
        for (String ratingId: ratingIds) {
            deleteRatingById(ratingId);
        }
    }

    public boolean deleteRating(Rating rating) {
        if (rating == null) return false;
        if (rating.getId() == null) return false;

        deleteRatingById(rating.getId());
        return true;
    }

    public void deleteAllRating(List<Rating> ratings) {
        for (Rating rating: ratings) {
            deleteRatingById(rating.getId());
        }
    }


    // INTERNAL METHODS

    private Optional<Rating> saveRating(Rating rating) {
        Rating savedRating = ratingRepo.save(rating);
        return ratingRepo.findById(savedRating.getId());
    }




}

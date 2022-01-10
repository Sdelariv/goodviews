package be.svend.goodviews.services.rating;

import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.User;
import be.svend.goodviews.repositories.RatingRepository;
import be.svend.goodviews.services.comment.CommentService;
import be.svend.goodviews.services.notification.LikeNotificationService;
import be.svend.goodviews.services.notification.NotificationService;
import be.svend.goodviews.services.update.LogUpdateService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static be.svend.goodviews.services.rating.RatingCopier.mergeWithNewData;
import static be.svend.goodviews.services.rating.RatingValidator.isValidRatingValue;

@Service
public class RatingService {
    RatingRepository ratingRepo;
    RatingValidator ratingValidator;

    CommentService commentService;  // Needed to delete comments before deleting ratings

    NotificationService notificationService; // For deleting rating from notifications
    LikeNotificationService likeNotificationService; // For notifying about likes

    LogUpdateService logUpdateService; // For logupdates

    public RatingService(RatingRepository ratingRepo, RatingValidator ratingValidator,
                         CommentService commentService,
                         NotificationService notificationService, LikeNotificationService likeNotificationService,
                         LogUpdateService logUpdateService) {
        this.ratingRepo = ratingRepo;
        this.ratingValidator = ratingValidator;

        this.commentService = commentService;

        this.notificationService = notificationService;
        this.likeNotificationService = likeNotificationService;
        this.logUpdateService = logUpdateService;


    }

    // FIND METHODS

    public Optional<Rating> findById(String id) {
        Optional<Rating> foundRating = ratingRepo.findById(id);

        if (foundRating.isPresent()) System.out.println("Found rating: " + foundRating.get());

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

    /**
     * Presumes that the ratingvalue,user and film checks have been done, and that the rating is new
     * @param rating
     * @return
     */
    public Optional<Rating> createNewRating(Rating rating) {
        System.out.println("Trying to create a new rating");

        // Prep
        rating.setDateOfRating(LocalDate.now());
        rating.updateId();
        if (rating.getReview() != null) rating.setDateOfReview(LocalDate.now());

        // Saving Rating
        Optional<Rating> createdRating = saveRating(rating);
        if (createdRating.isEmpty()) {
            System.out.println("Couldn't create this new rating " + rating);
            return Optional.empty();
        }

        // Log-update
        System.out.println("Created " + createdRating.get());
        logUpdateService.createRatingUpdate(createdRating.get()); // TODO: Move to controller?

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

    /**
     * Will find the rating in the database, and update whatever is new in the given object to the existing rating (which it then saves).
     * Will update: ratingValue,review,commentList
     * @param updatedRating
     * @return Optional<Rating> returns the updatedRating (optional) if present, empty (optional) if it couldn't find it or couldn't update.
     */
    public Optional<Rating> updateRating(Rating updatedRating) {
        Optional<Rating> existingRating = findById(updatedRating.getId());
        if (existingRating.isEmpty()) return Optional.empty();

        Rating ratingToUpdate = mergeWithNewData(existingRating.get(),updatedRating);

        return saveRating(ratingToUpdate);
    }

    // TODO: Figure out whether this is the best place and tactic:

    public Optional<Rating> updateRatingWithRatingValue(Rating rating, Integer ratingValue) {
        rating.setRatingValue(ratingValue);
        rating.setDateOfRating(LocalDate.now());

        return updateRating(rating);
    }

    public Optional<Rating> updateRatingValueByUserAndFilmId(String userId, String filmId, Integer ratingValue) {
        if (!isValidRatingValue(ratingValue)) return Optional.empty();

        Optional<Rating> existingRating = ratingValidator.ratingIdInDatabase(userId+filmId);
        if (existingRating.isEmpty()) return Optional.empty();

        Rating ratingToUpdate = existingRating.get();;
        ratingToUpdate.setRatingValue(ratingValue);
        ratingToUpdate.setDateOfRating(LocalDate.now());

        return updateRating(ratingToUpdate);
    }

    public Optional<Rating> updateRatingWithReview(Rating rating, String review) {
        rating.setReview(review);
        rating.setDateOfReview(LocalDate.now());

        return updateRating(rating);
    }

    public Optional<Rating> updateReviewByRatingId(String ratingId, String review) {
        Optional<Rating> existingRating = ratingValidator.ratingIdInDatabase(ratingId);
        if (existingRating.isEmpty()) return Optional.empty();

        Rating ratingToUpdate = existingRating.get();;
        ratingToUpdate.setReview(review);
        ratingToUpdate.setDateOfReview(LocalDate.now());

        return updateRating(ratingToUpdate);
    }

    /**
     * Presumes that the rating and user exist
     * @param rating
     * @param user
     * @return
     */
    public Optional<Rating> addLikeToRating(Rating rating, User user) {
        if (!rating.addUserLike(user)) {
            System.out.println("Like already exists");
            return Optional.empty();
        }

        Optional<Rating> savedRating = saveRating(rating);
        if (savedRating.isEmpty()) {
            System.out.println("Something went wrong liking");
            return Optional.empty();
        }

        logUpdateService.createGeneralLog(user.getUsername() + " has liked " + rating.getUser().getUsername() + "'s rating of " + rating.getFilm().getTitle());
        likeNotificationService.createLikeNotification(user,rating);

        return savedRating;
    }

    public void removeLikeFromRatingByUser(Rating rating, User user) {
        rating.deleteUserLike(user);

        logUpdateService.createGeneralLog(user.getUsername() + " has unliked " + rating.getUser().getUsername() + "'s rating of " + rating.getFilm().getTitle());
        likeNotificationService.deleteLikeNotification(user,rating);
        saveRating(rating);
    }

    public void removeLikesByUser(User user) {
        List<Rating> ratingWithLikes = ratingRepo.findByUserLikesContaining(user);

        for (Rating rating: ratingWithLikes) {
            rating.deleteUserLike(user);
            saveRating(rating);
            likeNotificationService.deleteLikeNotification(user, rating);
        }
    }


    // DELETE METHODS

    public boolean deleteRatingById(String ratingId) {
        System.out.println("Trying to delete a rating with id:" + ratingId);
        if (ratingId == null) return false;

        // Find the rating
        Optional<Rating> foundRating = findById(ratingId);
        if (foundRating.isEmpty()) System.out.println("Failed");

        deleteRating(foundRating.get());
        return true;
    }

    public void deleteRatingsById(List<String> ratingIds) {
        for (String ratingId: ratingIds) {
            deleteRatingById(ratingId);
        }
    }

    public boolean deleteRating(Rating rating) {
        if (rating.getId() == null) return false;
        String ratingId = rating.getId();

        // Delete traces
        logUpdateService.deleteRatingFromLogByRating(rating);
        commentService.deleteAllCommentsContainingRating(rating);
        notificationService.deleteNotificationsByRating(rating);

        // Prep info for log and updating rating average
        String filmId = rating.getFilm().getId();
        String filmTitle = rating.getFilm().getTitle();
        String username = rating.getUser().getUsername();

        // Delete
        ratingRepo.delete(rating);
        System.out.println("Succesfully deleted the rating");
        if (findById(ratingId).isPresent()) return false;

        // Post info // TODO: Move to Controller
        logUpdateService.createGeneralLog("Deleted " + username + "'s rating of " + filmTitle);

        return true;
    }


    public void deleteAllRating(List<Rating> ratings) {
        for (Rating rating: ratings) {
            deleteRatingById(rating.getId());
        }
    }

    public boolean deleteByFilmId(String filmId) {
        List<Rating> ratingsOfFilm = ratingRepo.findByFilm_Id(filmId);

        for (Rating rating : ratingsOfFilm) {
            deleteRating(rating);
        }
        return true;
    }

    public void deleteRatingsByUser(User user) {
        List<Rating> ratingsOfUser = ratingRepo.findByUser_Username(user.getUsername());

        for (Rating rating: ratingsOfUser) {
            deleteRating(rating);
        }
    }



    // INTERNAL METHODS

    private Optional<Rating> saveRating(Rating rating) {
        Rating savedRating = ratingRepo.save(rating);
        return ratingRepo.findById(savedRating.getId());
    }


}

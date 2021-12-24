package be.svend.goodviews.services.rating;

import be.svend.goodviews.models.Rating;
import be.svend.goodviews.repositories.FilmRepository;
import be.svend.goodviews.repositories.RatingRepository;
import be.svend.goodviews.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RatingValidator {
    RatingRepository ratingRepo;
    FilmRepository filmRepo;
    UserRepository userRepo;

    public RatingValidator(RatingRepository ratingRepo, FilmRepository filmRepo, UserRepository userRepo) {
        this.ratingRepo = ratingRepo;
        this.filmRepo = filmRepo;
        this.userRepo = userRepo;
    }

    public boolean isValidNewRating(Rating rating) {
        if (!isValidRating(rating)) return false;

        if (isInDatabase(rating).isPresent()) {
            System.out.println("Rating already exists");
            return false;
        }

        return true;

    }

    public boolean isValidRating(Rating rating) {
        if (rating == null || rating.getRatingValue() == null) return false;

        if (!hasValidRatingValue(rating)) {
            System.out.println("Invalid ratingValue");
            return false;
        }

        if (!hasValidFilm(rating)) {
            System.out.println("Rating has no film");
            return false;
        }

        if (!hasValidUser(rating)){
            System.out.println("Rating has no valid user");
            return false;
        }

        return true;
    }

    public static boolean hasValidRatingValue(Rating rating) {
        if (rating == null || rating.getRatingValue() == null) return false;

        return isValidRatingValue(rating.getRatingValue());
    }

    public static boolean isValidRatingValue(Integer ratingValue) {
        if (ratingValue == null) return false;
        if (ratingValue < 0) return false;
        if (ratingValue > 100) return false;

        return true;
    }

    public Optional<Rating> isInDatabase(Rating rating) {
        rating.updateId();
        if (rating == null || rating.getId() == null);
        return ratingIdInDatabase(rating.getId());
    }

    public Optional<Rating> ratingIdInDatabase(String ratingId) {
        return ratingRepo.findById(ratingId);
    }

    public boolean hasValidUser(Rating rating) {
        if (rating == null) return false;
        if (rating.getUser() == null || rating.getUser().getUsername() == null) return false;
        if (userRepo.findByUsername(rating.getUser().getUsername()).isEmpty()) return false;

        return true;
    }

    public boolean hasValidFilm(Rating rating) {
        if (rating == null) return false;
        if (rating.getFilm() == null || rating.getFilm().getId() == null) return false;
        if (filmRepo.findById(rating.getFilm().getId()).isEmpty()) return false;

        return true;
    }

}

package be.svend.goodviews.services;

import be.svend.goodviews.models.Rating;
import be.svend.goodviews.repositories.FilmRepository;
import be.svend.goodviews.repositories.RatingRepository;
import be.svend.goodviews.repositories.UserRepository;
import org.springframework.stereotype.Component;

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
        if (rating == null) return false;

        if (rating.getRatingValue() == null) {
            System.out.println("Invalid ratingValue");
            return false;
        }

        if (!ratingHasValidFilm(rating)) {
            System.out.println("Rating has no film");
            return false;
        }

        if (!ratingHasValidUser(rating)){
            System.out.println("Rating has no user");
            return false;
        }

        return true;

    }

    private boolean ratingHasValidUser(Rating rating) {
        if (rating.getUser() == null) return false;
        if (rating.getUser().getId() == null) return false;
        if (userRepo.findById(rating.getUser().getId()).isEmpty()) return false;

        return true;
    }

    private boolean ratingHasValidFilm(Rating rating) {
        if (rating.getFilm() == null) return false;
        if (rating.getFilm().getId() == null) return false;
        if( filmRepo.findById(rating.getFilm().getId()).isEmpty()) return false;

        return true;
    }
}

package be.svend.goodviews.DTOs.creators;

import be.svend.goodviews.DTOs.RatingUpdateDTO;
import be.svend.goodviews.models.Comment;
import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.User;
import be.svend.goodviews.models.update.RatingLogUpdate;
import be.svend.goodviews.repositories.CommentRepository;
import be.svend.goodviews.repositories.RatingRepository;
import be.svend.goodviews.repositories.WantToSeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TimeLineDTOService {
    CommentRepository commentRepo;
    WantToSeeRepository wtsRepo;
    RatingRepository ratingRepo;

    public TimeLineDTOService(CommentRepository commentRepo, WantToSeeRepository wtsRepo, RatingRepository ratingRepo) {
        this.commentRepo = commentRepo;
        this.wtsRepo = wtsRepo;
        this.ratingRepo = ratingRepo;
    }

    public RatingUpdateDTO createRatingDTO(RatingLogUpdate update, User user) {
        List<Comment> commentList = commentRepo.findAllByRating(update.getRating());
        boolean userHasSeen = wtsRepo.findByUserAndFilm(user, update.getRating().getFilm()).isPresent();
        int userHasRated = findUserRating(update.getRating().getFilm(), user);

        RatingUpdateDTO ratingDTO = new RatingUpdateDTO(update, commentList, userHasSeen, userHasRated);
        return ratingDTO;
    }

    public int findUserRating(Film film, User user) {
        int userHasRated = -1;
        Optional<Rating> rating = ratingRepo.findById(user.getUsername() + film.getId());


        if (rating.isPresent()) {
            userHasRated = rating.get().getRatingValue();
        }

        return userHasRated;
    }
}

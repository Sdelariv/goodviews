package be.svend.goodviews.services.update;

import be.svend.goodviews.models.Comment;
import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.update.CommentLogUpdate;
import be.svend.goodviews.models.update.RatingLogUpdate;
import be.svend.goodviews.repositories.update.LogUpdateRepository;
import org.springframework.stereotype.Service;

@Service
public class LogUpdateService {
    LogUpdateRepository logUpdateRepo;

    public LogUpdateService(LogUpdateRepository logUpdateRepo) {
        this.logUpdateRepo = logUpdateRepo;
    }

    //  CREATE METHODS

    public void createRatingUpdate(Rating rating) {
        RatingLogUpdate ratingUpdate = new RatingLogUpdate(rating);
        logUpdateRepo.save(ratingUpdate);
    }

    public void createCommentUpdate(Rating rating, Comment comment) {
        CommentLogUpdate commentUpdate = new CommentLogUpdate(rating,comment);
        logUpdateRepo.save(commentUpdate);
    }
}

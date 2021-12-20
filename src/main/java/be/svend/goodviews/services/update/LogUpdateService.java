package be.svend.goodviews.services.update;

import be.svend.goodviews.models.Comment;
import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.User;
import be.svend.goodviews.models.update.CommentLogUpdate;
import be.svend.goodviews.models.update.LogUpdate;
import be.svend.goodviews.models.update.RatingLogUpdate;
import be.svend.goodviews.repositories.update.LogUpdateRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogUpdateService {
    LogUpdateRepository logUpdateRepo;

    public LogUpdateService(LogUpdateRepository logUpdateRepo) {
        this.logUpdateRepo = logUpdateRepo;
    }

    // FIND METHODS

    public List<LogUpdate> findByUserIncludingClassified(User user) {
        return logUpdateRepo.findByUser(user);
    }

    public List<LogUpdate> findByUserExcludingClassified(User user) {
        return logUpdateRepo.findByUserAndIsClassifiedFalse(user);
    }

    public List<LogUpdate> findByUsersExcludingClassified(List<User> users) {
        List<LogUpdate> updatesOfUsers = new ArrayList<>();
        for (User user: users) {
            List<LogUpdate> updatesOfUser = findByUserExcludingClassified(user);
            if (updatesOfUser != null) updatesOfUsers.addAll(updatesOfUser);
        }

        return updatesOfUsers.stream().sorted(Comparator.comparing(LogUpdate::getDateTime)).collect(Collectors.toList());
    }

    //  CREATE METHODS

    public void createRatingUpdate(Rating rating) {
        RatingLogUpdate ratingUpdate = new RatingLogUpdate(rating);
        save(ratingUpdate);
    }

    public void createCommentUpdate(Rating rating, Comment comment) {
        CommentLogUpdate commentUpdate = new CommentLogUpdate(rating,comment);
        save(commentUpdate);
    }

    public void createUpdateCommentUpdate(Comment comment) {
        CommentLogUpdate commentLogUpdate = new CommentLogUpdate();
        commentLogUpdate.setUser(comment.getUser());
        commentLogUpdate.setComment(comment);
        commentLogUpdate.setUpdateString(comment.getUser().getUsername() + " has updated their comment with id " + comment.getId());
        commentLogUpdate.setClassified(true);
        save(commentLogUpdate);
    }

    public void save(LogUpdate logUpdate) {
        logUpdateRepo.save(logUpdate);
        System.out.println("Log updated");
    }
}

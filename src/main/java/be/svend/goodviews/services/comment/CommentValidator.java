package be.svend.goodviews.services.comment;

import be.svend.goodviews.models.Comment;
import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.User;
import be.svend.goodviews.repositories.CommentRepository;
import be.svend.goodviews.repositories.RatingRepository;
import be.svend.goodviews.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CommentValidator {

    CommentRepository commentRepo;
    UserRepository userRepo;
    RatingRepository ratingRepo;

    public CommentValidator(CommentRepository commentRepo, UserRepository userRepo, RatingRepository ratingRepo) {
        this.commentRepo = commentRepo;
        this.userRepo = userRepo;
        this.ratingRepo = ratingRepo;
    }

    public Optional<Comment> isExistingComment(Comment comment) {
        if (comment == null || comment.getId() == null) return Optional.empty();

        return commentRepo.findById(comment.getId());
    }

    public Optional<User> hasExistingUser(Comment comment) {
        if (comment.getUser() == null || comment.getUser().getUsername() == null) return Optional.empty();
        return userRepo.findByUsername(comment.getUser().getUsername());
    }

    public Optional<Rating> hasExistingRating(Comment comment) {
        if (comment.getRating() == null || comment.getRating().getId() == null) return Optional.empty();
        return ratingRepo.findById(comment.getRating().getId());
    }
}

package be.svend.goodviews.services.comment;

import be.svend.goodviews.models.Comment;
import be.svend.goodviews.models.Rating;
import be.svend.goodviews.repositories.CommentRepository;
import be.svend.goodviews.repositories.RatingRepository;
import be.svend.goodviews.repositories.UserRepository;
import be.svend.goodviews.services.rating.RatingService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {
    CommentRepository commentRepo;
    RatingRepository ratingRepo;
    UserRepository userRepo;

    RatingService ratingService; // Need ratingService to add or delete a comment from a Rating

    // CONSTRUCTORS

    public CommentService(CommentRepository commentRepo,
                          RatingRepository ratingRepo,
                          UserRepository userRepo,
                          RatingService ratingService) {
        this.commentRepo = commentRepo;
        this.ratingRepo = ratingRepo;
        this.userRepo = userRepo;
        this.ratingService = ratingService;
    }

    // FIND METHODS

    public Optional<Comment> findById(Long id) {
        return commentRepo.findById(id);
    }

    public List<Comment> findByRatingId(String ratingId) {
        Optional<Rating> ratingInDb = ratingRepo.findById(ratingId);
        if (ratingInDb.isEmpty()) return Collections.emptyList();

        return ratingInDb.get().getCommentList().stream().sorted(Comparator.comparing(c -> c.getDateTime())).collect(Collectors.toList());
    }

    public List<Comment> findByUsername(String username) {
        return commentRepo.findAllByUser_Username(username);
    }

    // CREATE METHODS

    public Optional<Comment> createNewComment(Comment comment, String ratingId) {
        System.out.println("Trying to create new comment");

        Optional<Rating> ratingToComment = ratingRepo.findById(ratingId);
        if (ratingToComment.isEmpty()) return Optional.empty();

        Comment validatedComment = initialiseAndValidateComment(comment);
        Optional<Comment> savedComment = saveComment(comment);

        ratingService.addCommentToRating(ratingToComment.get(),comment);
        return savedComment;
    }

    // UPDATE METHODS

    public Optional<Comment> updateComment(Comment comment) {
        Optional<Comment> existingComment = findById(comment.getId());
        if (existingComment.isEmpty()) return Optional.empty();

        Comment updatedComment = CommentMerger.updateCommentWithNewData(existingComment.get(),comment);
        return saveComment(updatedComment);
    }

    public List<Comment> deleteUserFromCommentsByUsername(String username) {
        List<Comment> commentsOfUser = findByUsername(username);

        for (Comment comment: commentsOfUser) {
            comment.setUser(null);
            commentRepo.save(comment);
        }

        return commentsOfUser;
    }

    // DELETE

    public boolean deleteComment(Comment comment) {
        // Find comment
        Optional<Comment> existingComment = findById(comment.getId());
        if (existingComment.isEmpty()) return false;

        // Deleting comment from Rating
        ratingService.deleteCommentFromRating(comment);

        // Deleting comment
        System.out.println("Deleting comment: " + comment.getComment());
        commentRepo.delete(comment);
        return true;
    }

    // INTERNAL

    private Optional<Comment> saveComment(Comment comment) {
        Comment savedComment = commentRepo.save(comment);
        return findById(savedComment.getId());
    }

    private Comment initialiseAndValidateComment(Comment comment) {
        comment.setDateTime(LocalDateTime.now());

        if (comment.getId() != null) comment.setId(null);

        return comment;
    }
}

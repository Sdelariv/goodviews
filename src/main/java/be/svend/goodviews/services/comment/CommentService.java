package be.svend.goodviews.services.comment;

import be.svend.goodviews.models.Comment;
import be.svend.goodviews.models.Rating;
import be.svend.goodviews.repositories.CommentRepository;
import be.svend.goodviews.repositories.RatingRepository;
import be.svend.goodviews.repositories.UserRepository;
import be.svend.goodviews.services.notification.CommentNotificationService;
import be.svend.goodviews.services.rating.RatingService;
import be.svend.goodviews.services.update.LogUpdateService;
import org.springframework.stereotype.Service;

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

    LogUpdateService logUpdateService; // To update the log

    CommentNotificationService commentNotificationService; // To send notifications

    // CONSTRUCTORS

    public CommentService(CommentRepository commentRepo,
                          RatingRepository ratingRepo,
                          UserRepository userRepo,
                          CommentNotificationService commentNotificationService,
                          LogUpdateService logUpdateService) {
        this.commentRepo = commentRepo;
        this.ratingRepo = ratingRepo;
        this.userRepo = userRepo;

        this.commentNotificationService = commentNotificationService;
        this.logUpdateService = logUpdateService;
    }

    // FIND METHODS

    public Optional<Comment> findById(Long id) {
        return commentRepo.findById(id);
    }

    public List<Comment> findByRatingId(String ratingId) {
        Optional<Rating> ratingInDb = ratingRepo.findById(ratingId);
        if (ratingInDb.isEmpty()) return Collections.emptyList();

        // Sorting by DateTime
        List<Comment> ratingComments = commentRepo.findAllByRating(ratingInDb.get());
        return ratingComments.stream().sorted(Comparator.comparing(c -> c.getDateTime())).collect(Collectors.toList());
    }

    public List<Comment> findByUsername(String username) {
        return commentRepo.findAllByUser_Username(username);
    }

    // CREATE METHODS

    /**
     * Creates a new Comment and adds it to the Rating based on its ratingId
     * @param comment - The new comment to be added to the db and linked to the rating
     * @param ratingId - the RatingId of the Rating object to which the comment needs to get added to its list
     * @return
     */
    public Optional<Comment> createNewComment(Comment comment, String ratingId) {
        System.out.println("Trying to create new comment");

        // Finding the rating to add a comment to
        Optional<Rating> ratingToComment = ratingRepo.findById(ratingId);
        if (ratingToComment.isEmpty()) {
            System.out.println("Invalid rating");
            return Optional.empty();
        }

        // Prepare and save comment
        Comment validatedComment = addDateTimeAndNullifyId(comment);
        Optional<Comment> savedComment = saveComment(validatedComment);

        // Linking the comment to the relevant Rating
        comment.setRating(ratingToComment.get());

        // Sending notifications + updating Log
        commentNotificationService.sendCommentNotification(savedComment.get());
        logUpdateService.createCommentUpdate(ratingToComment.get(),savedComment.get());
        return savedComment;
    }

    // UPDATE METHODS

    public Optional<Comment> updateComment(Comment comment) {
        // Finding comment
        Optional<Comment> existingComment = findById(comment.getId());
        if (existingComment.isEmpty()) return Optional.empty();

        // Updating comment
        Comment updatedComment = CommentMerger.updateCommentWithNewData(existingComment.get(),comment);
        logUpdateService.createUpdateCommentUpdate(existingComment.get());
        return saveComment(updatedComment);
    }

    /**
     * Looks for all the comments that a user made, and deletes the user from that comment
     * @param username
     * @return List<Comment> a list of all the comments, with the user removed
     */
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

        // Deleting comment
        System.out.println("Deleting comment: " + comment.getComment());
        logUpdateService.createGeneralLog(comment.getUser() + "'s comment was deleted");
        commentRepo.delete(comment);
        return true;
    }

    public void deleteComments(List<Comment> comments) {
        for (Comment comment: comments) {
            deleteComment(comment);
        }
    }


    public void deleteAllCommentsFromRating(Rating rating) {
        System.out.println("Deleting all comments from the rating");
        List<Comment> commentsOfRating = commentRepo.findAllByRating(rating);
        for (Comment comment: commentsOfRating) {
            logUpdateService.deleteCommentIdFromLog(comment);
            logUpdateService.createGeneralLog("Deleting comment of " + comment.getUser().getUsername() + " on " + rating.getUser().getUsername() + "'s rating");
            commentRepo.delete(comment);
        }
    }

    // INTERNAL

    private Optional<Comment> saveComment(Comment comment) {
        Comment savedComment = commentRepo.save(comment);
        return findById(savedComment.getId());
    }

    private Comment addDateTimeAndNullifyId(Comment comment) {
        comment.setDateTime(LocalDateTime.now());

        if (comment.getId() != null) comment.setId(null);

        return comment;
    }
}

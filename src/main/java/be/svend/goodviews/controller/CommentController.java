package be.svend.goodviews.controller;

import be.svend.goodviews.models.Comment;
import be.svend.goodviews.models.Rating;
import be.svend.goodviews.services.comment.CommentScrubber;
import be.svend.goodviews.services.comment.CommentService;
import be.svend.goodviews.services.comment.CommentValidator;
import be.svend.goodviews.services.rating.RatingValidator;
import be.svend.goodviews.services.users.UserValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static be.svend.goodviews.services.StringValidator.isValidString;

@RestController
@RequestMapping("/comment")
public class CommentController {
    CommentService commentService;

    CommentValidator commentValidator;
    UserValidator userValidator;
    RatingValidator ratingValidator;

    public CommentController(CommentService commentService, CommentValidator commentValidator,
                             UserValidator userValidator, RatingValidator ratingValidator) {
        this.commentService = commentService;
        this.commentValidator = commentValidator;
        this.userValidator = userValidator;
        this.ratingValidator = ratingValidator;
    }

    // FIND METHODS

    @GetMapping("/{id}")
    public ResponseEntity findCommentById(@PathVariable Long id) {
        System.out.println("FIND COMMENT BY ID CALLED for " + id);

        Optional<Comment> comment = commentService.findById(id);
        if (comment.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(comment.get());
    }

    @CrossOrigin
    @GetMapping("/findByRatingId")
    public ResponseEntity findCommentsByRatingId(@RequestParam String ratingId) {
        System.out.println("FIND COMMENTS BY RATING ID CALLED for " + ratingId);

        if (!isValidString(ratingId)) return ResponseEntity.badRequest().body("Invalid input format");

        if (ratingValidator.ratingIdInDatabase(ratingId).isEmpty()) return ResponseEntity.status(404).body("Rating not in database");

        List<Comment> commentsOfRating = commentService.findByRatingId(ratingId);
        commentsOfRating = CommentScrubber.scrubUsers(commentsOfRating);

        return ResponseEntity.ok(commentsOfRating);
    }

    @GetMapping("/findByUsername")
    public ResponseEntity findCommentsByUsername(@RequestParam String username) {
        System.out.println("FIND COMMENTS BY USERNAME CALLED for " + username);

        if (!isValidString(username)) return ResponseEntity.badRequest().body("Invalid input format");

        if (userValidator.isExistingUserWithUsername(username).isEmpty()) return ResponseEntity.status(404).body("No user with that username");

        List<Comment> commentsOfRating = commentService.findByUsername(username);
        if (commentsOfRating.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(commentsOfRating);
    }

    // CREATE METHODS

    @CrossOrigin
    @PostMapping("/create")
    public ResponseEntity createNewComment(@RequestBody Comment comment) {
        System.out.println("CREATE NEW COMMENT CALLED for " + comment);

        // Validate comment
        if (commentValidator.hasExistingUser(comment).isEmpty()) return ResponseEntity.status(400).body("Invalid user");
        if (commentValidator.isExistingComment(comment).isPresent()) return ResponseEntity.status(400).body("Comment already exists");
        Optional<Rating> foundRating = commentValidator.hasExistingRating(comment);
        if (foundRating.isEmpty()) return ResponseEntity.status(400).body("Invalid rating");
        comment.setRating(foundRating.get());

        // TODO: Check if the comment is made by the user, or is it added automatically?

        // Save comment
        Optional<Comment> savedComment = commentService.createNewComment(comment);
        if (savedComment.isEmpty()) return ResponseEntity.status(500).body("Something went wrong saving the comment");

        return ResponseEntity.ok(savedComment.get());
    }

    // UPDATE METHODS

    @PostMapping("/update")
    public ResponseEntity updateComment(@RequestBody Comment comment) {
        System.out.println("UPDATE COMMENT CALLED for " + comment);

        if (commentValidator.isExistingComment(comment).isEmpty()) return ResponseEntity.status(400).body("Comment to be updated does not exist");
        if (commentValidator.hasExistingUser(comment).isEmpty()) return ResponseEntity.status(400).body("Invalid user");
        if (commentValidator.hasExistingRating(comment).isEmpty()) return ResponseEntity.status(400).body("Invalid rating");

        // Update comment
        Optional<Comment> savedComment = commentService.updateComment(comment);
        if (savedComment.isEmpty()) return ResponseEntity.status(500).body("Something went wrong updating the comment");

        return ResponseEntity.ok(savedComment.get());
    }

    // DELETE METHODS

    @DeleteMapping("{commentId}")
    public ResponseEntity deleteCommentByCommentId(@PathVariable Long commentId) {
        System.out.println("DELETE COMMENT BY COMMENT ID CALLED for " + commentId);

        Optional<Comment> foundComment = commentService.findById(commentId);
        if (foundComment.isEmpty()) return ResponseEntity.status(400).body("Invalid commentId");

        if (!commentService.deleteComment(foundComment.get())) return ResponseEntity.status(500).body("Something went wrong with the deletion");

        return ResponseEntity.ok().body("Succesfully deleted comment");
    }
}

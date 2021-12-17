package be.svend.goodviews.services.comment;

import be.svend.goodviews.models.Comment;
import be.svend.goodviews.repositories.CommentRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CommentValidator {

    CommentRepository commentRepo;

    public CommentValidator(CommentRepository commentRepo) {
        this.commentRepo = commentRepo;
    }

    public Optional<Comment> isExistingComment(Comment comment) {
        if (comment == null || comment.getId() == null) return Optional.empty();

        return commentRepo.findById(comment.getId());
    }
}

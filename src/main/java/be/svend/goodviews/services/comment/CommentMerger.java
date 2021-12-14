package be.svend.goodviews.services.comment;



import be.svend.goodviews.models.Comment;

import java.time.LocalDate;
import java.util.Optional;

public class CommentMerger {

    public static Comment updateCommentWithNewData(Comment existingComment, Comment comment) {
        Comment mergedComment = existingComment;

        existingComment.setUser(comment.getUser());
        existingComment.setUpdated(LocalDate.now());
        existingComment.setComment(comment.getComment());

        return mergedComment;
    }
}

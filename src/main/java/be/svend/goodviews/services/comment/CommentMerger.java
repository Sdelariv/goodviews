package be.svend.goodviews.services.comment;



import be.svend.goodviews.models.Comment;

import java.time.LocalDateTime;

public class CommentMerger {

    /**
     * Adds data of a comment to an existingComment (and adds the current localDateTime as the updated datetime)
     * @param existingComment - the comment used to update
     * @param comment - the comment from which the user and comment will get extracted
     * @return Comment - the merged comment
     */
    public static Comment updateCommentWithNewData(Comment existingComment, Comment comment) {
        Comment mergedComment = existingComment;

        existingComment.setUser(comment.getUser());
        existingComment.setUpdated(LocalDateTime.now());
        existingComment.setComment(comment.getComment());

        return mergedComment;
    }
}

package be.svend.goodviews.services.comment;

import be.svend.goodviews.models.Comment;
import be.svend.goodviews.services.users.UserScrubber;

import java.util.List;

public class CommentScrubber {

    public static List<Comment> scrubUsers(List<Comment> commentList) {
        for (Comment comment: commentList) {
            comment.setUser(UserScrubber.scrubAllExceptUsername(comment.getUser()));
        }
        return commentList;
    }
}

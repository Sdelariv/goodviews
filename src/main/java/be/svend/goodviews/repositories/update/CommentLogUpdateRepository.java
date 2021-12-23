package be.svend.goodviews.repositories.update;

import be.svend.goodviews.models.Comment;
import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.update.CommentLogUpdate;
import be.svend.goodviews.models.update.LogUpdate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentLogUpdateRepository extends JpaRepository<CommentLogUpdate, Long> {

    List<CommentLogUpdate> findByRating(Rating rating);

    List<CommentLogUpdate> findByComment(Comment comment);
}

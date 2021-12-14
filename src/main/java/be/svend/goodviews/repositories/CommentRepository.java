package be.svend.goodviews.repositories;

import be.svend.goodviews.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByUser_Username(String username);
}

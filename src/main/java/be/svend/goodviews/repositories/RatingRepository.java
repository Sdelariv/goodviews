package be.svend.goodviews.repositories;

import be.svend.goodviews.models.Comment;
import be.svend.goodviews.models.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, String> {

    List<Rating> findByFilm_Id(String filmId);

    List<Rating> findByUser_Username(String userId);

    Optional<Rating> findRatingByCommentListContaining(Comment comment);
}

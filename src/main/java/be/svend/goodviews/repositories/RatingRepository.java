package be.svend.goodviews.repositories;

import be.svend.goodviews.models.Comment;
import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, String> {

    List<Rating> findByFilm_Id(String filmId);

    List<Rating> findByUser_Username(String userId);

    List<Rating> findByUserLikesContaining(User user);

    Integer countRatingsByUser_Username(String userId);

    List<Rating> findTop3ByOrderByDateOfRating();
}

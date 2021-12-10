package be.svend.goodviews.repositories;

import be.svend.goodviews.models.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, String> {

    List<Rating> findByFilm_Id(String filmId);
}

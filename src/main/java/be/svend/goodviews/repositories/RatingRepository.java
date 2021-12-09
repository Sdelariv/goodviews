package be.svend.goodviews.repositories;

import be.svend.goodviews.models.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}

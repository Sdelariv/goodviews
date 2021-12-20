package be.svend.goodviews.repositories.update;

import be.svend.goodviews.models.Rating;

import be.svend.goodviews.models.update.RatingLogUpdate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingLogUpdateRepository extends JpaRepository<RatingLogUpdate, Long> {

    List<RatingLogUpdate> findByRating(Rating rating);
}

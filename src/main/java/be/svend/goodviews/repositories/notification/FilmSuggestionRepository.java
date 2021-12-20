package be.svend.goodviews.repositories.notification;


import be.svend.goodviews.models.notification.FilmSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FilmSuggestionRepository extends JpaRepository<FilmSuggestion,Long> {

    Optional<FilmSuggestion> findBySuggestedFilmId(String suggestedFilmId);
}

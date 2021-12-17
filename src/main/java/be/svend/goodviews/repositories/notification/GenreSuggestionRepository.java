package be.svend.goodviews.repositories.notification;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.notification.GenreSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreSuggestionRepository extends JpaRepository<GenreSuggestion,Long> {

    Optional<GenreSuggestion> findByFilmAndSuggestedGenreName(Film film, String suggestedGenreName);
}

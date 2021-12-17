package be.svend.goodviews.repositories.notification;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.notification.TagSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagSuggestionRepository extends JpaRepository<TagSuggestion, Long> {

    Optional<TagSuggestion> findByFilmAndSuggestedTagName(Film film, String suggestedTag);
}

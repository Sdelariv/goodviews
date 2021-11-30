package be.svend.goodviews.repositories;

import be.svend.goodviews.models.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}

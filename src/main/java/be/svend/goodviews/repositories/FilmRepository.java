package be.svend.goodviews.repositories;

import be.svend.goodviews.models.Film;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmRepository extends JpaRepository<Film, String> {
}

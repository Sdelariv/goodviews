package be.svend.goodviews.repositories;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilmRepository extends JpaRepository<Film, String> {

    List<Film> findFilmsByDirectorContaining(Person person);

    List<Film> findFilmsByWriterContaining(Person person);
}

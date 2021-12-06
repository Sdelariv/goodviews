package be.svend.goodviews.repositories;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.Genre;
import be.svend.goodviews.models.Person;
import be.svend.goodviews.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilmRepository extends JpaRepository<Film, String> {

    List<Film> findFilmsByDirectorContaining(Person person);

    List<Film> findFilmsByWriterContaining(Person person);

    List<Film> findByGenre(Genre genre);

    List<Film> findByTag(Tag tag);

    List<Film> findByTags(List<Tag> tags);

    List<Film> findByGenres(List<Genre> genres);
}

package be.svend.goodviews.repositories;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.Genre;
import be.svend.goodviews.models.Person;
import be.svend.goodviews.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface FilmRepository extends JpaRepository<Film, String> {

    List<Film> findFilmsByDirectorContaining(Person person);

    List<Film> findFilmsByWriterContaining(Person person);

    List<Film> findAllByGenresContaining(Genre genre);

    List<Film> findAllByGenresIn(List<Genre> genres);

    List<Film> findAllByTagsContaining(Tag tag);

    List<Film> findAllByTagsIn(List<Tag> tags);

    List<Film> findFilmsByTitle(String title);

    List<Film> findByTranslatedTitle(String title);
}

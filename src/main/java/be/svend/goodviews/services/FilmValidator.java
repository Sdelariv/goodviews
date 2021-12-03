package be.svend.goodviews.services;

import be.svend.goodviews.models.Film;
import org.springframework.stereotype.Component;

@Component
public class FilmValidator {
    PersonService personService;
    GenreService genreService;
    TagService tagService;

    public FilmValidator(PersonService directorService, GenreService genreService, TagService tagService) {
        this.personService = directorService;
        this.genreService = genreService;
        this.tagService = tagService;
    }

    public Film validate(Film film) {
        // Fetches or saves the genres, tags and directors
        film.setGenres(genreService.saveGenres(film.getGenres()));
        film.setTags(tagService.saveTags(film.getTags()));
        film.setDirector(personService.saveDirectors(film.getDirector()));

        return film;
    }
}

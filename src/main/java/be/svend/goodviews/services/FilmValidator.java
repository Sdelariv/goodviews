package be.svend.goodviews.services;

import be.svend.goodviews.models.Film;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FilmValidator {
    DirectorService directorService;
    GenreService genreService;
    TagService tagService;

    public FilmValidator(DirectorService directorService, GenreService genreService, TagService tagService) {
        this.directorService = directorService;
        this.genreService = genreService;
        this.tagService = tagService;
    }

    public Film validate(Film film) {
        // Fetches or saves the genres, tags and directors
        film.setGenres(genreService.saveGenres(film.getGenres()));
        film.setTags(tagService.saveTags(film.getTags()));
        film.setDirector(directorService.saveDirectors(film.getDirector()));

        return film;
    }
}

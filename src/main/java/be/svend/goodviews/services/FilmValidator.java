package be.svend.goodviews.services;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.repositories.FilmRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FilmValidator {
    PersonService personService;
    GenreService genreService;
    TagService tagService;
    FilmRepository filmRepo;

    public FilmValidator(PersonService directorService, GenreService genreService, TagService tagService, FilmRepository filmRepo) {
        this.personService = directorService;
        this.genreService = genreService;
        this.tagService = tagService;
        this.filmRepo = filmRepo;
    }

    public Film initialise(Film film) {
        // Fetches or saves the genres, tags and directors
        film.setGenres(genreService.saveGenres(film.getGenres()));
        film.setTags(tagService.saveTags(film.getTags()));
        film.setDirector(personService.savePersons(film.getDirector()));
        film.setWriter(personService.savePersons(film.getWriter()));

        return film;
    }


}

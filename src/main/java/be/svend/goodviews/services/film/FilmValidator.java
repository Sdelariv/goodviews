package be.svend.goodviews.services.film;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.repositories.FilmRepository;
import be.svend.goodviews.services.film.properties.GenreService;
import be.svend.goodviews.services.crew.PersonService;
import be.svend.goodviews.services.film.properties.TagService;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class FilmValidator {
    PersonService personService;
    GenreService genreService;
    TagService tagService;
    FilmRepository filmRepo;


    public FilmValidator(PersonService directorService,
                         GenreService genreService,
                         TagService tagService,
                         FilmRepository filmRepo) {
        this.personService = directorService;
        this.genreService = genreService;
        this.tagService = tagService;
        this.filmRepo = filmRepo;
    }

    /**
     * Fetches, or saves + fetches the genres, tags, directors and writers from the database
     * Returns the film with the properties from the database
     * @param film - the film that needs initialising
     * @return Film with its properties saved and fetched from the database
     */
    public Film initialise(Film film) {
        film.setGenres(genreService.saveGenres(film.getGenres()));
        film.setTags(tagService.saveTags(film.getTags()));
        film.setDirector(personService.createPersons(film.getDirector()));
        film.setWriter(personService.createPersons(film.getWriter()));

        return film;
    }

    public static boolean filmHasValidIdFormat(Film film) {
        if (film.getId() == null) return false;

        if (!isValidFilmIdFormat(film.getId())) return false;

        return true;
    }

    public static boolean isValidFilmIdFormat(String id) {
        if (id == null) return false;
        if (id.contains(";")) return false;

        return id.startsWith("tt");
    }

    public Optional<Film> isExistingFilm(Film film) {
        if (film == null) return Optional.empty();
        if (film.getId() == null) return Optional.empty();

        return filmRepo.findById(film.getId());
    }

    public Optional<Film> isExistingFilmId(String filmId) {
        if (filmId == null) return Optional.empty();

        return filmRepo.findById(filmId);
    }

}

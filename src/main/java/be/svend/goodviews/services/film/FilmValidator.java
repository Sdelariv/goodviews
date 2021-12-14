package be.svend.goodviews.services.film;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.services.GenreService;
import be.svend.goodviews.services.crew.PersonService;
import be.svend.goodviews.services.TagService;
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
        return id.startsWith("tt");
    }


}

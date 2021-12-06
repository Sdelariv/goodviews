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

    /**
     * Fetches, or saves + fetches the genres, tags, directors and writers from the database
     * Returns the film with the properties from the database
     * @param film - the film that needs initialising
     * @return Film with its properties saved and fetched from the database
     */
    public Film initialise(Film film) {
        film.setGenres(genreService.saveGenres(film.getGenres()));
        film.setTags(tagService.saveTags(film.getTags()));
        film.setDirector(personService.savePersons(film.getDirector()));
        film.setWriter(personService.savePersons(film.getWriter()));

        return film;
    }

    public boolean hasValidIdFormat(Film film) {
        if (film.getId() == null) return false;

        if (!film.getId().startsWith("tt")) return false;

        return true;
    }


}

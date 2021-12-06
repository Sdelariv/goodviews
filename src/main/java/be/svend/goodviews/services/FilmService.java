package be.svend.goodviews.services;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.Genre;
import be.svend.goodviews.models.Person;
import be.svend.goodviews.models.Tag;
import be.svend.goodviews.repositories.FilmRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    FilmRepository filmRepo;
    FilmValidator filmValidator;
    PersonService personService;

    public FilmService(FilmRepository filmRepo, FilmValidator filmValidator, PersonService personService) {
        this.filmRepo = filmRepo;
        this.filmValidator = filmValidator;
        this.personService = personService;
    }

    // FIND methods

    public Optional<Film> findById(String id) {
        if (id == null) return Optional.empty();

        Optional<Film> foundFilm = filmRepo.findById(id);

        if (foundFilm.isEmpty()) return Optional.empty();

        return foundFilm;
    }

    public List<Film> findByGenre(Genre genre) {
        return filmRepo.findAllByGenresContaining(genre);
    }

    public List<Film> findByGenres(List<Genre> genres) {
        return filmRepo.findAllByGenresIn(genres);
    }

    public List<Film> findByTag(Tag tag) {
        return filmRepo.findAllByTagsContaining(tag);
    }

    public List<Film> findByTags(List<Tag> tags) {
        return filmRepo.findAllByTagsIn(tags);
    }


    public List<Film> findFilmsByDirectorId(String directorId) {
        if (directorId == null) return Collections.emptyList();

        Optional<Person> director = personService.findPersonById(directorId);
        if (director.isEmpty()) return Collections.emptyList();


        List<Film> filmsByDirector = filmRepo.findFilmsByDirectorContaining(director.get());

        return filmsByDirector;
    }

    public List<Film> findFilmsByWriterId(String writerId) {
        if (writerId == null) return Collections.emptyList();

        Optional<Person> director = personService.findPersonById(writerId);
        if (director.isEmpty()) return Collections.emptyList();


        List<Film> filmsByDirector = filmRepo.findFilmsByWriterContaining(director.get());

        return filmsByDirector;
    }


    public List<Film> findByTitle(String name) {

        List<Film> foundFilms = filmRepo.findFilmsByTitle(name);
        foundFilms.addAll(filmRepo.findByTranslatedTitle(name));

        return foundFilms;
    }

    // CREATE methods

    public void createFilms(List<Film> films) {

        for (Film film: films) {
            createFilm(film);
        }
    }

    public void createFilm(Film film) {
        if (findFilmByFilm(film).isPresent()) {
            System.out.println("Can't create a film with existing id");
            return;
        }

        if (!filmValidator.hasValidIdFormat(film)) {
            System.out.println("Can't create a film that doesn't have a valid id format: " + film.getId());
            return;
        }

        Film initialisedFilm = filmValidator.initialise(film);

        filmRepo.save(initialisedFilm);
        System.out.println("Saved the following Film:");
        System.out.println(film);
    }

    // UPDATE methods

    public void updateFilms(List<Film> films) {

        for (Film film: films) {
            updateFilm(film);
        }
    }

    public void updateFilm(Film film) {
        Optional<Film> existingFilm = findFilmByFilm(film);

        if (existingFilm.isEmpty()) {
            System.out.println("Can't update a film with id not in database");
            return;
        } else {
            film = filmValidator.initialise(film); // Makes sure that the properties are fetched or saved to db

            filmRepo.save(existingFilm.get());
            System.out.println("Saved the following Film:");
            System.out.println(film);
        }
    }

    // DELETE methods

    public void deleteFilms(List<Film> films) {

        for (Film film: films) {
            deleteFilm(film);
        }
    }

    public void deleteFilm(Film film) {
        Optional<Film> existingFilm = findFilmByFilm(film);

        if (existingFilm.isEmpty()) {
            System.out.println("Can't delete a film with id not in database");
            return;
        } else {
            System.out.println("Deleting " + film.getTitle());
            filmRepo.deleteById(film.getId());
        }
    }



    // INTERNAL

    private Optional<Film> findFilmByFilm(Film film) {
        return findById(film.getId());
    }

}

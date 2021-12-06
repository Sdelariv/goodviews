package be.svend.goodviews.services;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.repositories.FilmRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FilmService {
    FilmRepository filmRepo;
    FilmValidator filmValidator;

    public FilmService(FilmRepository filmRepo, FilmValidator filmValidator) {
        this.filmRepo = filmRepo;
        this.filmValidator = filmValidator;
    }

    public Optional<Film> findById(String id) {
        if (id == null) return Optional.empty();

        Optional<Film> foundFilm = filmRepo.findById(id);

        if (foundFilm.isEmpty()) return Optional.empty();

        return foundFilm;
    }

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
            filmRepo.save(existingFilm.get());
            System.out.println("Saved the following Film:");
            System.out.println(film);
        }
    }

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

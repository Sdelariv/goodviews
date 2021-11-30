package be.svend.goodviews.services;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.repositories.FilmRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FilmService {
    FilmRepository filmRepo;
    FilmValidator filmValidator;

    public FilmService(FilmRepository filmRepo, FilmValidator filmValidator) {
        this.filmRepo = filmRepo;
        this.filmValidator = filmValidator;
    }

    public void saveFilms(List<Film> films) {
        for (Film film: films) {
            saveFilm(film);
        }
    }

    public void saveFilm(Film film) {
        Film validatedFilm = filmValidator.validate(film);

        filmRepo.save(validatedFilm);
        System.out.println("Saved the following Film:");
        System.out.println(film);
    }

}

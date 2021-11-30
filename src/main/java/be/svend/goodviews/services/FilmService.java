package be.svend.goodviews.services;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.repositories.FilmRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FilmService {
    FilmRepository filmRepo;
    DirectorService directorService;
    GenreService genreService;
    TagService tagService;

    public FilmService(FilmRepository filmRepo, DirectorService directorService, GenreService genreService, TagService tagService) {
        this.filmRepo = filmRepo;
        this.directorService = directorService;
        this.genreService = genreService;
        this.tagService = tagService;
    }

    public void saveFilms(List<Film> films) {
        for (Film film: films) {
            saveFilm(film);
        }
    }

    public void saveFilm(Film film) {
        film.setGenres(genreService.saveGenres(film.getGenres()));
        film.setTags(tagService.saveTags(film.getTags()));
        film.setDirector(directorService.saveDirectors(film.getDirector()));

        filmRepo.save(film);
        System.out.println("Saved the following Film:");
        System.out.println(film);
    }

}

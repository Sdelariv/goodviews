package be.svend.goodviews.controller;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.services.film.FilmService;
import be.svend.goodviews.services.film.FilmValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static be.svend.goodviews.services.film.FilmValidator.isValidFilmIdFormat;

@RestController
@RequestMapping("/film")
public class FilmController {
    FilmService filmService;
    FilmValidator filmValidator;

    public FilmController(FilmService filmService, FilmValidator filmValidator) {
        this.filmService = filmService;
        this.filmValidator = filmValidator;
    }

    // FIND METHODS

    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable String id) {
        System.out.println("FIND BY ID called for: " + id);

        if (!isValidFilmIdFormat(id)) return ResponseEntity.badRequest().body("Invalid id format");

        Optional<Film> foundFilm = filmService.findById(id);

        if (foundFilm.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(foundFilm.get());
    }

}

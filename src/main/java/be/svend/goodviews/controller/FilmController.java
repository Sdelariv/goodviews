package be.svend.goodviews.controller;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.Genre;
import be.svend.goodviews.models.Person;
import be.svend.goodviews.models.Tag;
import be.svend.goodviews.repositories.PersonRepository;
import be.svend.goodviews.services.crew.PersonService;
import be.svend.goodviews.services.crew.PersonValidator;
import be.svend.goodviews.services.film.FilmService;
import be.svend.goodviews.services.film.FilmValidator;
import be.svend.goodviews.services.film.properties.GenreService;
import be.svend.goodviews.services.film.properties.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static be.svend.goodviews.services.film.FilmValidator.isValidFilmIdFormat;

@RestController
@RequestMapping("/film")
public class FilmController {
    FilmService filmService;
    FilmValidator filmValidator;

    PersonValidator personValidator;
    PersonService personService;

    TagService tagService;
    GenreService genreService;

    public FilmController(FilmService filmService, FilmValidator filmValidator,
                          PersonValidator personValidator, PersonService personService,
                          TagService tagService, GenreService genreService) {
        this.filmService = filmService;
        this.filmValidator = filmValidator;

        this.personValidator = personValidator;
        this.personService = personService;

        this.tagService = tagService;
        this.genreService = genreService;
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

    @GetMapping("/findByTitle")
    public ResponseEntity findByTitle(@RequestParam String title) {
        System.out.println("FIND BY TITLE CALLED with: " + title);

        // TODO: Validate string?

        List<Film> foundFilms = filmService.findByTitle(title);

        if (foundFilms.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(foundFilms);
    }

    @GetMapping("/findByTag")
    public ResponseEntity findByTagName(@RequestParam String tagName) {
        System.out.println("FIND BY TAG NAME CALLED with: " + tagName);

        // TODO: validate string?

        Optional<Tag> foundTag = tagService.findByName(tagName);
        if (foundTag.isEmpty()) return ResponseEntity.status(404).body("No such tag in the database");

        List<Film> foundFilms = filmService.findByTag(foundTag.get());
        if (foundFilms.isEmpty()) return ResponseEntity.status(404).body("No films with that tag");

        return ResponseEntity.ok(foundFilms);
    }

    @GetMapping("/findByGenre")
    public ResponseEntity findByGenreName(@RequestParam String genreName) {
        System.out.println("FIND BY GENRE NAME CALLED with: " + genreName);

        // TODO: validate string?

        Optional<Genre> foundGenre = genreService.findByName(genreName);
        if (foundGenre.isEmpty()) return ResponseEntity.status(404).body("No such genre in the database");

        List<Film> foundFilms = filmService.findByGenre(foundGenre.get());
        if (foundFilms.isEmpty()) return ResponseEntity.status(404).body("No films with that genre");

        return ResponseEntity.ok(foundFilms);
    }

    @GetMapping("/findByPerson")
    public ResponseEntity findByPerson(@RequestBody Person person) {
        System.out.println("FIND BY PERSON CALLED with: " + person);

        Optional<Person> foundPerson = personValidator.isExistingPerson(person);
        if (foundPerson.isEmpty()) return ResponseEntity.notFound().build();

        List<Film> filmsInvolvingPerson = filmService.findFilmsByPersonInvolved(person);

        if (filmsInvolvingPerson.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(filmsInvolvingPerson.stream().distinct().collect(Collectors.toList()));
    }

  @GetMapping("/findByPersonName")
  public ResponseEntity findByPersonName(@RequestParam String name) {
      System.out.println("FIND BY PERSON-NAME CALLED with: " + name);

      // TODO: Validate string?

      List<Person> foundPersons = personService.FindPersonsByName(name);
      if (foundPersons.isEmpty()) return ResponseEntity.notFound().build();

      List<Film> filmsInvolvingPerson = new ArrayList<>();
      for (Person person: foundPersons) {
          filmsInvolvingPerson.addAll(filmService.findFilmsByPersonInvolved(person));
      }

      if (filmsInvolvingPerson.isEmpty()) return  ResponseEntity.notFound().build();

      return ResponseEntity.ok(filmsInvolvingPerson);
  }

    @GetMapping("/findAll")
    public ResponseEntity findAll() {
        System.out.println("FIND ALL CALLED");

        List<Film> foundFilms = filmService.findAllFilms();
        if (foundFilms.isEmpty()) return ResponseEntity.status(500).body("Something went wrong fetching the films");

        return ResponseEntity.ok(foundFilms);
    }

    // CREATE METHODS

    // UPDATE METHODS

    // DELETE METHODS

    // TODO: Delete ratings with ratingService, then films with filmService

}

package be.svend.goodviews.controller;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.Genre;
import be.svend.goodviews.models.Person;
import be.svend.goodviews.models.Tag;
import be.svend.goodviews.services.crew.PersonService;
import be.svend.goodviews.services.crew.PersonValidator;
import be.svend.goodviews.services.film.FilmService;
import be.svend.goodviews.services.film.FilmValidator;
import be.svend.goodviews.services.film.properties.GenreService;
import be.svend.goodviews.services.film.properties.TagService;
import be.svend.goodviews.services.rating.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static be.svend.goodviews.services.StringValidator.isValidString;
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
    RatingService ratingService;

    public FilmController(FilmService filmService, FilmValidator filmValidator,
                          PersonValidator personValidator, PersonService personService,
                          TagService tagService, GenreService genreService, RatingService ratingService) {
        this.filmService = filmService;
        this.filmValidator = filmValidator;

        this.personValidator = personValidator;
        this.personService = personService;

        this.tagService = tagService;
        this.genreService = genreService;
        this.ratingService = ratingService;
    }

    // FIND METHODS

    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable String id) {
        System.out.println("FIND FILM BY ID called for: " + id);

        if (!isValidString(id)) return ResponseEntity.badRequest().body("Invalid id format");
        if (!isValidFilmIdFormat(id)) return ResponseEntity.badRequest().body("Invalid id format");

        Optional<Film> foundFilm = filmService.findById(id);

        if (foundFilm.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(foundFilm.get());
    }

    @GetMapping("/findByTitle")
    public ResponseEntity findByTitle(@RequestParam String title) {
        System.out.println("FIND BY TITLE CALLED with: " + title);

        if (!isValidString(title)) return ResponseEntity.badRequest().body("Invalid title");

        List<Film> foundFilms = filmService.findByTitle(title);

        if (foundFilms.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(foundFilms);
    }

    @CrossOrigin
    @GetMapping("/findByPartialTitle")
    public ResponseEntity findByPartialTitle(@RequestParam String partialTitle) {
        System.out.println("FIND BY PARTIAL TITLE CALLED with: " + partialTitle);

        if (!isValidString(partialTitle)) return ResponseEntity.badRequest().body("Invalid title");

        List<Film> foundFilms = filmService.findByPartialTitle(partialTitle);

        if (foundFilms.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(foundFilms.stream().distinct());
    }

    @GetMapping("/findByGenre")
    public ResponseEntity findByGenreName(@RequestParam String genreName) {
        System.out.println("FIND BY GENRE NAME CALLED with: " + genreName);

        if (!isValidString(genreName)) return ResponseEntity.badRequest().body("Invalid genreName");

        Optional<Genre> foundGenre = genreService.findByName(genreName);
        if (foundGenre.isEmpty()) return ResponseEntity.status(404).body("No such genre in the database");

        List<Film> foundFilms = filmService.findByGenre(foundGenre.get());
        if (foundFilms.isEmpty()) return ResponseEntity.status(404).body("No films with that genre");

        return ResponseEntity.ok(foundFilms);
    }

    @GetMapping("/findByTag")
    public ResponseEntity findByTagName(@RequestParam String tagName) {
        System.out.println("FIND BY TAG NAME CALLED with: " + tagName);

        if (!isValidString(tagName)) return ResponseEntity.badRequest().body("Invalid tagName");

        Optional<Tag> foundTag = tagService.findByName(tagName);
        if (foundTag.isEmpty()) return ResponseEntity.status(404).body("No such tag in the database");

        List<Film> foundFilms = filmService.findByTag(foundTag.get());
        if (foundFilms.isEmpty()) return ResponseEntity.status(404).body("No films with that tag");

        return ResponseEntity.ok(foundFilms);
    }

    @GetMapping("/findByPerson")
    public ResponseEntity findByPerson(@RequestBody Person person) {
        System.out.println("FIND BY PERSON CALLED with: " + person);

        Optional<Person> foundPerson = personValidator.isExistingPerson(person);
        if (foundPerson.isEmpty()) return ResponseEntity.status(404).body("No such person found");

        List<Film> filmsInvolvingPerson = filmService.findFilmsByPersonInvolved(person);

        if (filmsInvolvingPerson.isEmpty()) return ResponseEntity.status(404).body("No films found where that person was involved");
        return ResponseEntity.ok(filmsInvolvingPerson.stream().distinct().collect(Collectors.toList()));
    }

  @GetMapping("/findByPersonName")
  public ResponseEntity findByPersonName(@RequestParam String name) {
      System.out.println("FIND BY PERSON-NAME CALLED with: " + name);

      if (!isValidString(name)) return ResponseEntity.badRequest().body("Invalid name");

      List<Person> foundPersons = personService.findPersonsByName(name);
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

    @PostMapping("/createFromImdbId") // TODO: Needs to be changed to an object with the id?
    public ResponseEntity createFilmFromImdbId(@RequestParam String imdbId) {
        System.out.println("CREATE FILM FROM IMDB ID CALLED with: " + imdbId);

        if (!isValidString(imdbId)) return ResponseEntity.badRequest().body("Invalid id format");

        if (filmValidator.isExistingFilmId(imdbId).isPresent()) return ResponseEntity.badRequest().body("Film already in database");

        Optional<Film> createdFilm = filmService.createFilmByImdbId(imdbId);
        if (createdFilm.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(createdFilm.get());
    }

    // UPDATE METHODS

    @PostMapping("/updateFilmWithImdbData")
    public ResponseEntity updateFilmWithWebDataFromId(@RequestParam String imdbId) {
        System.out.println("UPDATE FILM FROM IMDB ID CALLED with: " + imdbId);

        if (!isValidString(imdbId)) return ResponseEntity.badRequest().body("Invalid id format");

        Optional<Film> foundFilm = filmValidator.isExistingFilmId(imdbId);
        if (foundFilm.isEmpty()) return ResponseEntity.badRequest().body("Film not in database");

        Optional<Film> createdFilm = filmService.updateFilmReplaceWithWebDataByImdbId(foundFilm.get());
        if (createdFilm.isEmpty()) return ResponseEntity.status(500).body("Something went wrong updating");

        return ResponseEntity.ok(createdFilm.get());
    }

    // DELETE METHODS

    @DeleteMapping("/delete")
    public ResponseEntity deleteFilm(@RequestBody Film film) {
        System.out.println("DELETE FILM CALLED for: " + film);

        Optional<Film> foundFilm = filmValidator.isExistingFilm(film);
        if (foundFilm.isEmpty()) return ResponseEntity.notFound().build();

        // Delete ratings
        if (!ratingService.deleteByFilmId(foundFilm.get().getId())) return ResponseEntity.status(500).body("Something went wrong deleting the ratings of the films");

        // Delete film
        filmService.deleteFilm(foundFilm.get());
        return ResponseEntity.ok().body("Film deleted");
    }

}

package be.svend.goodviews.controller;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.User;
import be.svend.goodviews.models.notification.FilmSuggestion;
import be.svend.goodviews.models.notification.GenreSuggestion;
import be.svend.goodviews.models.notification.Notification;
import be.svend.goodviews.models.notification.TagSuggestion;
import be.svend.goodviews.services.film.FilmValidator;
import be.svend.goodviews.services.notification.SuggestionService;
import be.svend.goodviews.services.users.UserValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static be.svend.goodviews.services.StringValidator.isValidString;

@RestController
@RequestMapping("/suggestion")
public class SuggestionController {
    SuggestionService suggestionService;

    FilmValidator filmValidator;
    UserValidator userValidator;

    public SuggestionController(SuggestionService suggestionService,
                                FilmValidator filmValidator, UserValidator userValidator) {
        this.suggestionService = suggestionService;
        this.filmValidator = filmValidator;
        this.userValidator = userValidator;
    }

    // FIND METHODS

    @GetMapping("/adminList")
    public ResponseEntity findAllSuggestionsForAdmin() {
        System.out.println("FIND ALL SUGGESTIONS FOR ADMIN CALLED");

        List<Notification> foundSuggestions = suggestionService.findAllAdminNotifications();
        if (foundSuggestions.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(foundSuggestions);
    }

    @GetMapping("/genre")
    public ResponseEntity findAllGenreSuggestions() {
        System.out.println("FIND ALL GENRE SUGGESTIONS CALLED");

        List<GenreSuggestion> foundSuggestions = suggestionService.findAllGenreSuggestions();
        if (foundSuggestions.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(foundSuggestions);
    }

    @GetMapping("/tag")
    public ResponseEntity findAllTagSuggestions() {
        System.out.println("FIND ALL TAG SUGGESTIONS CALLED");

        List<TagSuggestion> foundSuggestions = suggestionService.findAllTagSuggestions();
        if (foundSuggestions.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(foundSuggestions);
    }

    @GetMapping("/film")
    public ResponseEntity findAllFilmSuggestions() {
        System.out.println("FIND ALL FILM SUGGESTIONS CALLED");

        List<FilmSuggestion> foundSuggestions = suggestionService.findAllFilmSuggestions();
        if (foundSuggestions.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(foundSuggestions);
    }

    // CREATE METHODS

    @PostMapping("/suggestGenre")
    public ResponseEntity createGenreSuggestion(@RequestParam String suggestedGenreName, @RequestParam String filmId, @RequestParam String suggesterUsername) {
        System.out.println("CREATE GENRE SUGGESTION CALLED for " + filmId + " and genre: " + suggestedGenreName);

        if (!isValidString(suggestedGenreName) || !isValidString(filmId) || !isValidString(suggesterUsername))
            return ResponseEntity.badRequest().body("Invalid input");

        Optional<User> suggester = userValidator.isExistingUserWithUsername(suggesterUsername);
        if (suggester.isEmpty()) return ResponseEntity.status(400).body("No such user found");

        Optional<Film> film = filmValidator.isExistingFilmId(filmId);
        if (film.isEmpty()) return ResponseEntity.status(400).body("No such film found");

        if (!suggestionService.sendGenreSuggestion(suggestedGenreName, film.get(), suggester.get()))
            return ResponseEntity.badRequest().body("Suggestion invalid (film already has that genre or suggestion already exists");
        return ResponseEntity.ok().body("Genre suggestion sent");
    }

    @PostMapping("/suggestTag")
    public ResponseEntity createTagSuggestion(@RequestParam String suggestedTagName, @RequestParam String filmId, @RequestParam String suggesterUsername) {
        System.out.println("CREATE TAG SUGGESTION CALLED for " + filmId + " and genre: " + suggestedTagName);

        if (!isValidString(suggestedTagName) || !isValidString(filmId) || !isValidString(suggesterUsername))
            return ResponseEntity.badRequest().body("Invalid input");

        Optional<User> suggester = userValidator.isExistingUserWithUsername(suggesterUsername);
        if (suggester.isEmpty()) return ResponseEntity.status(400).body("No such user found");

        Optional<Film> film = filmValidator.isExistingFilmId(filmId);
        if (film.isEmpty()) return ResponseEntity.status(400).body("No such film found");

        if (!suggestionService.sendTagSuggestion(suggestedTagName, film.get(), suggester.get()))
            return ResponseEntity.badRequest().body("Suggestion invalid (film already has that tag or suggestion already exists");
        return ResponseEntity.ok().body("Tag suggestion sent");
    }

    @PostMapping("/suggestFilm")
    public ResponseEntity createFilmSuggestion(@RequestParam String suggestedFilmId, @RequestParam String suggesterUsername) {
        System.out.println("CREATE FILM SUGGESTION CALLED for " + suggestedFilmId);

        if (!isValidString(suggestedFilmId) || !isValidString(suggesterUsername))
            return ResponseEntity.badRequest().body("Invalid input");

        Optional<User> suggester = userValidator.isExistingUserWithUsername(suggesterUsername);
        if (suggester.isEmpty()) return ResponseEntity.status(400).body("No such user found");

        // Check if film is already in Db
        suggestedFilmId = suggestedFilmId.trim();
        if (filmValidator.isExistingFilmId(suggestedFilmId).isPresent()) return ResponseEntity.badRequest().body("Film already in db");

        if (!suggestionService.sendFilmSuggestion(suggestedFilmId, suggester.get()))
            return ResponseEntity.badRequest().body("Suggestion invalid (film doesn't exist or suggestion already exists)");
        return ResponseEntity.ok().body("Film suggestion sent");
    }

    // UPDATE METHODS


    // TODO: continue
}


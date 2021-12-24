package be.svend.goodviews.controller;

import be.svend.goodviews.models.Rating;
import be.svend.goodviews.services.film.FilmService;
import be.svend.goodviews.services.rating.RatingService;
import be.svend.goodviews.services.rating.RatingValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static be.svend.goodviews.services.StringValidator.isValidString;
import static be.svend.goodviews.services.film.FilmValidator.isValidFilmIdFormat;
import static be.svend.goodviews.services.rating.RatingValidator.hasValidRatingValue;

@RestController
@RequestMapping("/rating")
public class RatingController {
    RatingService ratingService;
    RatingValidator ratingValidator;

    FilmService filmService; // To update the average rating of the film

    public RatingController(RatingService ratingService, RatingValidator ratingValidator,
                            FilmService filmService) {
        this.ratingService = ratingService;
        this.ratingValidator = ratingValidator;

        this.filmService = filmService;
    }

    // FIND METHODS

    @CrossOrigin // TODO: Take away if no longer localhost
    @GetMapping("/{id}")
    public ResponseEntity findRatingById(@PathVariable String id) {
        System.out.println("FIND RATINGS BY ID FOR " + id);

        if (!isValidString(id)) return ResponseEntity.badRequest().body("Invalid input format");

        Optional<Rating> rating = ratingService.findById(id);
        if (rating.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(rating.get());
    }

    @GetMapping("/findByFilmId")
    public ResponseEntity findRatingsByFilmId(@RequestParam String filmId) {
        System.out.println("FIND RATING BY FILM ID CALLED with " + filmId);

        if (!isValidFilmIdFormat(filmId)) return ResponseEntity.badRequest().body("Invalid input format");

        List<Rating> filmRatings = ratingService.findByFilmId(filmId);
        if (filmRatings.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(filmRatings);
    }

    @GetMapping("/findByUsername")
    public ResponseEntity findRatingsByUsername(@RequestParam String username) {
        System.out.println("FIND RATINGS BY USERNAME CALLED for " + username);

        if (!isValidString(username)) return ResponseEntity.badRequest().body("Invalid input format");

        List<Rating> userRatings = ratingService.findByUsername(username);
        if (userRatings.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(userRatings);
    }

    // CREATE METHODS

    @PostMapping("/create")
    public ResponseEntity createNewRating(@RequestBody Rating rating) {
        System.out.println("CREATE NEW RATING CALLED for " + rating);

        // Validate rating
        if (!hasValidRatingValue(rating)) return ResponseEntity.status(400).body("Invalid ratingValue");
        if (!ratingValidator.hasValidFilm(rating)) return ResponseEntity.status(400).body("No valid film attached to the rating");
        if (!ratingValidator.hasValidUser(rating)) return ResponseEntity.status(400).body("Invalid user attached to the rating");

        // Save rating and update its average
        Optional<Rating> savedRating = ratingService.createNewRating(rating);
        if (savedRating.isEmpty()) return ResponseEntity.status(500).body("Something went wrong saving the rating");

        // Update average
        filmService.calculateAndUpdateAverageRatingByFilmId(rating.getFilm().getId());

        return ResponseEntity.ok(savedRating.get());
    }



}

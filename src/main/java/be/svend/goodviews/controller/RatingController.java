package be.svend.goodviews.controller;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.User;
import be.svend.goodviews.services.rating.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rating")
public class RatingController {
    RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    // FIND METHODS

    @GetMapping("/findByFilmId")
    public ResponseEntity findRatingsByFilmId(@RequestParam String filmId) {
        System.out.println("FIND RATING BY FILM ID CALLED with " + filmId);

        // TODO: validate String?

        List<Rating> filmRatings = ratingService.findByFilmId(filmId);
        if (filmRatings.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(filmRatings);
    }

    @GetMapping("/findByUsername")
    public ResponseEntity findRatingsByUsername(@RequestParam String username) {
        System.out.println("FIND RATINGS BY USERNAME CALLED FOR " + username);

        // TODO: Validate string?

        List<Rating> userRatings = ratingService.findByUsername(username);
        if (userRatings.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(userRatings);
    }

    @CrossOrigin // TODO: Take away if no longer localhost
    @GetMapping("/{id}")
    public ResponseEntity findRatingById(@PathVariable String id) {
        System.out.println("FIND RATINGS BY ID FOR " + id);

        // TODO: Validate string?

        Optional<Rating> rating = ratingService.findById(id);
        if (rating.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(rating.get());
    }

}

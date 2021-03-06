package be.svend.goodviews.controller;

import be.svend.goodviews.DTOs.RatingDTO;
import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.User;
import be.svend.goodviews.repositories.RatingRepository;
import be.svend.goodviews.services.film.FilmService;
import be.svend.goodviews.services.rating.RatingScrubber;
import be.svend.goodviews.services.rating.RatingService;
import be.svend.goodviews.services.rating.RatingValidator;
import be.svend.goodviews.services.users.UserScrubber;
import be.svend.goodviews.services.users.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static be.svend.goodviews.services.StringValidator.isValidString;
import static be.svend.goodviews.services.film.FilmValidator.isValidFilmIdFormat;
import static be.svend.goodviews.services.rating.RatingValidator.hasValidRatingValue;
import static be.svend.goodviews.services.rating.RatingValidator.isValidRatingValue;

@RestController
@RequestMapping("/rating")
public class RatingController {
    RatingService ratingService;
    RatingValidator ratingValidator;
    RatingRepository ratingRepo;

    FilmService filmService; // To update the average rating of the film
    UserService userService; // To check whether a new rating is of an existing user

    public RatingController(RatingService ratingService, RatingValidator ratingValidator,
                            RatingRepository ratingRepo,
                            FilmService filmService, UserService userService) {
        this.ratingService = ratingService;
        this.ratingValidator = ratingValidator;
        this.ratingRepo = ratingRepo;

        this.filmService = filmService;
        this.userService = userService;
    }

    // FIND METHODS

    @CrossOrigin
    @GetMapping("/{id}")
    public ResponseEntity findRatingById(@PathVariable String id) {
        System.out.println("FIND RATINGS BY ID FOR " + id);

        if (!isValidString(id)) return ResponseEntity.badRequest().body("Invalid input format");

        Optional<Rating> rating = ratingService.findById(id);
        if (rating.isEmpty()) return ResponseEntity.notFound().build();

        Rating scrubbedRating = RatingScrubber.scrubRatingOfUserInfo(rating.get());

        return ResponseEntity.ok(scrubbedRating);
    }

    @CrossOrigin
    @GetMapping("/findByIdforUser")
    public ResponseEntity findRatingById(@RequestParam String ratingId, @RequestParam String username) {
        System.out.println("FIND RATINGS BY ID FOR " + ratingId);

        if (!isValidString(ratingId) || !isValidString(username)) return ResponseEntity.badRequest().body("Invalid input format");

        Optional<Rating> rating = ratingService.findById(ratingId);
        if (rating.isEmpty()) return ResponseEntity.notFound().build();

        Optional<User> foundUser = userService.findByUsername(username);
        if (foundUser.isEmpty()) return ResponseEntity.notFound().build();

        RatingDTO ratingDTO = ratingService.createRatingDTO(rating.get(),foundUser.get());

        return ResponseEntity.ok(ratingDTO);
    }

    @GetMapping("/findByFilmId")
    public ResponseEntity findRatingsByFilmId(@RequestParam String filmId) {
        System.out.println("FIND RATING BY FILM ID CALLED with " + filmId);

        if (!isValidFilmIdFormat(filmId)) return ResponseEntity.badRequest().body("Invalid input format");

        List<Rating> filmRatings = ratingService.findByFilmId(filmId);
        if (filmRatings.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(filmRatings);
    }

    @CrossOrigin
    @GetMapping("/findByUsername")
    public ResponseEntity findRatingsByUsername(@RequestParam String username) {
        System.out.println("FIND RATINGS BY USERNAME CALLED for " + username);

        if (!isValidString(username)) return ResponseEntity.badRequest().body("Invalid input format");

        List<Rating> userRatings = ratingService.findByUsername(username);
        if (userRatings.isEmpty()) return ResponseEntity.notFound().build();

        // Sort
        userRatings = userRatings.stream().sorted(Comparator.comparing(r -> r.getDateOfRating())).collect(Collectors.toList());
        Collections.reverse(userRatings);

        return ResponseEntity.ok(userRatings);
    }

    @CrossOrigin
    @GetMapping("/findNumberByUsername")
    public ResponseEntity findNumberOfRatingsByUsername(@RequestParam String username) {
        System.out.println("FIND NUMBER OF RATINGS BY USERNAME CALLED for " + username);

        if (!isValidString(username)) return ResponseEntity.badRequest().body("Invalid input format");

        Integer numberOfRatings = ratingRepo.countRatingsByUser_Username(username);

        return ResponseEntity.ok(numberOfRatings);
    }

    @CrossOrigin
    @GetMapping("/latestRatings")
        public ResponseEntity findLatestRatings() {
        System.out.println("FIND LATEST RATINGS CALLED");

        List<Rating> latestRatings = ratingRepo.findTop3ByOrderByDateOfRatingDesc();

        for (Rating rating: latestRatings) {
            rating.setUser(UserScrubber.scrubAllExceptUsername(rating.getUser()));
        }

        return ResponseEntity.ok(latestRatings);
    }

    @CrossOrigin
    @GetMapping("/currentRatingStatus")
    public ResponseEntity findCurrentRatingStatusByUsernameAndFilmId(@RequestParam String username, @RequestParam String filmId) {
        System.out.println("FIND CURRENT RATING STATUS BY USERNAME AND FILM ID CALLED for" + username + " & " + filmId);

        if (!isValidString(username) || !isValidString(filmId)) return ResponseEntity.status(400).body("Invalid format");

        Optional<Rating> foundRating = ratingService.findById(username + filmId);
        if (foundRating.isPresent()) return ResponseEntity.ok(foundRating.get());

        Optional<Film> foundFilm = filmService.findById(filmId);
        if (foundFilm.isEmpty()) return ResponseEntity.status(404).body("No such film");

        Optional<User> foundUser = userService.findByUsername(username);
        if (foundUser.isEmpty()) return ResponseEntity.status(404).body("No such user");

        Rating rating = new Rating();
        rating.setFilm(foundFilm.get());
        rating.setUser(foundUser.get());
        return ResponseEntity.ok(rating);

    }

    // CREATE METHODS

    @CrossOrigin
    @PostMapping("/create")
    public ResponseEntity createNewRating(@RequestBody Rating rating) {
        System.out.println("CREATE NEW RATING CALLED for " + rating);

        // Validate rating
        if (!hasValidRatingValue(rating)) return ResponseEntity.status(400).body("Invalid ratingValue");
        if (!ratingValidator.hasValidFilm(rating)) return ResponseEntity.status(400).body("No valid film attached to the rating");
        if (!ratingValidator.hasValidUser(rating)) return ResponseEntity.status(400).body("Invalid user attached to the rating");

        // TODO: Check if it's by the user or an admin

        // Check if create or update is required
        Optional<Rating> savedRating;
        if (ratingValidator.isInDatabase(rating).isPresent()) {
            savedRating = ratingService.updateRating(rating);
        } else {
            savedRating = ratingService.createNewRating(rating);
        }
        if (savedRating.isEmpty()) return ResponseEntity.status(500).body("Something went wrong saving the rating");

        // Update average
        filmService.calculateAndUpdateAverageRatingByFilmId(rating.getFilm().getId());

        return ResponseEntity.ok(savedRating.get());
    }

    // UPDATE METHODS

    @CrossOrigin
    @RequestMapping("/addLike")
    public ResponseEntity updateRatingWithLike(@RequestParam String username, @RequestParam String ratingId) {
        System.out.println("UPDATE RATING WITH LIKE CALLED for " + username + " on " + ratingId);

        if (!isValidString(username) || !isValidString(ratingId)) return ResponseEntity.status(400).body("Invalid input format");

        Optional<User> foundUser = userService.findByUsername(username);
        if (foundUser.isEmpty()) return ResponseEntity.status(400).body("No such user");
        Optional<Rating> foundRating = ratingService.findById(ratingId);
        if (foundRating.isEmpty()) return ResponseEntity.status(400).body("No such rating");

        ratingService.addLikeToRating(foundRating.get(),foundUser.get());

        return ResponseEntity.ok("Like saved");
    }

    @CrossOrigin
    @RequestMapping("/removeLike")
    public ResponseEntity updateRatingRemovingLike(@RequestParam String username, @RequestParam String ratingId) {
        System.out.println("UPDATE RATING WITH REMOVING LIKE CALLED for " + username + " on " + ratingId);

        if (!isValidString(username) || !isValidString(ratingId)) return ResponseEntity.status(400).body("Invalid input format");

        Optional<User> foundUser = userService.findByUsername(username);
        if (foundUser.isEmpty()) return ResponseEntity.status(400).body("No such user");
        Optional<Rating> foundRating = ratingService.findById(ratingId);
        if (foundRating.isEmpty()) return ResponseEntity.status(400).body("No such rating");

        ratingService.removeLikeFromRatingByUser(foundRating.get(),foundUser.get());

        return ResponseEntity.ok("Unlike saved");
    }

    @CrossOrigin
    @PostMapping("/addReview")
    public ResponseEntity updateRatingWithReview(@RequestBody Rating rating) {
        System.out.println("UPDATE RATING WITH REVIEW CALLED FOR:" + rating.toString());
        String username = rating.getUser().getUsername();
        String review = rating.getReview();

        if (!isValidString(username) || !isValidString(review)) return ResponseEntity.status(400).body("Invalid input format");
        String ratingId = username + rating.getFilm().getId();

        Optional<User> foundUser = userService.findByUsername(username);
        if (foundUser.isEmpty()) return ResponseEntity.status(400).body("No such user");
        Optional<Rating> foundRating = ratingService.findById(ratingId);
        if (foundRating.isEmpty()) return ResponseEntity.status(400).body("No such rating");

        ratingService.updateRatingWithReview(foundRating.get(), review);

        return ResponseEntity.ok("Review saved");
    }

    // DELETE METHODS

    @CrossOrigin
    @DeleteMapping("delete/{ratingId}")
    public ResponseEntity deleteRatingByRatingId(@PathVariable String ratingId) {
        System.out.println("DELETE RATING CALLED for id: " + ratingId);

        if (!isValidString(ratingId)) return ResponseEntity.status(400).body("Invalid input");

        Optional<Rating> existingRating = ratingValidator.ratingIdInDatabase(ratingId);
        if (existingRating.isEmpty()) return ResponseEntity.status(404).body("Rating not found");

        // TODO: Check if it's by the user or an admin

        String filmId = existingRating.get().getFilm().getId();

        if (!ratingService.deleteRating(existingRating.get())) return ResponseEntity.status(500).body("Something went wrong deleting the rating");

        filmService.calculateAndUpdateAverageRatingByFilmId(filmId);

        return ResponseEntity.ok().body("Rating deleted");
    }


}

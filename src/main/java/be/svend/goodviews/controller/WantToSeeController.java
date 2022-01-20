package be.svend.goodviews.controller;

import be.svend.goodviews.models.User;
import be.svend.goodviews.models.WantToSee;
import be.svend.goodviews.services.film.FilmValidator;
import be.svend.goodviews.services.rating.WantToSeeService;
import be.svend.goodviews.services.users.UserValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static be.svend.goodviews.services.StringValidator.isValidString;

@RestController
@RequestMapping("/wantToSee")
public class WantToSeeController {
    WantToSeeService wtsService;

    UserValidator userValidator;
    FilmValidator filmValidator;

    public WantToSeeController(WantToSeeService wtsService, UserValidator userValidator, FilmValidator filmValidator) {
        this.wtsService = wtsService;
        this.userValidator = userValidator;
        this.filmValidator = filmValidator;
    }

    // FIND METHODS

    @CrossOrigin
    @GetMapping("/{username}")
    public ResponseEntity findWantToSeesByUsername(@PathVariable String username) {
        System.out.println("FIND WANT-TO-SEES BY USERNAME called for: " + username);

        if (!isValidString(username)) return ResponseEntity.badRequest().body("Invalid username format");

        Optional<User> foundUser = userValidator.isExistingUserWithUsername(username);
        if (foundUser.isEmpty()) return ResponseEntity.notFound().build();

        List<WantToSee> foundWantToSees = wtsService.findByUser(foundUser.get());

        return ResponseEntity.ok(foundWantToSees);
    }
}

package be.svend.goodviews.controller;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.User;
import be.svend.goodviews.models.WantToSee;
import be.svend.goodviews.services.film.FilmValidator;
import be.svend.goodviews.services.rating.WantToSeeService;
import be.svend.goodviews.services.users.UserValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static be.svend.goodviews.services.StringValidator.isValidString;

@CrossOrigin
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
        if (foundUser.isEmpty()) return ResponseEntity.status(400).body("No user user");

        List<WantToSee> foundWantToSees = wtsService.findByUser(foundUser.get()).stream().sorted(Comparator.comparing(wts -> wts.getDateCreated())).collect(Collectors.toList());
        Collections.reverse(foundWantToSees);

        return ResponseEntity.ok(foundWantToSees);
    }

    @CrossOrigin
    @GetMapping("/findByUsernameAndFilmId")
    public ResponseEntity findWantToSeeByUsernameAndFilmId(@RequestParam String username, @RequestParam String filmId) {
        System.out.println("FIND WANT-TO-SEES BY USERNAME AND FILM ID called for: " + username);

        if (!isValidString(username) || !isValidString(filmId)) return ResponseEntity.badRequest().body("Invalid input format");

        Optional<User> foundUser = userValidator.isExistingUserWithUsername(username);
        if (foundUser.isEmpty()) return ResponseEntity.status(400).body("No user user");

        Optional<Film> foundFilm = filmValidator.isExistingFilmId(filmId);
        if (foundFilm.isEmpty()) return ResponseEntity.status(400).body("No user film");

        Optional<WantToSee> foundWantToSee = wtsService.findByUserAndFilm(foundUser.get(), foundFilm.get());
        if (foundWantToSee.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(foundWantToSee.get().getId());
    }


    // CREATE METHODS

    @CrossOrigin
    @PostMapping("/createWTS")
    public ResponseEntity createWTSByUsernameAndFilmId(@RequestBody WtsDTO wtsDTO) {
        String username = wtsDTO.getUsername();
        String filmId = wtsDTO.getFilmId();

        System.out.println("CREATE WTS BY USERNAME AND FILM ID called for: " + username + " and " + filmId);

        if (!isValidString(username)) return ResponseEntity.badRequest().body("Invalid username format");
        if (!isValidString(filmId)) return ResponseEntity.badRequest().body("Invalid filmId format");

        Optional<User> foundUser = userValidator.isExistingUserWithUsername(username);
        if (foundUser.isEmpty()) return ResponseEntity.status(400).body("No such user");
        Optional<Film> foundFilm = filmValidator.isExistingFilmId(filmId);
        if (foundFilm.isEmpty()) return ResponseEntity.status(400).body("No such film");

        Optional<WantToSee> createdWTS = wtsService.createWantToSee(foundUser.get(),foundFilm.get());
        if (createdWTS.isEmpty()) return ResponseEntity.status(500).body("Something went wrong saving");

        return ResponseEntity.ok(createdWTS);
    }

    // DELETE METHODS

    @CrossOrigin
    @DeleteMapping("/deleteWTS")
    public ResponseEntity deleteWTSById(@RequestBody WantToSee wts) {
        Long wtsId = wts.getId();
        System.out.println("DELETE WTS BY FILM ID called for: " + wtsId);


        Optional<WantToSee> foundWts = wtsService.findById(wtsId);
        if (foundWts.isEmpty()) return ResponseEntity.status(404).body("No such wts found");

        boolean deleted = wtsService.deleteWantToSee(foundWts.get());
        if (!deleted) return ResponseEntity.status(500).body("Something went deleting");

        return ResponseEntity.ok().body("WTS deleted");
    }
}

class WtsDTO {
    private  String username;

    private String filmId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFilmId() {
        return filmId;
    }

    public void setFilmId(String filmId) {
        this.filmId = filmId;
    }
}
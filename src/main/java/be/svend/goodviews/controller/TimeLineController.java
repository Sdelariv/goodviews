package be.svend.goodviews.controller;

import be.svend.goodviews.DTOs.TimelineDTO;
import be.svend.goodviews.models.User;
import be.svend.goodviews.models.update.LogUpdate;
import be.svend.goodviews.services.rating.RatingService;
import be.svend.goodviews.services.update.LogUpdateService;
import be.svend.goodviews.services.users.UserValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static be.svend.goodviews.services.StringValidator.isValidString;

@RestController
@RequestMapping("/timeline")
public class TimeLineController {
    RatingService ratingService;
    LogUpdateService logUpdateService;

    UserValidator userValidator;

    public TimeLineController(RatingService ratingService, LogUpdateService logUpdateService,
                              UserValidator userValidator) {
        this.ratingService = ratingService;
        this.logUpdateService = logUpdateService;
        this.userValidator = userValidator;
    }

    // FIND METHODS

    @CrossOrigin
    @GetMapping("/{username}")
    public ResponseEntity findTimelineByUsername(@PathVariable String username) {
        System.out.println("FIND TIMELINE BY USERNAME CALLED for: " + username);

        if (!isValidString(username)) return ResponseEntity.badRequest().body("Invalid username format");

        Optional<User> foundUser = userValidator.isExistingUserWithUsername(username);
        if (foundUser.isEmpty()) return ResponseEntity.status(400).body("No user user");

        List<TimelineDTO> foundUpdates = logUpdateService.findTimelinebyUserAndOffset(foundUser.get(), 0);

        return ResponseEntity.ok(foundUpdates);
    }




}

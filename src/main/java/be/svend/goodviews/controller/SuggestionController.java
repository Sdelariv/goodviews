package be.svend.goodviews.controller;

import be.svend.goodviews.models.notification.GenreSuggestion;
import be.svend.goodviews.models.notification.Notification;
import be.svend.goodviews.services.notification.SuggestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/suggestion")
public class SuggestionController {
    SuggestionService suggestionService;

    public SuggestionController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
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

    }


}

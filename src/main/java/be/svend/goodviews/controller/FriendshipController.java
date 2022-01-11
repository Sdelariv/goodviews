package be.svend.goodviews.controller;

import be.svend.goodviews.models.Friendship;
import be.svend.goodviews.services.users.FriendshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static be.svend.goodviews.services.StringValidator.isValidString;

@RestController
@RequestMapping("/friendship")
public class FriendshipController {
    FriendshipService friendshipService;

    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    // FIND METHODS

    @GetMapping("/{id}")
    public ResponseEntity findFriendshipById(@PathVariable String id) {
        System.out.println("FIND FRIENDSHIP BY ID CALLED for: " + id);

        if (!isValidString(id)) return ResponseEntity.badRequest().body("Invalid id format");

        Optional<Friendship> friendship = friendshipService.findFriendshipById(id);
        if (friendship.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(friendship.get());


    }
}

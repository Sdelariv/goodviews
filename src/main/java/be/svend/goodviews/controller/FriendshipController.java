package be.svend.goodviews.controller;

import be.svend.goodviews.models.Friendship;
import be.svend.goodviews.models.User;
import be.svend.goodviews.services.users.FriendshipService;
import be.svend.goodviews.services.users.UserValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static be.svend.goodviews.services.StringValidator.isValidString;

@RestController
@RequestMapping("/friendship")
public class FriendshipController {
    FriendshipService friendshipService;

    UserValidator userValidator;

    public FriendshipController(FriendshipService friendshipService,
                                UserValidator userValidator) {
        this.friendshipService = friendshipService;
        this.userValidator = userValidator;
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

    @GetMapping("/{username}/friendsAndRequests")
    public ResponseEntity findFriendshipsAndRequestsByUsername(@PathVariable String username) {
        System.out.println("FIND FRIENDSHIP AND REQUESTS BY USERNAME for: " + username);

        if (!isValidString(username)) return ResponseEntity.badRequest().body("Invalid id format");

        Optional<User> user = userValidator.isExistingUserWithUsername(username);
        if (user.isEmpty()) return ResponseEntity.status(400).body("No such user");

        List<Friendship> friendships = friendshipService.findAllFriendRequestsByUser(user.get());
        if (friendships.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(friendships);
    }

    @GetMapping("/{username}/friendRequests")
    public ResponseEntity findFriendRequestsForUsername(@PathVariable String username) {
        System.out.println("FIND FRIENDSHIPREQUESTS BY USERNAME for: " + username);

        if (!isValidString(username)) return ResponseEntity.badRequest().body("Invalid id format");

        Optional<User> user = userValidator.isExistingUserWithUsername(username);
        if (user.isEmpty()) return ResponseEntity.status(400).body("No such user");

        List<Friendship> friendships = friendshipService.findAllFriendRequestsForUser(user.get());
        if (friendships.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(friendships);
    }

    @GetMapping("/{username}/friends")
    public ResponseEntity findFriendsByUsername(@PathVariable String username) {
        System.out.println("FIND FRIENDS BY USERNAME for: " + username);

        if (!isValidString(username)) return ResponseEntity.badRequest().body("Invalid id format");

        Optional<User> user = userValidator.isExistingUserWithUsername(username);
        if (user.isEmpty()) return ResponseEntity.status(400).body("No such user");

        List<Friendship> friendships = friendshipService.findAllFriendsByUser(user.get());
        if (friendships.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(friendships);
    }

    // CREATE



}

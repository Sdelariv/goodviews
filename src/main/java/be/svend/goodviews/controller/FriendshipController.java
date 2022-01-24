package be.svend.goodviews.controller;

import be.svend.goodviews.models.Friendship;
import be.svend.goodviews.models.User;
import be.svend.goodviews.repositories.RatingRepository;
import be.svend.goodviews.services.users.FriendFinder;
import be.svend.goodviews.services.users.FriendshipService;
import be.svend.goodviews.services.users.UserScrubber;
import be.svend.goodviews.services.users.UserValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static be.svend.goodviews.services.StringValidator.isValidString;

@RestController
@RequestMapping("/friendship")
public class FriendshipController {
    FriendshipService friendshipService;
    FriendFinder friendFinder;
    RatingRepository ratingRepo;

    UserValidator userValidator;

    public FriendshipController(FriendshipService friendshipService,
                                FriendFinder friendFinder,
                                RatingRepository ratingRepo,
                                UserValidator userValidator) {
        this.friendshipService = friendshipService;
        this.friendFinder = friendFinder;
        this.ratingRepo = ratingRepo;
        this.userValidator = userValidator;
    }

    // FIND METHODS

    @GetMapping("/{id}")
    public ResponseEntity findFriendshipById(@PathVariable String id) {
        System.out.println("FIND FRIENDSHIP BY ID CALLED for: " + id);
        if (!isValidString(id)) return ResponseEntity.badRequest().body("Invalid id format");

        // TODO: Check if user has clearance for that request (admin or relevant user)

        Optional<Friendship> friendship = friendshipService.findFriendshipById(id);
        if (friendship.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(friendship.get());
    }

    @GetMapping("/{username}/friendsAndRequests")
    public ResponseEntity findFriendshipsAndRequestsByUsername(@PathVariable String username) {
        System.out.println("FIND FRIENDSHIP AND REQUESTS BY USERNAME for: " + username);
        if (!isValidString(username)) return ResponseEntity.badRequest().body("Invalid id format");

        // TODO: Check if user has clearance for that request (admin or relevant user)

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

        // TODO: Check if user has clearance for that request (admin or relevant user)

        Optional<User> user = userValidator.isExistingUserWithUsername(username);
        if (user.isEmpty()) return ResponseEntity.status(400).body("No such user");

        List<Friendship> friendships = friendshipService.findAllFriendRequestsForUser(user.get());
        if (friendships.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(friendships);
    }


    @GetMapping("/{username}/friendships")
    public ResponseEntity findFriendshipsByUsername(@PathVariable String username) {
        System.out.println("FIND FRIENDS BY USERNAME for: " + username);
        if (!isValidString(username)) return ResponseEntity.badRequest().body("Invalid username format");

        // TODO: Check if user has clearance for that request (admin or relevant user)

        Optional<User> user = userValidator.isExistingUserWithUsername(username);
        if (user.isEmpty()) return ResponseEntity.status(400).body("No such user");

        List<Friendship> friendships = friendshipService.findAllFriendshipsByUser(user.get());
        if (friendships.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(friendships);
    }

    @CrossOrigin
    @GetMapping("/{username}/friendlist")
    public ResponseEntity findFriendsByUsername(@PathVariable String username) {
        System.out.println("FIND FRIENDS BY USERNAME for: " + username);
        if (!isValidString(username)) return ResponseEntity.badRequest().body("Invalid username format");

        // TODO: Check if user has clearance for that request (admin or relevant user)

        Optional<User> user = userValidator.isExistingUserWithUsername(username);
        if (user.isEmpty()) return ResponseEntity.status(400).body("No such user");

        List<User> friends = friendFinder.findAllFriendsByUser(user.get());
        if (friends.isEmpty()) return ResponseEntity.notFound().build();

        // Scrub
        List<User> scrubbedFriends = new ArrayList<>();
        friends.forEach(u -> scrubbedFriends.add(UserScrubber.scrubAllExceptUsername(u)));

        return ResponseEntity.ok(scrubbedFriends);
    }



    // CREATE METHODS

    @PostMapping("/sendRequest")
    public ResponseEntity createFriendRequest(@PathVariable String senderUsername, @PathVariable String targetUsername) {
        System.out.println("CREATE FRIEND REQUEST CALLED for: " + senderUsername + " and " + targetUsername);

        if (!isValidString(senderUsername) || (!isValidString(targetUsername))) return ResponseEntity.badRequest().body("Invalid format");

        // TODO: Check if user has clearance for that request (admin or relevant user)

        Optional<User> requester = userValidator.isExistingUserWithUsername(senderUsername);
        Optional<User> target = userValidator.isExistingUserWithUsername(targetUsername);
        if (requester.isEmpty()) return ResponseEntity.badRequest().body("Requesting user doesn't exist");
        if (target.isEmpty()) return ResponseEntity.badRequest().body("Target user doesn't exist");

        if (requester.get().getUsername().equals(target.get().getUsername())) return ResponseEntity.badRequest().body("Can't be friends with oneself");

        if (!friendshipService.requestFriendship(requester.get(),target.get())) return ResponseEntity.status(500).body("Something went wrong trying to create the friendshiprequest");

        return ResponseEntity.ok().body("FriendshipRequest sent");
    }

    // UPDATE METHODS

    @PostMapping("/acceptRequest")
    public ResponseEntity acceptFriendRequest(@RequestParam String friendshipRequestId){
        System.out.println("ACCEPT FRIEND REQUEST CALLED for: " + friendshipRequestId);

        if (!isValidString(friendshipRequestId)) return ResponseEntity.badRequest().body("Invalid id format");

        // TODO: Check if user has clearance for that request (admin or relevant user)

        Optional<Friendship> existingRequest = friendshipService.findFriendshipById(friendshipRequestId);
        if (existingRequest.isEmpty()) return ResponseEntity.notFound().build();

        if (existingRequest.get().isAccepted()) return ResponseEntity.status(400).body("Friendship already exists");

        Optional<Friendship> acceptedFriendship = friendshipService.acceptFriendship(existingRequest.get());
        if (acceptedFriendship.isEmpty()) return ResponseEntity.status(500).body("Something went wrong accepting the friendship");

        return ResponseEntity.ok(acceptedFriendship.get());
    }

    // DELETE METHODS

    @DeleteMapping("/denyFriendRequest")
    public ResponseEntity denyFriendRequest(@RequestParam String friendshipRequestId) {
        System.out.println("DENY FRIEND REQUEST CALLED for: " + friendshipRequestId);

        if (!isValidString(friendshipRequestId)) return ResponseEntity.badRequest().body("Invalid id format");

        // TODO: Check if user has clearance for that request (admin or relevant user)

        Optional<Friendship> existingRequest = friendshipService.findFriendshipById(friendshipRequestId);
        if (existingRequest.isEmpty()) return ResponseEntity.notFound().build();

        if (existingRequest.get().isAccepted()) return ResponseEntity.status(400).body("Friendship already exists");

        if (!friendshipService.denyFriendship(existingRequest.get())) return ResponseEntity.status(500).body("Something went wrong denying the friendship");

        return ResponseEntity.ok().body("Friendship denied");
    }

    @DeleteMapping("/deleteFriendRequest")
    public ResponseEntity deleteFriendRequest(@RequestParam String friendshipRequestId) {
        System.out.println("DELETE FRIEND REQUEST CALLED for: " + friendshipRequestId);

        if (!isValidString(friendshipRequestId)) return ResponseEntity.badRequest().body("Invalid id format");

        // TODO: Check if user has clearance for that request (admin or relevant user)

        Optional<Friendship> existingFriendship = friendshipService.findFriendshipById(friendshipRequestId);
        if (existingFriendship.isEmpty()) return ResponseEntity.notFound().build();

        if (existingFriendship.get().isAccepted()) return ResponseEntity.status(400).body("Friendship already exists");

        if (!friendshipService.deleteFriendship(existingFriendship.get())) return ResponseEntity.status(500).body("Something went wrong denying the friendship");

        return ResponseEntity.ok().body("Friendship deleted");
    }

}

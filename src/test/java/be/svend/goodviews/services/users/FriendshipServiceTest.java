package be.svend.goodviews.services.users;

import be.svend.goodviews.GoodviewsApplication;
import be.svend.goodviews.models.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

class FriendshipServiceTest {
    private static User user;
    private static  User nonExistingUser;
    private static  User bibi;
    private static  User sven;
    private static  User waddles;

    private static  FriendshipService friendshipService;
    private static  UserService userService;


    @BeforeAll
    static void generalInit(){
        ConfigurableApplicationContext ctx = SpringApplication.run(GoodviewsApplication.class);
        friendshipService = ctx.getBean(FriendshipService.class);
        userService = ctx.getBean(UserService.class);

        user = new User();
        user.setUsername("testUser");
        user.setFirstName("Test");
        user.setLastName("Testington");
        user.setPassword("thierPassword");
        userService.createNewUser(user);

        bibi = new User();
        bibi.setUsername("bibi");
        bibi.setFirstName("Bibi");
        bibi.setLastName("The Bear");
        bibi.setPassword("herPassword");
        userService.createNewUser(bibi);

        sven = new User();
        sven.setUsername("sdelariv");
        sven.setFirstName("Sven");
        sven.setLastName("Delarivière");
        sven.setPassword("myPassword");
        userService.createNewUser(sven);

        waddles = new User();
        waddles.setUsername("waddles");
        waddles.setFirstName("Waddles");
        waddles.setLastName("The Pig");
        waddles.setPassword("hisPassword");
        userService.createNewUser(waddles);

        nonExistingUser = new User();
        nonExistingUser.setUsername("I don't exist");

        // friendshipService.requestFriendship(sven,"bibi");
        // friendshipService.createFriendship(bibi,waddles);
    }

    @Test
    void findAllFriendshipsAndRequests() {
        assertTrue(friendshipService.findAllFriendshipsAndRequests().size() > 0);
    }

    @Test
    void findAllFriendships() {
        assertTrue(friendshipService.findAllFriendshipsAndRequests().size() > 0);
    }

    @Test
    void findAllRequests() {
        assertTrue(friendshipService.findAllFriendshipsAndRequests().size() > 0);
    }

    @Test
    void findAllFriendsByUser() {
        assertTrue(friendshipService.findAllFriendsByUser(bibi).size() > 0);
    }

    @Test
    void findAllExistingFriendRequestsByUser() {
        assertTrue(friendshipService.findAllFriendRequestsByUser(sven).size() > 0);
    }

    @Test
    void findAllNonExistingFriendRequestsByUser() {

        assertTrue(friendshipService.findAllFriendRequestsByUser(bibi).size() == 0);
    }

    @Test
    void findAllFriendRequestsOfUser() {
        assertTrue(friendshipService.findAllFriendsByUser(bibi).size() > 0);
    }

    @Test
    void requestFriendship() {
        // friendshipService.requestFriendship(user,"bibi");
        assertTrue(friendshipService.findAllFriendRequestsByUser(user).size() > 0);
    }

    @Test
    void createRequestedFriendship() {
        assertTrue(friendshipService.createFriendship(user,waddles).isPresent());
    }

    @Test
    void createFriendship() {
    }

    @Test
    void acceptFriendship() {
    }

    @Test
    void denyFriendship() {
    }

    @AfterAll
    static void end() {
        userService.deleteUser(user);
    }
}
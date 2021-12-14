package be.svend.goodviews.services.users;

import be.svend.goodviews.GoodviewsApplication;
import be.svend.goodviews.models.User;
import be.svend.goodviews.repositories.UserRepository;
import be.svend.goodviews.services.rating.RatingService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    static UserService userService;

    @BeforeAll
    static void generalInit() {
        System.out.println("General initialisation");
        ConfigurableApplicationContext ctx = SpringApplication.run(GoodviewsApplication.class);
        userService = new UserService(ctx.getBean(UserRepository.class),ctx.getBean(UserValidator.class), ctx.getBean(RatingService.class));
        System.out.println("Success");
    }

    @Test
    void findByUsername() {
        Optional<User> foundUser = userService.findByUsername("sdelariv");
        assertTrue(foundUser.isPresent());
    }

    @Test
    void findByUserObject() {
    }

    @Test
    void findAllUsers() {
    }

    @Test
    void findAllAdmins() {
    }

    @Test
    void findAllRegularUsers() {
    }

    @Test
    void findAllArchitects() {
    }

    @Test
    void createNewUser() {
    }

    @Test
    void createNewUsers() {
    }

    @Test
    void updateUserByAdding() {
    }

    @Test
    void updateUserByReplacing() {
    }

    @Test
    void changeUsername() {
    }

    @Test
    void upgradeUserToAdmin() {
    }

    @Test
    void upgradeUserToArchitect() {
    }

    @Test
    void downgradeUserToUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void deleteUsers() {
    }
}
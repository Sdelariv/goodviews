package be.svend.goodviews.services.rating;

import be.svend.goodviews.GoodviewsApplication;
import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.User;
import be.svend.goodviews.repositories.UserRepository;
import be.svend.goodviews.services.film.FilmService;
import be.svend.goodviews.services.users.UserService;
import be.svend.goodviews.services.users.UserValidator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

class RatingServiceTest {
    static RatingService ratingService;
    static FilmService filmService;
    static UserService userService;
    static Rating rating;
    static Rating ratingToCreate;
    static Rating ratingToDelete;

    @BeforeAll
    static void generalInit() {
        ConfigurableApplicationContext ctx = SpringApplication.run(GoodviewsApplication.class);
        ratingService = ctx.getBean(RatingService.class);
        filmService = ctx.getBean(FilmService.class);
        userService = ctx.getBean(UserService.class);

        rating = new Rating();
        if (ratingService.findById("sdelarivtt4468740").isPresent()) rating = ratingService.findById("sdelarivtt4468740").get();
        else {
            rating.setRatingValue(95);
            rating.setFilm(filmService.findById("tt4468740").get());
            rating.setUser(userService.findByUsername("sdelariv").get());
            rating = ratingService.createNewRating(rating).get();
        }

        ratingToCreate = new Rating();
        ratingToCreate.setRatingValue(100);
        ratingToCreate.setFilm(filmService.findById("tt4468740").get());
        ratingToCreate.setUser(userService.findByUsername("bibi").get());

        ratingToDelete = new Rating();
        ratingToDelete.setRatingValue(70);
        ratingToDelete.setFilm(filmService.findById("tt16283826").get());
        ratingToDelete.setUser(userService.findByUsername("sdelariv").get());
        ratingToDelete = ratingService.createNewRating(ratingToDelete).get();
    }

    @Test
    void findById() {
        assertTrue(ratingService.findById(rating.getId()).isPresent());
    }

    @Test
    void findByFilmId() {
        assertTrue(ratingService.findByFilmId("tt4468740").size() > 0);
    }

    @Test
    void findByUsername() {
        assertTrue(ratingService.findByUsername("sdelariv").size() > 0);
    }

    @Test
    void createNewRating() {
        assertTrue(ratingService.createNewRating(ratingToCreate).isPresent());
    }

    @Test
    void updateRating() {
    }

    @Test
    void addCommentToRating() {
    }

    @Test
    void deleteRating() {
        String filmId = ratingToDelete.getFilm().getId();
        ratingService.deleteRating(ratingToDelete);
        assertTrue(ratingService.findByFilmId(filmId).isEmpty());
    }

    @AfterAll
    static void end() {
        ratingService.deleteRating(ratingToCreate);
    }
}
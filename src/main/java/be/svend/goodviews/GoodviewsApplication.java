package be.svend.goodviews;

import be.svend.goodviews.models.*;
import be.svend.goodviews.models.notification.FilmSuggestion;
import be.svend.goodviews.models.notification.TagSuggestion;
import be.svend.goodviews.repositories.*;
import be.svend.goodviews.services.notification.NotificationService;
import be.svend.goodviews.services.comment.CommentService;
import be.svend.goodviews.services.film.FilmService;
import be.svend.goodviews.services.notification.SuggestionService;
import be.svend.goodviews.services.rating.RatingService;
import be.svend.goodviews.services.rating.RatingValidator;
import be.svend.goodviews.services.update.LogUpdateService;
import be.svend.goodviews.services.users.FriendshipService;
import be.svend.goodviews.services.users.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Optional;

@SpringBootApplication
public class GoodviewsApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(GoodviewsApplication.class, args);

        RatingService ratingService = ctx.getBean(RatingService.class);
        UserService userService = ctx.getBean(UserService.class);
        CommentService commentService = ctx.getBean(CommentService.class);
        FriendshipService friendshipService = ctx.getBean(FriendshipService.class);
        NotificationService notificationService = ctx.getBean(NotificationService.class);
        SuggestionService suggestionService = ctx.getBean(SuggestionService.class);
        RatingRepository ratingRepo = ctx.getBean(RatingRepository.class);
        LogUpdateService logUpdateService = ctx.getBean(LogUpdateService.class);
        FilmService filmService = ctx.getBean(FilmService.class);


        User bibi = new User();
        bibi.setUsername("bibi");
        bibi.setFirstName("Bibi");
        bibi.setLastName("The Bear");
        bibi.setPassword("herPassword");

        User sven = new User();
        sven.setUsername("sdelariv");
        sven.setFirstName("Sven");
        sven.setLastName("Delarivière");
        sven.setPassword("myPassword");

        User waddles = new User();
        waddles.setUsername("waddles");
        waddles.setFirstName("Waddles");
        waddles.setLastName("The Pig");
        waddles.setPassword("hisPassword");

        Film film = new Film();
        film.setId("tt0110367");



        User newUser = new User();
        newUser.setUsername("userToDelete");
        newUser.setFirstName("Fake");
        newUser.setLastName("McFakington");
        newUser.setPassword("fakePassword");
        newUser = userService.createNewUser(newUser).get();



        Film henry = filmService.findById("tt16283668").get();

        Rating rating = new Rating();
        rating.setRatingValue(90);
        rating.setUser(newUser);
        rating.setFilm(henry);
        ratingService.createNewRating(rating);

        Comment comment = new Comment();
        comment.setComment("Thank you newUser!");
        comment.setUser(sven);

        commentService.createNewComment(comment,"userToDeletett16283668");

        userService.deleteUser(newUser);




    }

}

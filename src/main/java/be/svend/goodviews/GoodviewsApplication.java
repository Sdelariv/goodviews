package be.svend.goodviews;

import be.svend.goodviews.models.Friendship;
import be.svend.goodviews.models.User;
import be.svend.goodviews.repositories.*;
import be.svend.goodviews.services.comment.CommentService;
import be.svend.goodviews.services.crew.PersonService;
import be.svend.goodviews.services.film.FilmService;
import be.svend.goodviews.services.film.FilmValidator;
import be.svend.goodviews.services.film.properties.TagService;
import be.svend.goodviews.services.rating.RatingService;
import be.svend.goodviews.services.rating.RatingValidator;
import be.svend.goodviews.services.users.FriendshipService;
import be.svend.goodviews.services.users.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class GoodviewsApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(GoodviewsApplication.class, args);

        RatingService ratingService = new RatingService(ctx.getBean(RatingRepository.class),ctx.getBean(RatingValidator.class), ctx.getBean(FilmService.class));
        UserService userService = ctx.getBean(UserService.class);
        CommentService commentService = ctx.getBean(CommentService.class);
        FriendshipService friendshipService = ctx.getBean(FriendshipService.class);

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


        friendshipService.findAllFriendsByUser(bibi).forEach(System.out::println);

    }

}

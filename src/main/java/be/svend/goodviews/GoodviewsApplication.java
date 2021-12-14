package be.svend.goodviews;

import be.svend.goodviews.models.*;
import be.svend.goodviews.repositories.*;
import be.svend.goodviews.services.*;
import be.svend.goodviews.services.comment.CommentService;
import be.svend.goodviews.services.crew.PersonService;
import be.svend.goodviews.services.film.FilmService;
import be.svend.goodviews.services.film.FilmValidator;
import be.svend.goodviews.services.rating.RatingService;
import be.svend.goodviews.services.rating.RatingValidator;
import be.svend.goodviews.services.users.UserService;
import be.svend.goodviews.services.users.UserValidator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Optional;

@SpringBootApplication
public class GoodviewsApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(GoodviewsApplication.class, args);

        RatingService ratingService = new RatingService(ctx.getBean(RatingRepository.class),ctx.getBean(RatingValidator.class), ctx.getBean(FilmService.class));
        UserService userService = ctx.getBean(UserService.class);
        CommentService commentService = ctx.getBean(CommentService.class);
        FilmService filmService = new FilmService(ctx.getBean(FilmRepository.class),ctx.getBean(FilmValidator.class),ctx.getBean(PersonService.class),ctx.getBean(RatingRepository.class),ctx.getBean(GenreRepository.class),ctx.getBean(TagService.class));


        commentService.findByRatingId("sdelarivtt4468740").forEach(System.out::println);


    }

}

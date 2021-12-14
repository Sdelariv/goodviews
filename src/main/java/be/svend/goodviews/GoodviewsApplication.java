package be.svend.goodviews;

import be.svend.goodviews.models.*;
import be.svend.goodviews.repositories.*;
import be.svend.goodviews.services.*;
import be.svend.goodviews.services.crew.PersonService;
import be.svend.goodviews.services.film.FilmService;
import be.svend.goodviews.services.film.FilmValidator;
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
        UserService userService = new UserService(ctx.getBean(UserRepository.class),ctx.getBean(UserValidator.class), ctx.getBean(RatingService.class));
        FilmService filmService = new FilmService(ctx.getBean(FilmRepository.class),ctx.getBean(FilmValidator.class),ctx.getBean(PersonService.class),ctx.getBean(RatingRepository.class),ctx.getBean(GenreRepository.class));

        User user = new User();
        user.setLastName("Delarivière");
        user.setFirstName("Sven");
        user.setUsername("sdelariv");

        System.out.println("Creating user");
        userService.createNewUser(user);

        System.out.println("Finding user");
        Optional<User> foundUser = userService.findByUsername("sdelariv");

        System.out.println("Upgrading user");
        userService.upgradeUserToAdmin(foundUser.get());



/*
        FilmService service = new FilmService(ctx.getBean(FilmRepository.class),ctx.getBean(FilmValidator.class),ctx.getBean(PersonService.class));
        service.createFilmByImdbId("tt3765512");
        service.createFilmByImdbId("tt16283826");

        List<Film> films = service.findAllFilms();

        HardcopyMaker.makeHardCopy(films);
*/

    }

}

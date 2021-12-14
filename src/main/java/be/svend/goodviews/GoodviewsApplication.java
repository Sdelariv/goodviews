package be.svend.goodviews;

import be.svend.goodviews.models.*;
import be.svend.goodviews.repositories.*;
import be.svend.goodviews.services.*;
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
        UserService userService = new UserService(ctx.getBean(UserRepository.class),ctx.getBean(UserValidator.class), ctx.getBean(RatingService.class));
        FilmService filmService = new FilmService(ctx.getBean(FilmRepository.class),ctx.getBean(FilmValidator.class),ctx.getBean(PersonService.class),ctx.getBean(RatingRepository.class),ctx.getBean(GenreRepository.class),ctx.getBean(TagService.class));

        User user = new User();
        user.setLastName("Delarivière");
        user.setFirstName("Sven");
        user.setUsername("sdelariv");
        user.setPassword("myPassword");

        User user2 = new User();
        user2.setFirstName("Bibi");
        user2.setLastName("The Bear");
        user2.setUsername("bibi");
        user2.setPassword("herPassword");

        System.out.println("Creating users");
        System.out.println("- Saving Sven:");
        userService.createNewUser(user);
        System.out.println("- Saving Bibi");
        userService.createNewUser(user2);

        System.out.println("Finding user");
        Optional<User> foundUser = userService.findByUsername("sdelariv");

        System.out.println("Upgrading user");
        userService.upgradeUserToAdmin(foundUser.get());

        System.out.println("Regular users:");
        userService.findAllRegularUsers().forEach(System.out::println);

        System.out.println("Admins:");
        userService.findAllAdmins().forEach(System.out::println);



/*
        FilmService service = new FilmService(ctx.getBean(FilmRepository.class),ctx.getBean(FilmValidator.class),ctx.getBean(PersonService.class));
        service.createFilmByImdbId("tt3765512");
        service.createFilmByImdbId("tt16283826");

        List<Film> films = service.findAllFilms();

        HardcopyMaker.makeHardCopy(films);
*/

    }

}

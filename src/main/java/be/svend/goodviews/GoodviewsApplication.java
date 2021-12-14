package be.svend.goodviews;

import be.svend.goodviews.models.*;
import be.svend.goodviews.repositories.*;
import be.svend.goodviews.services.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class GoodviewsApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(GoodviewsApplication.class, args);

        RatingService ratingService = new RatingService(ctx.getBean(RatingRepository.class),ctx.getBean(RatingValidator.class), ctx.getBean(FilmService.class));
        UserService userService = new UserService(ctx.getBean(UserRepository.class),ctx.getBean(UserValidator.class), ctx.getBean(RatingService.class));
        FilmService filmService = new FilmService(ctx.getBean(FilmRepository.class),ctx.getBean(FilmValidator.class),ctx.getBean(PersonService.class),ctx.getBean(RatingRepository.class),ctx.getBean(GenreRepository.class));

        Genre genre = new Genre();
        genre.setName("Mystery");

        filmService.findByGenre(genre).forEach(System.out::println);


/*
        FilmService service = new FilmService(ctx.getBean(FilmRepository.class),ctx.getBean(FilmValidator.class),ctx.getBean(PersonService.class));
        service.createFilmByImdbId("tt3765512");
        service.createFilmByImdbId("tt16283826");

        List<Film> films = service.findAllFilms();

        HardcopyMaker.makeHardCopy(films);
*/

    }

}

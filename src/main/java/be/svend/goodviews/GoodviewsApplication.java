package be.svend.goodviews;

import be.svend.goodviews.factory.FilmFactory;
import be.svend.goodviews.factory.scraper.imdbscraper.ImdbScraper;
import be.svend.goodviews.factory.scraper.svendscraper.HardcopyScraper;
import be.svend.goodviews.factory.scraper.webscraper.WebScraper;
import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.Genre;
import be.svend.goodviews.models.Person;
import be.svend.goodviews.repositories.FilmRepository;
import be.svend.goodviews.repositories.GenreRepository;
import be.svend.goodviews.services.FilmService;
import be.svend.goodviews.services.FilmValidator;
import be.svend.goodviews.services.GenreService;
import be.svend.goodviews.services.PersonService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@SpringBootApplication
public class GoodviewsApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(GoodviewsApplication.class, args);

        /*
        FilmService service = new FilmService(ctx.getBean(FilmRepository.class),ctx.getBean(FilmValidator.class), ctx.getBean(PersonService.class));

        Person darren = new Person("nm0004716","Darren Aranovsky");

        Film film = new Film();
        film.setTitle("Pi");
        film.setId("tt0138704");
        film.setReleaseYear(1998);
        film.setDirector(darren);

        service.createFilm(film);

        System.out.println("Finding film:");
        System.out.println(service.findById("tt0138704").get());

        System.out.println("Finding directors:");
        List<Film> filmsByDarren = service.findFilmsByDirectorId("nm0004716");
        filmsByDarren.forEach(System.out::println);

        System.out.println("Finding writers:");
        List<Film> filmsWrittenByDarren = service.findFilmsByWriterId("nm0004716");
        filmsWrittenByDarren.forEach(System.out::println);

        Genre genre = new Genre("New Weird");
        film.setGenres(List.of(new Genre("New Weird")));
        service.updateFilm(film);

        GenreService g_service = new GenreService(ctx.getBean(GenreRepository.class));
        g_service.deleteGenre(new Genre("New Weird"));

        System.out.println("Finding films with title Pi, after deleting genre");
        List<Film> filmsWithTitlePi = service.findByTitle("Pi");
        filmsWithTitlePi.forEach(System.out::println);

*/




        FilmFactory factory = new FilmFactory(ctx.getBean(FilmService.class),ctx.getBean(ImdbScraper.class), ctx.getBean(HardcopyScraper.class));

        factory.createDatabaseFromHardcopy();


    }

}

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
import java.util.stream.Collectors;

@SpringBootApplication
public class GoodviewsApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(GoodviewsApplication.class, args);


        FilmService service = new FilmService(ctx.getBean(FilmRepository.class),ctx.getBean(FilmValidator.class), ctx.getBean(PersonService.class));

        List<String> filmIds = service.findAllFilms().stream().map(f -> f.getId()).collect(Collectors.toList());

        service.updateFilmsAddWebDataByImdbId(filmIds);


    }

}

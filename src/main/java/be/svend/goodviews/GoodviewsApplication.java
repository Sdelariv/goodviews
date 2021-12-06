package be.svend.goodviews;

import be.svend.goodviews.factory.FilmFactory;
import be.svend.goodviews.factory.imdbscraper.ImdbScraper;
import be.svend.goodviews.factory.svendscraper.HardcopyScraper;
import be.svend.goodviews.factory.svendscraper.MoktokDBScraper;
import be.svend.goodviews.services.FilmService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class GoodviewsApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(GoodviewsApplication.class, args);



        FilmFactory factory = new FilmFactory(ctx.getBean(FilmService.class),ctx.getBean(ImdbScraper.class), ctx.getBean(HardcopyScraper.class));
        factory.createDatabaseFromHardcopy();


    }

}

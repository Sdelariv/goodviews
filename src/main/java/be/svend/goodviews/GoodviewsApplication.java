package be.svend.goodviews;

import be.svend.goodviews.factory.FilmFactory;
import be.svend.goodviews.repositories.FilmRepository;
import be.svend.goodviews.repositories.GenreRepository;
import be.svend.goodviews.repositories.TagRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class GoodviewsApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(GoodviewsApplication.class, args);
    }

}

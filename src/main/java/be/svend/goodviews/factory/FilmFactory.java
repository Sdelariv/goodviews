package be.svend.goodviews.factory;

import be.svend.goodviews.factory.imdbscraper.ImdbScraper;
import be.svend.goodviews.factory.svendscraper.HardcopyScraper;
import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.Genre;
import be.svend.goodviews.models.Tag;
import be.svend.goodviews.services.FilmService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;


@Component
public class FilmFactory {
    FilmService filmService;
    ImdbScraper imdbScraper;
    HardcopyScraper hardcopyScraper;

    public FilmFactory(FilmService filmService,
                       ImdbScraper imdbScraper,
                       HardcopyScraper hardcopyScraper) {
        this.filmService = filmService;
        this.imdbScraper = imdbScraper;
        this.hardcopyScraper = hardcopyScraper;

    }


    public void createDatabaseFromImdbTsv() {
        List<Film> films = imdbScraper.scrapeImdb();
        filmService.createFilms(films);
    }

    public void createDatabaseFromHardcopy() {
        List<Film> films = hardcopyScraper.scrapeHardCopy();
        filmService.createFilms(films);
    }

    public void saveTestFilms() {
        Film pad2 = new Film("Paddington 2");
        pad2.setId("tt4468740");
        pad2.setAverageRating(99);
        pad2.setReleaseDate(LocalDate.of(2017, 12, 9));
        pad2.setDirector("Paul King");
        pad2.addTag(new Tag("Bear"));
        pad2.addTag( new Tag("Heartwarming"));
        pad2.addTag("Christmas");
        pad2.addTag("Winter");
        pad2.addTag(new Tag("Sequel"));
        pad2.addGenre( new Genre("Adventure"));
        pad2.addGenre(new Genre("Family"));
        pad2.setRunTime(103);
        pad2.setPosterUrl("https://upload.wikimedia.org/wikipedia/ar/8/80/Paddington_two_poster.jpg");

        Film emma = new Film("Emma");
        emma.setId("tt9214832");
        emma.setAverageRating(87);
        emma.setDirector("Autumn de Wilde");
        emma.setReleaseDate(LocalDate.of(2020, 2,21));
        emma.addGenre(new Genre("Period"));
        emma.addGenre(new Genre("Romance"));
        emma.addGenre("Comedy");
        emma.addTag("Ladyfilm");
        emma.addTag("Period");
        emma.addTag("Jane Austen");
        emma.addTag("Novel adaptation");
        emma.addTag("Funny");
        emma.setRunTime(124);
        emma.setPosterUrl("https://upload.wikimedia.org/wikipedia/en/thumb/5/53/Emma_poster.jpeg/220px-Emma_poster.jpeg");


        Film jp = new Film("Jurassic Park");
        jp.setId("tt0107290");
        jp.setDirector("Steven Spielberg");
        jp.setAverageRating(92);
        jp.setReleaseDate(LocalDate.of(1993,10,20));
        jp.addGenre("Science Fiction");
        jp.addGenre("Adventure");
        jp.addGenre("Action");
        jp.addTag("Dinosaurs");
        jp.addTag("Novel adaptation");
        jp.addTag("Exciting");
        jp.setRunTime(127);
        jp.setPosterUrl("https://upload.wikimedia.org/wikipedia/en/e/e7/Jurassic_Park_poster.jpg");

        filmService.updateFilms(List.of(pad2,emma,jp));
    }
}

package be.svend.goodviews.factory.scraper.webscraper;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.services.FilmValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static be.svend.goodviews.services.FilmValidator.isValidFilmIdFormat;

public class WebScraper {


    public static List<Film> addPosters(List<Film> films) {

        for (Film film: films) {
            Optional<String> posterUrl = PosterScraper.scrapePoster(film.getId());

            if (posterUrl.isPresent()) film.setPosterUrl(posterUrl.get());
        }

        return films;
    }

    public static Optional<Film> createFilmWithWebData(String id) {

        Film film = new Film();
        film.setId(id);

        Optional<Film> createdFilm = updateFilmWithWebData(film);

        return createdFilm;
    }

    public static List<Film> updateFilmsWithWebData(List<Film> films) {

        for (Film film: films) {
            updateFilmWithWebData(film);
        }

        return films;
    }


    public static Optional<Film> updateFilmWithWebData(Film film) {

        String json = fetchJsonBasedOnId(film.getId());

        if (json == null) return Optional.empty();

        // Initialise TODO: Move where?
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Film.class,new FilmDeserialiser());
        objectMapper.registerModule(module);

        Film updatedFilm = new Film();

        // Add everything

        try {
            updatedFilm = objectMapper.readValue(json, Film.class);
            System.out.println(film);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add smaller version of poster

        Optional<String> posterUrl = PosterScraper.scrapePoster(film.getId());
        if (posterUrl.isPresent()) updatedFilm.setPosterUrl(posterUrl.get());

        return Optional.of(updatedFilm);
    }

    private static String fetchJsonBasedOnId(String id) {
        // TODO: check whether Id exists, return null if not
        //  fetch Json if it does

        return null;
    }
}

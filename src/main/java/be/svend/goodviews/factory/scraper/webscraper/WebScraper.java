package be.svend.goodviews.factory.scraper.webscraper;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.services.FilmMerger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WebScraper {


    public static List<Film> addPosters(List<Film> films) {

        for (Film film: films) {
            Optional<String> posterUrl = PosterScraper.scrapePoster(film.getId());

            if (posterUrl.isPresent()) film.setPosterUrl(posterUrl.get());
        }

        return films;
    }

    public static List<Film> createFilmsWithWebData(List<String> ids) {
        List<Film> createdFilms = new ArrayList<>();

        for (String id: ids) {
            Optional<Film> film = createFilmWithWebData(id);
            if (film.isPresent()) createdFilms.add(film.get());
        }

        return createdFilms;
    }

    public static Optional<Film> createFilmWithWebData(String id) {
        // Setting up
        Film createdFilm = new Film();
        createdFilm.setId(id);

        // Fetching information
        String json = fetchJsonBasedOnId(createdFilm.getId());
        if (json == null) return Optional.empty();

        // Initialise TODO: Move where?
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Film.class,new FilmDeserialiser());
        objectMapper.registerModule(module);

        // Create a Film object based on the Web Data
        try {
            createdFilm = objectMapper.readValue(json, Film.class);
            System.out.println(createdFilm);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add smaller version of poster
        Optional<String> posterUrl = PosterScraper.scrapePoster(createdFilm.getId());
        if (posterUrl.isPresent()) createdFilm.setPosterUrl(posterUrl.get());

        return Optional.of(createdFilm);
    }

    public static List<Film> updateFilmsAddWebData(List<Film> films) {
        List<Film> updatedFilms = new ArrayList<>();

        for (Film film: films) {
            Optional<Film> updatedFilm = updateFilmAddWebData(film);
            if (updatedFilm.isPresent()) updatedFilms.add(updatedFilm.get());
        }

        return films;
    }

    public static List<Film> updateFilmsReplaceWithWebData(List<Film> films) {
        List<Film> updatedFilms = new ArrayList<>();

        for (Film film: films) {
            Optional<Film> updatedFilm = updateFilmReplaceWithWebData(film);
            if (updatedFilm.isPresent()) updatedFilms.add(updatedFilm.get());
        }

        return films;
    }

    public static Optional<Film> updateFilmReplaceWithWebData(Film film) {
        Optional<Film> filmWithWebData = createFilmWithWebData(film.getId());
        if (filmWithWebData.isEmpty()) return Optional.empty();

        return filmWithWebData;
    }


    public static Optional<Film> updateFilmAddWebData(Film film) {

        Optional<Film> filmWithWebData = createFilmWithWebData(film.getId());
        if (filmWithWebData.isEmpty()) return Optional.empty();

        Optional<Film> updatedFilm = FilmMerger.mergeFilms(film,filmWithWebData.get());
        return updatedFilm;
    }

    /**
     * Goes to the IMDB page (based on filmId), gets its HTML, filters the Json bit and returns that as a string
     * @param id - the filmId (in IMDB-formatting) to fetch the url
     * @return Json String of the film
     */
    private static String fetchJsonBasedOnId(String id) {
        String imdbUrl = "https://www.imdb.com/title/" + id + "/";

        String json = null;

        try {
            // Getting the full HTML page of the film
            Document doc = Jsoup.connect(imdbUrl).get();
            String fullHtml = doc.body().toString();

            // Extracting the json bit
            json = fullHtml.split("@context")[1].split("</script")[0];
            json = "{\"@context" + json;

            // Json has \ characters all over, so we're getting rid of them
            json = json.replaceAll("\\\\","");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Id doesn't exist");
            return null;
        }

        return json;
    }
}

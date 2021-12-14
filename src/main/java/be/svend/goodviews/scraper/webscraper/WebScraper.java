package be.svend.goodviews.scraper.webscraper;

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
            Optional<String> posterUrl = scrapePoster(film.getId());

            if (posterUrl.isPresent()) film.setPosterUrl(posterUrl.get());
        }

        return films;
    }

    // CREATE FILM FROM WEB METHODS

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

        // Initialise Json Decoder TODO: Move where?
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Film.class,new FilmDeserialiser());
        objectMapper.registerModule(module);

        // Create a Film object based on the Web Data
        try {
            createdFilm = objectMapper.readValue(json, Film.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add smaller version of poster
        Optional<String> posterUrl = scrapePoster(createdFilm.getId());
        if (posterUrl.isPresent()) createdFilm.setPosterUrl(posterUrl.get());

        // Add Translated title
        createdFilm = addTitles(createdFilm);

        return Optional.of(createdFilm);
    }

    // UPDATE FILM FROM WEB

    public static List<Film> addAllWithWebData(List<Film> films) {
        List<Film> updatedFilms = new ArrayList<>();

        for (Film film: films) {
            Optional<Film> updatedFilm = addWebData(film);
            if (updatedFilm.isPresent()) updatedFilms.add(updatedFilm.get());
        }

        return films;
    }

    public static List<Film> replaceAllWithWebData(List<Film> films) {
        List<Film> updatedFilms = new ArrayList<>();

        for (Film film: films) {
            Optional<Film> updatedFilm = replaceWithWebData(film);
            if (updatedFilm.isPresent()) updatedFilms.add(updatedFilm.get());
        }

        return films;
    }

    public static Optional<Film> replaceWithWebData(Film film) {
        Optional<Film> filmWithWebData = createFilmWithWebData(film.getId());
        if (filmWithWebData.isEmpty()) return Optional.empty();

        return filmWithWebData;
    }


    public static Optional<Film> addWebData(Film film) {

        Optional<Film> filmWithWebData = createFilmWithWebData(film.getId());
        if (filmWithWebData.isEmpty()) return Optional.empty();

        Optional<Film> updatedFilm = FilmMerger.mergeFilms(film,filmWithWebData.get());
        return updatedFilm;
    }

    // INTERNAL METHODS

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

    private static Film addTitles(Film film) {
        String id = film.getId();
        String imdbUrl = "https://www.imdb.com/title/" + id + "/releaseinfo";

        // Getting the full HTML page of the film
        Document doc = null;
        try {
            doc = Jsoup.connect(imdbUrl).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fullHtml = doc.body().toString();

        film = extractTitlesFromHtmlString(film,fullHtml);

        return film;
    }

    private static Film extractTitlesFromHtmlString(Film film, String fullHtml) {
        fullHtml = fullHtml.split("id=\"akas\"")[1];

        try {
            // Extracting the original title
            String originalTitleBit = fullHtml.split("original title")[1].split("/tr")[0];
            String originalTitle = originalTitleBit.split("title\">")[1].split("</td")[0];

            // Extracting English title
            String translatedTitleBit = fullHtml.split("UK<")[1].split("/tr")[0];
            String translatedTitle = translatedTitleBit.split("title\">")[1].split("</td")[0];

            if (!translatedTitle.equals(originalTitle)) {
                film.setTranslatedTitle(translatedTitle);
            }


        } catch (ArrayIndexOutOfBoundsException e) {
        }

        return film;
    }


    public static Optional<String> scrapePoster(String id) {
        Document doc = null;
        String imdbUrl = "https://www.imdb.com/title/" + id + "/";
        Optional<String> posterUrl = Optional.empty();

        try {
            doc = Jsoup.connect(imdbUrl).get();

            String fullHtml = doc.body().toString();

            String[] splittedHtml = fullHtml.split("img alt"); // Locate the img alt div

            String img_alt = splittedHtml[1].split("</div>")[0].split("src")[1]; // Get the first url out
            posterUrl = Optional.of(img_alt.substring(2,img_alt.length()-2)); // Remove " and "

        } catch (IOException e) {
            e.printStackTrace();
        }

        return posterUrl;
    }
}

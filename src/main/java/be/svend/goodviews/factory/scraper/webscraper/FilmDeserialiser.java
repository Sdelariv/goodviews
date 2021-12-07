package be.svend.goodviews.factory.scraper.webscraper;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.Genre;
import be.svend.goodviews.models.Person;
import be.svend.goodviews.models.Tag;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.io.IOException;
import java.time.Duration;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class FilmDeserialiser extends StdDeserializer<Film> {

    public FilmDeserialiser() {
        this(null);
    }
    public FilmDeserialiser(Class<?> vc) {
        super(vc);
    }

    @Override
    public Film deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jp.getCodec().readTree(jp);

        Film film = new Film();

        String id = node.get("url").toString().split("/")[2];
        film.setId(id);

        String title = node.get("name").toString();
        film.setTitle(title);

        String translatedTitle = null;
        try {
             translatedTitle = node.get("alternateName").toString();
        } catch (NullPointerException e) {
        }
        film.setTranslatedTitle(translatedTitle);

        Integer releaseYear = null;
        try {
            releaseYear = Integer.parseInt(node.get("datePublished").toString().substring(1,5));
        } catch (NumberFormatException e) {
        }
        film.setReleaseYear(releaseYear);

        /*
        // TODO: Decide whether you want a bigger poster
        String posterUrl = node.get("image").toString();
        film.setPosterUrl(posterUrl);
        */

        List<Genre> genres = new ArrayList<>();
        node.get("genre").forEach(g -> genres.add(new Genre(g.toString().substring(1,g.toString().length()-1))));
        film.setGenres(genres);

        List<Tag> tags;

        List<Person> director;

        List<Person> writer;

        String runtimeResponse = node.get("duration").toString();
        Integer runTime = convertToMinutes(runtimeResponse);
        film.setRunTime(runTime);

        Double returnedRating = node.get("aggregateRating").get("ratingValue").asDouble();
        Integer averageRatingImdb = (int) (returnedRating * 10);
        film.setAverageRatingImdb(averageRatingImdb);

        return film;
    }

    private Integer convertToMinutes(String r) {
        String[] response = r.substring(3).split("H");
        Integer hours = Integer.parseInt(response[0]);
        Integer minutes = Integer.parseInt(response[1].substring(0,response.length-1));
        Integer runTime = hours * 60 + minutes;
        return runTime;
    }


}

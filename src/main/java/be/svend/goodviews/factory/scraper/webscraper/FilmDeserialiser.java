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

import java.io.IOException;
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
        title = title.substring(1,title.length()-1);
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

        /*
        // If we want imdb tags (probably not)
        List<Tag> tags = new ArrayList<>();
        String[] keywords = node.get("keywords").toString().split(",");
        for (String keyword: keywords) {
            tags.add(new Tag(keyword));
        }
        film.setTags(tags);
         */

        List<Person> director = convertToPersonList(node.get("director"));
        film.setDirector(director);

        String creatorResponse = node.get("creator").toString();
        System.out.println(creatorResponse);

        List<Person> writer = convertToPersonList(node.get("creator"));
        film.setWriter(writer);

        String runtimeResponse = node.get("duration").toString();
        Integer runTime = convertToMinutes(runtimeResponse);
        film.setRunTime(runTime);

        Double returnedRating = node.get("aggregateRating").get("ratingValue").asDouble();
        Integer averageRatingImdb = (int) (returnedRating * 10);
        film.setAverageRatingImdb(averageRatingImdb);

        return film;
    }

    private List<Person> convertToPersonList(JsonNode directorResponses) {
        List<Person> persons = new ArrayList<>();

        for (JsonNode directorResponse: directorResponses) {
            if (!directorResponse.get("@type").toString().equals("\"Person\"")) continue;

            String directorId = directorResponse.get("url").toString().split("/")[2];
            String name = directorResponse.get("name").toString();
            name = trimOuterCharacters(name); // getting rid of " and "
            Person person = new Person(directorId,name);
            persons.add(person);
        }

        return persons;
    }

    private Integer convertToMinutes(String r) {
        Integer runTime = 0;

        try {
            r = r.substring(3, r.length() - 1); // Removing PT and surrounding "" from duration string

            if (r.contains("H")) {
                String[] response = r.split("H");
                Integer hours = Integer.parseInt(response[0]);
                Integer minutes = Integer.parseInt(response[1].substring(0, response.length - 1));
                runTime = hours * 60 + minutes;
            } else {
                runTime = Integer.parseInt(r.substring(0, r.length() - 1));
            }
        } catch (NumberFormatException e) {
            System.out.println("Something went wrong trying to convert " + r + "into a runtime");
        }

        return runTime;
    }

    private String trimOuterCharacters(String string) {
        return string.substring(1,string.length()-1);
    }


}

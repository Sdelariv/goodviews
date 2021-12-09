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
        // Initialising
        JsonNode node = jp.getCodec().readTree(jp);
        Film film = new Film();

        // Setting everything up
        film = addIdFromNode(film,node);

        film = addTitlesFromNode(film,node);

        film = addReleaseYearFromNode(film,node);

        film = addGenresFromNode(film,node);

        film = addDirectorsFromNode(film,node);

        film = addWritersFromNode(film,node);

        film = addRuntimeFromNode(film,node);

        film = addRatingFromNode(film,node);

        // film = addPosterFromNode(film,node); -  TODO: Decide whether you want a bigger poster

        // film = addTagsFromNode(film,node);  - If we want imdb tags (probably not)

        return film;
    }

    // ADDING FROM NODE METHODS

    private Film addIdFromNode(Film film, JsonNode node) {
        String id = node.get("url").toString().split("/")[2];
        film.setId(id);

        return film;
    }

    private Film addPosterFromNode(Film film, JsonNode node) {
        JsonNode returnedNode = node.get("image");
        if (returnedNode == null) return film;

        String posterUrl = returnedNode.toString();
        film.setPosterUrl(posterUrl);

        return film;
    }


    // TODO: fix translated title
    private Film addTitlesFromNode(Film film, JsonNode node) {
        JsonNode returnedNode = node.get("name");
        if (returnedNode == null) {
            System.out.println("Can't find name");
            return film;
        }

        String title = returnedNode.toString();
        title = title.substring(1,title.length()-1);
        film.setTitle(title);

        JsonNode returnedNode2 = node.get("alternateName");
        if (returnedNode2 == null) return film;

        String translatedTitle = node.get("alternateName").toString();
        film.setTranslatedTitle(translatedTitle);

        return film;
    }

    private Film addReleaseYearFromNode(Film film, JsonNode node) {
        JsonNode returnedNode = node.get("datePublished");
        if (returnedNode == null) return film;

        Integer releaseYear = null;
        try {
            String releaseYearString = returnedNode.toString().substring(1,5);
            if (releaseYearString != null) releaseYear = Integer.parseInt(releaseYearString);
        } catch (NumberFormatException e) {
        }

        film.setReleaseYear(releaseYear);
        return film;
    }

    private Film addGenresFromNode(Film film, JsonNode node) {
        JsonNode returnedNode = node.get("genre");
        if (returnedNode == null) return film;

        List<Genre> genres = new ArrayList<>();
        returnedNode.forEach(g -> genres.add(new Genre(g.toString().substring(1,g.toString().length()-1))));
        film.setGenres(genres);

        return film;
    }

    private Film addTagsFromNode(Film film, JsonNode node) {
        JsonNode returnedNode = node.get("keywords");
        if (returnedNode == null) return film;

        List<Tag> tags = new ArrayList<>();
        String[] keywords = returnedNode.toString().split(",");
        for (String keyword: keywords) {
            tags.add(new Tag(keyword));
        }
        film.setTags(tags);

        return film;
    }

    private Film addRatingFromNode(Film film, JsonNode node) {
        JsonNode returnedNode = node.get("aggregateRating");
        if (returnedNode == null) return film;

        JsonNode ratingValueNode = returnedNode.get("ratingValue");
        if (ratingValueNode == null) return film;


        Double returnedRating = ratingValueNode.asDouble();
        Integer averageRatingImdb = (int) (returnedRating * 10);
        film.setAverageRatingImdb(averageRatingImdb);

        return film;
    }

    private Film addDirectorsFromNode(Film film, JsonNode node) {
        JsonNode returnedNode = node.get("director");
        if (returnedNode == null) return film;

        List<Person> director = convertToPersonList(returnedNode);
        film.setDirector(director);

        return film;
    }

    private Film addWritersFromNode(Film film, JsonNode node) {
        JsonNode returnedNode = node.get("creator");
        if (returnedNode == null) return film;

        List<Person> writer = convertToPersonList(returnedNode);
        film.setWriter(writer);

        return film;
    }

    private Film addRuntimeFromNode(Film film, JsonNode node) {
        JsonNode returnedNode = node.get("duration");
        if (returnedNode == null) return film;

        Integer runTime = convertToMinutes(returnedNode.toString());
        film.setRunTime(runTime);

        return film;
    }

    // FURTHER INTERNAL METHODS

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
                Integer minutes = Integer.parseInt(response[1].substring(0, response.length ));// DELETES THE M TOO
                runTime = hours * 60 + minutes;
            } else {
                runTime = Integer.parseInt(r.substring(0, r.length() - 1));
            }
        } catch (NumberFormatException e) {
            System.out.println("Something went wrong trying to convert " + r + " into a runtime");
        }

        return runTime;
    }

    private String trimOuterCharacters(String string) {
        return string.substring(1,string.length()-1);
    }


}

package be.svend.goodviews.factory.scraper.svendscraper;

import be.svend.goodviews.factory.scraper.webscraper.PosterScraper;
import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.Genre;
import be.svend.goodviews.models.Person;
import be.svend.goodviews.models.Tag;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static be.svend.goodviews.factory.scraper.imdbscraper.LineValidator.isLineContainingRelevantId;

/**
 * Responsible for scraping duties based on the moktok Server and exporting to a tsv harcopy (for ease)
 * @Author: Sven Delarivière
 */
@Component
public class HardcopyScraper {
    File hardcopy = new File("D:/moktok.hardcopy/data.tsv");

    private final int FILMID_INDEX = 0;
    private final int FILM_TITLE_INDEX = 1;
    private final int FILM_TRANSLATED_TITLE_INDEX = 2;
    private final int RELEASE_YEAR_INDEX = 3;
    private final int POSTER_URL_INDEX = 4;
    private final int GENRES_INDEX = 5;
    private final int TAGS_INDEX = 6;
    private final int AV_RATING_INDEX = 7;
    private final int AV_RATING_IMDB_INDEX = 8;
    private final int DIRECTOR_INDEX = 9;
    private final int WRITER_INDEX = 10;

    public List<Film> scrapeHardCopy() {
        List<Film> films = new ArrayList<>();

        try (BufferedReader basicReader = new BufferedReader(new FileReader(hardcopy))) {
            String line = null;
            while ((line = basicReader.readLine()) != null) {
                String[] lineItems = line.split("\t");
                String filmId = lineItems[FILMID_INDEX];

                if (filmId.equals("filmId")) continue;

              films.add(translateLineItemsToFilm(lineItems));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return films;
    }

    private Film translateLineItemsToFilm(String[] lineItems) {
        Film film = new Film();

        String filmId = lineItems[FILMID_INDEX];
        film.setId(filmId);

        String filmTitle = lineItems[FILM_TITLE_INDEX];
        film.setTitle(nullcheckString(filmTitle));

        String filmTranslatedTitle = lineItems[FILM_TRANSLATED_TITLE_INDEX];
        film.setTranslatedTitle(nullcheckString(filmTranslatedTitle));

        String releaseYearString = lineItems[RELEASE_YEAR_INDEX];
        film.setReleaseYear(nullcheckIntegerConverter(releaseYearString));

        String posterUrl = lineItems[POSTER_URL_INDEX];
        film.setPosterUrl(nullcheckString(posterUrl));

        String genresString = lineItems[GENRES_INDEX];
        film.setGenres(convertToGenres(genresString));

        String tagsString = lineItems[TAGS_INDEX];
        film.setTags(convertToTags(tagsString));

        String averageRatingString = lineItems[AV_RATING_INDEX];
        film.setAverageRating(nullcheckIntegerConverter(averageRatingString));

        String averageRatingImdbString = lineItems[AV_RATING_IMDB_INDEX];
        film.setAverageRatingImdb(nullcheckIntegerConverter(averageRatingImdbString));

        String directors = lineItems[DIRECTOR_INDEX];
        film.setDirector(convertToListOfPersons(directors));

        String writers = lineItems[WRITER_INDEX];
        film.setWriter(convertToListOfPersons(writers));

        return film;
    }

    public static List<Person> convertToListOfPersons(String personsString) {
        List<Person> persons = new ArrayList<>();

        personsString = personsString.substring(1,personsString.length()-1); // Deleting [ and ]

        String[] personsArray = personsString.trim().split("Person\\{");

        for (String personString: personsArray) {
            if (personString =="") continue;
            String[] personProperties = personString.trim().split(",");

            String personId = personProperties[0].trim().substring(3);

            String personName = personProperties[1].trim().substring(6, personProperties[1].length()-3);


            Person person = new Person();
            person.setId(personId);
            person.setName(personName);

            persons.add(person);
        }

        return persons;
    }

    private List<Tag> convertToTags(String tagsString) {
        List<Tag> tags = new ArrayList<>();

        if (!tagsString.startsWith("[Tag")) return Collections.emptyList();

        tagsString = tagsString.substring(1,tagsString.length()-1); // Deleting [ and ]

        String[] tagArray = tagsString.trim().split("Tag\\{");

        for (String tagString: tagArray) {
            if (tagString =="") continue;

            String[] tagProperties = tagString.trim().split(",");

            String tagIdString = tagProperties[0].trim().substring(3);
            Long tagId = Long.parseLong(tagIdString);

            String tagName = tagProperties[1].trim().substring(6,tagProperties[1].length()-3);

            Tag tag = new Tag();
            tag.setId(tagId);
            tag.setName(tagName);

            tags.add(tag);
        }

        return tags;
    }

    private List<Genre> convertToGenres(String genresString) {
        List<Genre> genres = new ArrayList<>();

        if (!genresString.startsWith("[Genre")) return Collections.emptyList();

        genresString = genresString.substring(1,genresString.length()-1);

        String[] genreArray = genresString.trim().split("Genre\\{");


        for (String genreString: genreArray) {
            if (genreString == "") continue;

            String[] genreProperties = genreString.trim().split(",");

            String genreIdString = genreProperties[0].trim().substring(3);
            Long genreId = Long.parseLong(genreIdString);

            String genreName = genreProperties[1].trim().substring(6,genreProperties[1].length()-3);

            Genre genre = new Genre();
            genre.setId(genreId);
            genre.setName(genreName);
            genres.add(genre);
        }

        return genres;
    }

    private String nullcheckString(String string) {
        if (string.equals("null")) return null;
        return string;
    }

    private Integer nullcheckIntegerConverter(String integer) {
        Integer convertedInt;
        if (integer.equals("null")) return null;
        else {
            try {
                convertedInt = Integer.parseInt(integer);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return convertedInt;
    }

}

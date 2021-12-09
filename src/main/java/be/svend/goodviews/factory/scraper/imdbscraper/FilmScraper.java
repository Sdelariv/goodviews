package be.svend.goodviews.factory.scraper.imdbscraper;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.Genre;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static be.svend.goodviews.factory.scraper.imdbscraper.LineValidator.isFilmInDataLine;
import static be.svend.goodviews.factory.scraper.imdbscraper.LineValidator.isLineContainingRelevantId;

/**
 * Responsible for scraping duties based on tsv files of basicData
 * @Author: Sven Delarivière
 */
public class FilmScraper {
    File basicData;

    private final int ID_INDEX = 0;
    private final int TYPE_INDEX = 1;
    private final int PR_TITLE_INDEX = 2;
    private final int OR_TITLE_INDEX = 3;
    private final int YEAR_INDEX = 5;
    private final int RUNTIME_INDEX = 7;
    private final int GENRE_INDEX = 8;

    public FilmScraper(String parentFolderPath) {
        basicData = new File(parentFolderPath + "title.basics.tsv/data.tsv");
    }


    /**
     * Uses a map of filmIds (with their average rating) to create a list of films with the film-data from the dataset basicData
     * @param desiredIdsWithAverageRating Map<String-id, Integer-averageImdbRating> - a map of filmIds and their average imdb rating
     * @return List<Film> - a list of films with film-properties (incl average rating)
     */
    public List<Film> gatherFilmInfoFromIds(Map<String, Integer> desiredIdsWithAverageRating) {
        List<Film> films = new ArrayList<>();

        try (BufferedReader basicReader = new BufferedReader(new FileReader(basicData))) {
            String line = null;
            while ((line = basicReader.readLine()) != null) {
                String[] lineItems = line.split("\t");
                String filmId = lineItems[ID_INDEX];

                if (!isLineContainingRelevantId(lineItems,desiredIdsWithAverageRating)) continue;

                if (isFilmInDataLine(lineItems)) {
                    System.out.println("Found a film! " + lineItems[0]);
                    Film film = convertBasicDataToFilm(lineItems);
                    film.setAverageRatingImdb(desiredIdsWithAverageRating.get(filmId));

                    films.add(film);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        return films;
    }


    /**
     * Uses a lineItem from basicData to create a Film-object with its id, titles, runtime, releasedata and genre
     * @param lineItems - a lineItem from basicData
     * @return Film - object with id, titles, runtime, releasedata and genre extracted from the lineItem
     */
    private Film convertBasicDataToFilm(String[] lineItems) {
        Film film = new Film();

        // Id
        String filmId = lineItems[ID_INDEX];
        film.setId(filmId);

        // Title(s)
        String translatedTitle = lineItems[PR_TITLE_INDEX];
        String originalTitle = lineItems[OR_TITLE_INDEX];
        if (translatedTitle.equals(originalTitle)) {
            film.setTitle(originalTitle);
        } else {
            film.setTitle(originalTitle);
            film.setTranslatedTitle(translatedTitle);
        }

        // Runtime
        film.setRunTime(getRuntime(lineItems));

        // ReleaseYear
        film.setReleaseYear(getReleaseYearFromDataLine(lineItems));

        // Genres
        film.setGenres(getGenres(lineItems));

        return film;
    }

    /**
     * Creates a list of Genre objects based on the lineItem of the project
     * @param lineItems - a lineItem from basicData
     * @return List<Genre> list of genres of the project, extracted from the lineItem
     */
    private List<Genre> getGenres(String[] lineItems) {
        String[] genreNames = lineItems[GENRE_INDEX].split(",");
        List<Genre> genres = new ArrayList<>();
        for (String genreName: genreNames) {
            Genre genre = new Genre(genreName);
            genres.add(genre);
        }
        return genres;
    }

    /**
     * Creates an Integer of the runtime based on the lineItem of the project
     * @param lineItems - a lineItem from basicData
     * @return Integer - runtime of the project, extracted from the lineItem
     */
    private Integer getRuntime(String[] lineItems) {
        String runtimeMinutesString = lineItems[RUNTIME_INDEX];

        if (!runtimeMinutesString.equals("\\N")) {
            Integer runtimeMinutes = Integer.parseInt(runtimeMinutesString);
            return runtimeMinutes;
        } else {
            return 0;
        }
    }

    /**
     * Creates an Integer of the release year based on the lineItem of the project
     * @param lineItems - a lineItem from basicData
     * @return Integer - the year of the project, extracted from the lineItem
     */
    private Integer getReleaseYearFromDataLine(String[] lineItems) {
        String releaseYearString = lineItems[YEAR_INDEX];

        if (!releaseYearString.equals("\\N")) {
            Integer releaseYear = Integer.parseInt(releaseYearString);
            return releaseYear;
        } else {
            return 0;
        }
    }

}

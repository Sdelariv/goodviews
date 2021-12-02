package be.svend.goodviews.factory;

import be.svend.goodviews.models.Director;
import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.Genre;
import be.svend.goodviews.models.Writer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ImdbScraper {
    private File basicData;
    private File ratingData;
    private File crewData;
    private int voteMinimum;

    // For basicData
    private final int ID_INDEX = 0;
    private final int TYPE_INDEX = 1;
    private final int PR_TITLE_INDEX = 2;
    private final int OR_TITLE_INDEX = 3;
    private final int YEAR_INDEX = 5;
    private final int RUNTIME_INDEX = 7;
    private final int GENRE_INDEX = 8;

    // For ratingData
    private final int AVERAGE_RATING_INDEX = 1;
    private final int NUMBER_RATINGS_INDEX = 2;

    // For crewData
    private final int DIRECTOR_INDEX = 1;
    private final int WRITER_INDEX = 2;

    public ImdbScraper() {
        basicData = new File("D:/title.basics.tsv/data.tsv");
        ratingData = new File("D:/title.ratings.tsv/data.tsv");
        crewData = new File("D:/title.crew.tsv/data.tsv");
        voteMinimum = 25000;
    }

    public List<Film> scrapeImdb() {
        Map<String, Integer> ids = findIdsWithSufficientRatings();
        List<Film> filmList = gatherFilmsFromIds(ids);
        filmList = addCrewToFilm(filmList);

        return filmList;
    }

    public Map<String, Integer> findIdsWithSufficientRatings() {
        Map<String, Integer> ids = new HashMap<>();

        try (BufferedReader basicReader = new BufferedReader(new FileReader(ratingData))) {
            String line = null;
            while ((line = basicReader.readLine()) != null) {
                String[] lineItems = line.split("\t");
                if (lineItems[ID_INDEX].equals("tconst")) continue;

                if (hasSufficientVotes(lineItems)) {
                    String id = lineItems[ID_INDEX];
                    Integer averageRating = getAverageRatingImdb(lineItems);
                    ids.put(id, averageRating);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ids;
    }

    private Integer getAverageRatingImdb(String[] lineItems) {
        double averageRating = Double.parseDouble(lineItems[AVERAGE_RATING_INDEX]);
        averageRating = averageRating * 10;

        return (int) averageRating;
    }

    public List<Film> gatherFilmsFromIds(Map<String, Integer> ids) {
        List<Film> films = new ArrayList<>();

        try (BufferedReader basicReader = new BufferedReader(new FileReader(basicData))) {
            String line = null;
            while ((line = basicReader.readLine()) != null) {
                String[] lineItems = line.split("\t");
                String filmId = lineItems[ID_INDEX];

                if (filmId.equals("tconst")) continue;

                if (!ids.containsKey(filmId)) continue;

                if (isFilm(lineItems)) {
                    System.out.println("Found a film! " + lineItems[0]);
                    Film film = convertBasicDataToFilm(lineItems);
                    film.setAverageRatingImdb(ids.get(filmId));
                    films.add(film);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return films;
    }

    public List<Film> addCrewToFilm(List<Film> films) {
        List<String> filmIds = films.stream().map(f -> f.getId()).collect(Collectors.toList());
        Map<String, List<Director>> directors = getAllDirectors(filmIds);
        Map<String, List<Writer>> writers = getAllWriters(filmIds);

        for (Film film: films) {
            film.setDirector(directors.get(film.getId()));
            System.out.println("Adding " + directors + " as director(s) of " + film.getTitle());
            film.setWriter(writers.get(film.getId()));
            System.out.println("Adding " + writers + " as writer(s) of " + film.getTitle());
        }

        return films;
    }


    // INTERNAL METHODS

    private Map<String, List<Director>> getAllDirectors(List<String> filmIds) {
        Map<String,List<Director>> allDirectors = new HashMap<>();

        try (BufferedReader basicReader = new BufferedReader(new FileReader(crewData))) {
            String line = null;
            while ((line = basicReader.readLine()) != null) {
                String[] lineItems = line.split("\t");
                String filmId = lineItems[ID_INDEX];
                if (filmId.equals("tconst")) continue;

                if (!filmIds.contains(filmId)) continue;

                String[] directorIds = lineItems[DIRECTOR_INDEX].split(",");
                if (directorIds[0].equals("\\N")) continue;


                List<Director> directors = new ArrayList<>();
                for (String directorId: directorIds) {
                    System.out.println("Found director: " + directorId);
                    directors.add(new Director(directorId,null));
                }
                allDirectors.put(filmId,directors);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return allDirectors;
    }

    private Map<String, List<Writer>> getAllWriters(List<String> filmIds) {
        Map<String,List<Writer>> allWriters = new HashMap<>();

        try (BufferedReader basicReader = new BufferedReader(new FileReader(crewData))) {
            String line = null;
            while ((line = basicReader.readLine()) != null) {
                String[] lineItems = line.split("\t");
                String filmId = lineItems[ID_INDEX];
                if (filmId.equals("tconst")) continue;

                if (!filmIds.contains(filmId)) continue;

                String[] writerIds = lineItems[WRITER_INDEX].split(",");
                if (writerIds[0].equals("\\N")) continue;


                List<Writer> writers = new ArrayList<>();
                for (String writerId: writerIds) {
                    System.out.println("Found writer: " + writerId);
                    writers.add(new Writer(writerId,null));
                }
                allWriters.put(filmId,writers);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return allWriters;
    }

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
        film.setReleaseYear(getReleaseYear(lineItems));

        // Genres
        film.setGenres(getGenres(lineItems));

        return film;
    }

    private List<Genre> getGenres(String[] lineItems) {
        String[] genreNames = lineItems[GENRE_INDEX].split(",");
        List<Genre> genres = new ArrayList<>();
        for (String genreName: genreNames) {
            Genre genre = new Genre(genreName);
            genres.add(genre);
        }
        return genres;
    }

    private Integer getRuntime(String[] lineItems) {
        String runtimeMinutesString = lineItems[RUNTIME_INDEX];

        if (!runtimeMinutesString.equals("\\N")) {
            Integer runtimeMinutes = Integer.parseInt(runtimeMinutesString);
            return runtimeMinutes;
        } else {
            return 0;
        }
    }

    private Integer getReleaseYear(String[] lineItems) {
        String releaseYearString = lineItems[YEAR_INDEX];

        if (!releaseYearString.equals("\\N")) {
            Integer releaseYear = Integer.parseInt(releaseYearString);
            return releaseYear;
        } else {
            return 0;
        }
    }


    private boolean isFilm(String[] lineItems) {
        if (lineItems[TYPE_INDEX].equals("movie")) return true;
        else return false;
    }

    private boolean hasSufficientVotes(String[] lineItems){
        return Integer.parseInt(lineItems[NUMBER_RATINGS_INDEX]) > this.voteMinimum;
    }
}



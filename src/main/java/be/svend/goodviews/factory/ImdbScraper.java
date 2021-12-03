package be.svend.goodviews.factory;

import be.svend.goodviews.models.*;

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
    private File personData;
    private int voteMinimum;

    // For all
    private final int ID_INDEX = 0;

    // For basicData
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

    // For peopleData
    private final int CREW_NAME_INDEX = 1;

    public ImdbScraper() {
        String parentFolderPath = "D:/imdb/";
        basicData = new File(parentFolderPath + "title.basics.tsv/data.tsv");
        ratingData = new File(parentFolderPath + "title.ratings.tsv/data.tsv");
        crewData = new File(parentFolderPath + "title.crew.tsv/data.tsv");
        personData = new File(parentFolderPath + "name.basics.tsv/data.tsv");
        voteMinimum = 25000;
    }

    public List<Film> scrapeImdb() {
        Map<String, Integer> ids = findIdsWithSufficientRatings();
        List<Film> filmList = gatherFilmsFromIds(ids);
        filmList = addCrewIdsToFilms(filmList);
        filmList = addCrewDataToFilmsBasedOnCrewId(filmList);

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

    public List<Film> addCrewIdsToFilms(List<Film> films) {
        List<String> filmIds = films.stream().map(f -> f.getId()).collect(Collectors.toList());
        Map<String, List<Person>> directors = getAllDirectors(filmIds);
        Map<String, List<Person>> writers = getAllWriters(filmIds);

        for (Film film: films) {
            film.setDirector(directors.get(film.getId()));
            System.out.println("Adding " + directors + " as director(s) of " + film.getTitle());
            film.setWriter(writers.get(film.getId()));
            System.out.println("Adding " + writers + " as writer(s) of " + film.getTitle());
        }

        return films;
    }


    public List<Film> addCrewDataToFilmsBasedOnCrewId(List<Film> filmList) {
        for (Film film: filmList) {
            List<Person> directors = getDirectorBasedOnCrewIdsInFilm(film);
            film.setDirector(directors);
            System.out.println("Found full director data for " + film.getTitle() + ":" + directors);
        }

        for (Film film: filmList) {
            List<Person> writers = getWriterBasedOnCrewIdsInFilm(film);
            film.setWriter(writers);
            System.out.println("Found full writer data for " + film.getTitle() + ":" + writers);
        }

        return filmList;
    }

    private List<Person> getWriterBasedOnCrewIdsInFilm(Film film) {
        List<Person> writers = film.getWriter();

        for (Person writer: writers) {
            if (writer.getId() != null) writer.setName(getPersonNameBasedOnCrewId(writer.getId()));
        }

        return writers;
    }

    private List<Person> getDirectorBasedOnCrewIdsInFilm(Film film) {
        List<Person> directors = film.getDirector();

        for (Person director: directors) {
            if (director.getId() != null) director.setName(getPersonNameBasedOnCrewId(director.getId()));
        }

        return directors;
    }

    private String getPersonNameBasedOnCrewId(String crewId) {
        String name = "/";

        try (BufferedReader basicReader = new BufferedReader(new FileReader(personData))) {
            String line = null;
            while ((line = basicReader.readLine()) != null) {
                String[] lineItems = line.split("\t");
                String foundCrewId = lineItems[ID_INDEX];

                if (foundCrewId.equals("tconst")) continue;

                if (!crewId.equals(foundCrewId)) continue;

                name = lineItems[CREW_NAME_INDEX];
                }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return name;
    }


    // INTERNAL METHODS

    private Map<String, List<Person>> getAllDirectors(List<String> filmIds) {
        Map<String,List<Person>> allDirectors = new HashMap<>();

        try (BufferedReader basicReader = new BufferedReader(new FileReader(crewData))) {
            String line = null;
            while ((line = basicReader.readLine()) != null) {
                String[] lineItems = line.split("\t");
                String filmId = lineItems[ID_INDEX];
                if (filmId.equals("tconst")) continue;

                if (!filmIds.contains(filmId)) continue;

                String[] directorIds = lineItems[DIRECTOR_INDEX].split(",");
                if (directorIds[0].equals("\\N")) continue;


                List<Person> directors = new ArrayList<>();
                for (String directorId: directorIds) {
                    System.out.println("Found director: " + directorId);
                    directors.add(new Person(directorId,null));
                }
                allDirectors.put(filmId,directors);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return allDirectors;
    }

    private Map<String, List<Person>> getAllWriters(List<String> filmIds) {
        Map<String,List<Person>> allWriters = new HashMap<>();

        try (BufferedReader basicReader = new BufferedReader(new FileReader(crewData))) {
            String line = null;
            while ((line = basicReader.readLine()) != null) {
                String[] lineItems = line.split("\t");
                String filmId = lineItems[ID_INDEX];
                if (filmId.equals("tconst")) continue;

                if (!filmIds.contains(filmId)) continue;

                String[] writerIds = lineItems[WRITER_INDEX].split(",");
                if (writerIds[0].equals("\\N")) continue;


                List<Person> writers = new ArrayList<>();
                for (String writerId: writerIds) {
                    System.out.println("Found writer: " + writerId);
                    writers.add(new Person(writerId,null));
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



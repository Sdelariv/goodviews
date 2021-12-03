package be.svend.goodviews.factory;

import be.svend.goodviews.models.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Responsible for scraping duties based on tsv files of the database
 * @Author: Sven Delarivière
 */
@Component
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

    // GENERAL SCRAPER

    /**
     * Goes through all the relevant methods to scrape IMDB and returns a list of all the films with data
     * @return List<Film> - a list of Films with all the data
     */
    public List<Film> scrapeImdb() {
        // Gather ProjectIds with sufficient ratings
        Map<String, Integer> desiredIds = findIdsWithSufficientRatings();

        // Gather only films and their info based on those ids
        List<Film> filmList = gatherFilmInfoFromIds(desiredIds);

        // Gather crewInfo based on the films (and their ids)
        filmList = addCrewIdsToFilms(filmList);
        filmList = addCrewDataToFilmsBasedOnCrewId(filmList);

        return filmList;
    }

    // RATINGS-BASED GATHERING

    /**
     * Goes through the ratings-dataset to find films with enough ratings, and then saves that (and the average rating)
     * @return Map<String-desiredIds, Integer-averageImdbRating> - a map of filmIds with their average rating on IMDB
     */
    public Map<String, Integer> findIdsWithSufficientRatings() {
        Map<String, Integer> ids = new HashMap<>();

        try (BufferedReader basicReader = new BufferedReader(new FileReader(ratingData))) {
            String line = null;
            while ((line = basicReader.readLine()) != null) {
                String[] lineItems = line.split("\t");
                if (lineItems[ID_INDEX].equals("tconst")) continue;

                if (HasSufficientVotesInDataLine(lineItems)) {
                    String id = lineItems[ID_INDEX];
                    Integer averageRating = getAverageRatingImdbFromDataLine(lineItems);
                    ids.put(id, averageRating);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ids;
    }

    /**
     * Checks whether the dataline from ratingData has enough votes
     * @param lineItems lineItem from ratingData
     * @return boolean - true if sufficient votes, false if not
     */
    private boolean HasSufficientVotesInDataLine(String[] lineItems){
        return Integer.parseInt(lineItems[NUMBER_RATINGS_INDEX]) > this.voteMinimum;
    }

    /**
     * Takes the average rating from a dataline and converts it to an integer between 0 and 100
     * @param lineItems String[] - the lineItem read
     * @return Integer - integer of rating between 0 and 100
     */
    private Integer getAverageRatingImdbFromDataLine(String[] lineItems) {
        double averageRating = Double.parseDouble(lineItems[AVERAGE_RATING_INDEX]);
        averageRating = averageRating * 10;

        return (int) averageRating;
    }

    // FILM-DATA GATHERING

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

    // CREW INFO GATHERING

    /**
     * Scrapes the database for directorIds and writerIds and adds them to the crew of the film based on the filmIds
     * @param films List<Film> - a list of films to add crewIds based on its filmId
     * @return List<Film> - a list of films with crewIds added where they were found based on the film's Id
     */
    public List<Film> addCrewIdsToFilms(List<Film> films) {
        List<String> filmIds = films.stream().map(f -> f.getId()).collect(Collectors.toList());
        Map<String, List<Person>> directorsPerFilmId = getAllDirectorIdsPerFilmId(filmIds);
        Map<String, List<Person>> writersPerFilmId = getAllWriterIdsPerFilmId(filmIds);

        for (Film film: films) {
            String filmId = film.getId();

            film.setDirector(directorsPerFilmId.get(filmId));
            System.out.println("Adding " + directorsPerFilmId + " as director(s) of " + film.getTitle());

            film.setWriter(writersPerFilmId.get(filmId));
            System.out.println("Adding " + writersPerFilmId + " as writer(s) of " + film.getTitle());
        }

        return films;
    }

    /**
     * Scrapes the dataset for all the directorIds involved in a specific film (based on its id)
     * @param filmIds List<String> - a list of filmIds for which you want the directorIds
     * @return Map<String-filmId,List<Person> of Directors> - a map of filmIds and their directors
     */
    private Map<String, List<Person>> getAllDirectorIdsPerFilmId(List<String> filmIds) {
        Map<String,List<Person>> allDirectors = new HashMap<>();

        // Reading the dataset
        try (BufferedReader basicReader = new BufferedReader(new FileReader(crewData))) {
            String line = null;
            while ((line = basicReader.readLine()) != null) {
                String[] lineItems = line.split("\t");

                if (!isLineContainingRelevantId(lineItems,filmIds)) continue;

                // Dividing up the directorIds + checking whether the director is known
                String[] directorIds = lineItems[DIRECTOR_INDEX].split(",");
                if (directorIds[0].equals("\\N")) continue;

                // Adding the ids to a list of Directors to be returned
                List<Person> directors = new ArrayList<>();
                for (String directorId: directorIds) {
                    System.out.println("Found director: " + directorId);
                    directors.add(new Person(directorId,null));
                }
                String foundFilmId = lineItems[ID_INDEX];
                allDirectors.put(foundFilmId,directors);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return allDirectors;
    }

    /**
     * Scrapes the dataset for all the writerIds involved in a specific film (based on its id)
     * @param filmIds List<String-filmIds> - a list of filmIds to recover the writerIds from
     * @return Map<String-filmId,List<Person> of Writers> - a map of filmIds and their writerIds
     */
    private Map<String, List<Person>> getAllWriterIdsPerFilmId(List<String> filmIds) {
        Map<String,List<Person>> allWriters = new HashMap<>();

        // Reading the dataset
        try (BufferedReader basicReader = new BufferedReader(new FileReader(crewData))) {
            String line = null;
            while ((line = basicReader.readLine()) != null) {
                String[] lineItems = line.split("\t");

                if (!isLineContainingRelevantId(lineItems,filmIds)) continue;

                // Dividing up the writerIds + checking whether the writer is known
                String[] writerIds = lineItems[WRITER_INDEX].split(",");
                if (writerIds[0].equals("\\N")) continue;

                // Adding the ids to a list of Writers to be returned
                List<Person> writers = new ArrayList<>();
                for (String writerId: writerIds) {
                    System.out.println("Found writer: " + writerId);
                    writers.add(new Person(writerId,null));
                }

                String filmId = lineItems[ID_INDEX];
                allWriters.put(filmId,writers);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return allWriters;
    }

    /**
     * Takes a list of films and returns the list with the crew filled in (provided the crew has ids)
     * @param filmList List<Film> - a list of films (with crew having ids) for which the writer/director data needs filling in
     * @return List<Film> - of films with the writer and director data filled in where there were ids for them
     */
    public List<Film> addCrewDataToFilmsBasedOnCrewId(List<Film> filmList) {
        List<String> idsDesiredCrew = getCrewIdsFromFilms(filmList);

        System.out.println("Scraping relevant crew");
        Map<String, String> mapOfRelevantCrew = getRelevantCrewMapFromIds(idsDesiredCrew);

        for (Film film: filmList) {

            if (film.getDirector() != null) {
                film.setDirector(extractPersonsFromCrewMap(film.getDirector(),mapOfRelevantCrew));
                System.out.println("Added " + film.getDirector() + " to " + film.getTitle());
            }

            if (film.getWriter() != null) {
                film.setWriter(extractPersonsFromCrewMap(film.getWriter(),mapOfRelevantCrew));
                System.out.println("Added " + film.getDirector() + " to " + film.getTitle());
            }
        }

        return filmList;
    }

    /**
     * Will, given a list of people with ids fill in the names extracted from a map with ids and names.
     * Can be used for all manner of crew as long as only the name needs filling in.
     * @param personWithIdToBeNamed  List<Person> - a list of people where Id is present and name needs filling in
     * @param crewMapIdAndName  Map<Id-String,Name-String> - a pre-made map of ids and names from which the name will get extracted
     * @return List<Person> - a list of people with ids and names
     */
    public List<Person> extractPersonsFromCrewMap(List<Person> personWithIdToBeNamed, Map<String,String> crewMapIdAndName) {

        for (Person person: personWithIdToBeNamed) {
            String directorId = person.getId();

            if (crewMapIdAndName.containsKey(directorId)) {
                person.setName(crewMapIdAndName.get(directorId));
            }
        }

        return personWithIdToBeNamed;
    }

    /**
     * Scrapes the dataset personData to create a Map of names for the desired crewIds
     * @param idsDesiredCrew (a list of id Strings for the crew that you want the info extracted of)
     * @return Map<String-crewId, String-crewName> - a Map with the id-String and the name-String
     */
    private Map<String,String> getRelevantCrewMapFromIds(List<String> idsDesiredCrew) {
        Map<String,String> relevantCrew = new HashMap<>();

        try (BufferedReader basicReader = new BufferedReader(new FileReader(personData))) {
            String line = null;
            while ((line = basicReader.readLine()) != null) {
                String[] lineItems = line.split("\t");

                if(!isLineContainingRelevantId(lineItems,idsDesiredCrew)) continue;

                String foundCrewId = lineItems[ID_INDEX];
                String foundCrewName = lineItems[CREW_NAME_INDEX];

                System.out.println("Found " + foundCrewName);
                relevantCrew.put(foundCrewId,foundCrewName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return relevantCrew;
    }

    /**
     * Extracts a list of only the crewIds inside a list of Films. Can be used for contains(id) methods.
     * @param filmList List<Film> a list of Films with crewIds that need extracting
     * @return List<String-crewId> - a list of crewIds (of the films given)
     */
    private List<String> getCrewIdsFromFilms(List<Film> filmList) {
        List<String> crewIds = new ArrayList<>();

        for (Film film: filmList) {
            if (film.getDirector() != null) film.getDirector().stream().forEach(d -> crewIds.add(d.getId()));
            if (film.getWriter() != null) film.getWriter().stream().forEach(w -> crewIds.add(w.getId()));
        }

        return crewIds;
    }

    // VALIDATING METHODS

    /**
     * Validates whether the line is about a relevant id
     * @param lineItems the line read from a dataset
     * @param idsDesired List<String-id>the list of Ids that are considered relevant
     * @return boolean - true if about a relevant id, false if not.
     */
    private boolean isLineContainingRelevantId(String[] lineItems, List<String> idsDesired) {
        String foundId = lineItems[ID_INDEX];

        if (foundId.equals("tconst")) return false;

        if (!idsDesired.contains(foundId)) return false;

        return true;
    }

    /**
     * Validates whether the line is about a relevant id
     * @param lineItems String[] the read lineItem
     * @param idsDesiredCrew Map<String-id,Integer-averageRating> used solely for the ids
     * @return boolean - true if about a relevant id, false if not.
     */
    private boolean isLineContainingRelevantId(String[] lineItems, Map<String,Integer> idsDesiredCrew) {
        String foundId = lineItems[ID_INDEX];

        if (foundId.equals("tconst")) return false;

        if (!idsDesiredCrew.containsKey(foundId)) return false;

        return true;
    }

    /**
     * Checks whether the line in basicData is a film or not
     * @param lineItems - a lineItem from basicData
     * @return boolean - true if it's about a film, false if not
     */
    private boolean isFilmInDataLine(String[] lineItems) {
        if (lineItems[TYPE_INDEX].equals("movie")) return true;
        else return false;
    }



    // INTERNAL METHODS


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



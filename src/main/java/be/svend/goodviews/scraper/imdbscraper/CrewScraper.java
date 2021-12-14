package be.svend.goodviews.scraper.imdbscraper;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.Person;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static be.svend.goodviews.scraper.imdbscraper.LineValidator.isLineContainingRelevantId;

/**
 * Responsible for scraping duties based on tsv files of the crewData and personData
 * @Author: Sven Delarivière
 */
public class CrewScraper {
    private File crewData;
    private File personData;

    // For crewData
    private final int ID_INDEX = 0;
    private final int DIRECTOR_INDEX = 1;
    private final int WRITER_INDEX = 2;

    // For peopleData
    private final int CREW_NAME_INDEX = 1;

    public CrewScraper(String parentFolderPath) {
        crewData = new File(parentFolderPath + "title.crew.tsv/data.tsv");
        personData = new File(parentFolderPath + "name.basics.tsv/data.tsv");
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


}

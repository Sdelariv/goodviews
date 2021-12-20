package be.svend.goodviews.services.film;

import be.svend.goodviews.scraper.webscraper.WebScraper;
import be.svend.goodviews.models.*;
import be.svend.goodviews.repositories.FilmRepository;
import be.svend.goodviews.repositories.GenreRepository;
import be.svend.goodviews.repositories.RatingRepository;
import be.svend.goodviews.services.film.properties.TagService;
import be.svend.goodviews.services.crew.PersonService;
import be.svend.goodviews.services.update.LogUpdateService;
import org.springframework.stereotype.Service;

import java.util.*;


import static be.svend.goodviews.services.film.FilmValidator.filmHasValidIdFormat;
import static be.svend.goodviews.services.film.FilmValidator.isValidFilmIdFormat;

@Service
public class FilmService {
    FilmRepository filmRepo;
    FilmValidator filmValidator;
    RatingRepository ratingRepo;
    GenreRepository genreRepo;
    TagService tagService;

    PersonService personService; // Using PersonService to find a films with a particular director or writer, but could use Repo I think
    LogUpdateService logUpdateService; // Used to log

    // CONSTRUCTOR

    public FilmService(FilmRepository filmRepo,
                       FilmValidator filmValidator,
                       PersonService personService,
                       RatingRepository ratingRepo,
                       GenreRepository genreRepo,
                       TagService tagService,
                       LogUpdateService logUpdateService) {
        this.filmRepo = filmRepo;
        this.filmValidator = filmValidator;
        this.personService = personService;
        this.ratingRepo = ratingRepo;
        this.genreRepo = genreRepo;
        this.tagService = tagService;
        this.logUpdateService = logUpdateService;
    }

    // FIND METHODS

    public Optional<Film> findById(String id) {
        if (id == null) return Optional.empty();

        Optional<Film> foundFilm = filmRepo.findById(id);

        if (foundFilm.isEmpty()) return Optional.empty();

        return foundFilm;
    }

    public List<Film> findByGenre(Genre genre) {
        if (genre.getId() == null) {
            Optional<Genre> foundGenre = genreRepo.findByName(genre.getName());
            if (foundGenre.isPresent()) return filmRepo.findAllByGenresContaining(foundGenre.get());
        }
        return filmRepo.findAllByGenresContaining(genre);
    }

    public List<Film> findByGenres(List<Genre> genres) {
        return filmRepo.findAllByGenresIn(genres);
    }

    public List<Film> findByTag(Tag tag) {
        return filmRepo.findAllByTagsContaining(tag);
    }

    public List<Film> findByTags(List<Tag> tags) {
        return filmRepo.findAllByTagsIn(tags);
    }


    public List<Film> findFilmsByDirectorId(String directorId) {
        if (directorId == null) return Collections.emptyList();

        Optional<Person> director = personService.findPersonById(directorId);
        if (director.isEmpty()) return Collections.emptyList();


        List<Film> filmsByDirector = filmRepo.findFilmsByDirectorContaining(director.get());

        return filmsByDirector;
    }

    public List<Film> findFilmsByWriterId(String writerId) {
        if (writerId == null) return Collections.emptyList();

        Optional<Person> director = personService.findPersonById(writerId);
        if (director.isEmpty()) return Collections.emptyList();

        List<Film> filmsByDirector = filmRepo.findFilmsByWriterContaining(director.get());
        return filmsByDirector;
    }


    public List<Film> findByTitle(String name) {

        List<Film> foundFilms = filmRepo.findFilmsByTitle(name);
        foundFilms.addAll(filmRepo.findByTranslatedTitle(name));

        return foundFilms;
    }

    public List<Film> findAllFilms() {
        return filmRepo.findAll();
    }

    // CREATE METHODS

    public List<Film> createFilms(List<Film> films) {
        List<Film> createdFilms = new ArrayList<>();

        for (Film film: films) {
            Optional<Film> createdFilm = createFilm(film);
            if (createdFilm.isPresent()) createdFilm = updateFilmAddWebDataByImdbId(createdFilm.get().getId()); // TODO: Take out if you don't want this to be automatic
            if (createdFilm.isPresent()) createdFilms.add(createdFilm.get());
        }

        return createdFilms;
    }

    public Optional<Film> createFilm(Film film) {
        if (findFilmByFilm(film).isPresent()) {
            System.out.println("Can't create a film with an id already in the db");
            return Optional.empty();
        }

        if (!filmHasValidIdFormat(film)) {
            System.out.println("Can't create a film that doesn't have a valid id format: " + film.getId());
            return Optional.empty();
        }

        Film createdFilm = initialiseAndSaveFilm(film);
        return Optional.of(createdFilm);
    }


    public Optional<Film> createFilmByImdbId(String imdbId) {
       Optional<Film> createdFilm = fetchFilmByImdbId(imdbId);

        // Save if present
        if (createdFilm.isEmpty()) return Optional.empty();
        initialiseAndSaveFilm(createdFilm.get());

        // Log
        logUpdateService.createGeneralLog("Created film: " + createdFilm.get().getTitle());

        return createdFilm;
    }

    public Optional<Film> fetchFilmByImdbId(String imdbId) {
        // Check whether existing
        if (imdbId == null) return Optional.empty();
        if (findById(imdbId).isPresent()) {
            System.out.println("Can't create a film with an id that is already in the db");
            return Optional.empty();
        }

        // Get data
        return WebScraper.createFilmWithWebData(imdbId);
    }

    // UPDATE METHODS

    public List<Film> updateFilms(List<Film> films) {
        List<Film> updatedFilms = new ArrayList<>();

        for (Film film: films) {
            Optional<Film> updatedFilm = updateFilm(film);
            if (updatedFilm.isPresent()) updatedFilms.add(updatedFilm.get());
        }

        return updatedFilms;
    }

    public Optional<Film> updateFilm(Film film) {
        Optional<Film> existingFilm = findFilmByFilm(film);

        if (existingFilm.isEmpty()) {
            System.out.println("Can't update a film with id not in database");
            return Optional.empty();
        } else {
            initialiseAndSaveFilm(film);

            return Optional.of(film);
        }
    }

    public List<Film> updateFilmsAddWebDataByImdbId(List<String> filmIds) {
        List<Film> films = new ArrayList<>();

        for (String id: filmIds) {
            Optional<Film> film = updateFilmAddWebDataByImdbId(id);
            if (film.isPresent()) films.add(film.get());
        }

        return films;
    }

    public Optional<Film> updateFilmAddWebDataByImdbId(String filmId) {
        // Checks
        if (!isValidFilmIdFormat(filmId)) {
            System.out.println("Can't update a film with an invalid id");
            return Optional.empty();
        }

        Optional<Film> existingFilm = findById(filmId);
        if (findById(filmId).isEmpty()) {
            System.out.println("Can't update af ilm that's not in the db");
            return Optional.empty();
        }

        // Get data
        System.out.println("Fetching Web data for " + existingFilm.get().getTitle());
        Optional<Film> updatedFilm = WebScraper.addWebData(existingFilm.get());

        // Sava data
        if (updatedFilm.isEmpty()) {
            System.out.println("Couldn't find webData");
            return Optional.empty();
        }
        updatedFilm = Optional.of(initialiseAndSaveFilm(updatedFilm.get()));

        System.out.println("Updated (Added IMDB data) " + updatedFilm.get().getTitle());
        return updatedFilm;
    }

    public List<Film> updateFilmsReplaceWithWebDataByImdbId(List<String> filmIds) {
        List<Film> films = new ArrayList<>();

        for (String id: filmIds) {
            Optional<Film> film = updateFilmReplaceWithWebDataByImdbId(id);
            if (film.isPresent()) films.add(film.get());
        }

        return films;
    }

    public Optional<Film> updateFilmReplaceWithWebDataByImdbId(String filmId) {
        // Checks
        if (!isValidFilmIdFormat(filmId)) {
            System.out.println("Can't update a film that with an invalid id");
            return Optional.empty();
        }

        Optional<Film> existingFilm = findById(filmId);
        if (findById(filmId).isEmpty()) {
            System.out.println("Can't update a film that is nto in the db");
            return Optional.empty();
        }

        // Get data
        Optional<Film> updatedFilm = WebScraper.replaceWithWebData(existingFilm.get());

        // Save data if present
        if (updatedFilm.isEmpty()) return Optional.empty();
        updatedFilm = Optional.of(initialiseAndSaveFilm(updatedFilm.get()));

        System.out.println("Updated (Replace with IMDB data)" + updatedFilm.get().getTitle());
        return updatedFilm;
    }

    public Optional<Film> calculateAndUpdateAverageRatingByFilmId(String filmId) {
        Optional<Film> film = findById(filmId);
        if (film.isEmpty()) return Optional.empty();

        Integer calculatedAverage = calculateAverageRatingByFilmId(filmId);
        film.get().setAverageRating(calculatedAverage);

        filmRepo.save(film.get());
        System.out.println("Average updated");
        return film;
    }

    public Optional<Film> addGenreBasedOnFilmId(String filmId, Genre genre) {
        // Finding film
        Optional<Film> film = findById(filmId);
        if (film.isEmpty()) return Optional.empty();
        Film foundFilm = film.get();

        // Checking if genre already present
        if (foundFilm.getGenres().contains(genre)) return film;

        // Add and save genre + log
        foundFilm.addGenre(genre);
        logUpdateService.createGeneralLog(genre.getName() + " has been added to " + foundFilm.getTitle());
        return Optional.of(initialiseAndSaveFilm(foundFilm));
    }

    public Optional<Film> addTagBasedOnFilmIdAndTagString(String filmId, String tagString) {
        // Finding film
        Optional<Film> film = findById(filmId);
        if (film.isEmpty()) return Optional.empty();
        Film foundFilm = film.get();

        // Checking if tag already exists (and whether the film already has it if so)
        Tag tagToAdd;
        Optional<Tag> tagInDb = tagService.findByName(tagString);
        if (tagInDb.isPresent()) tagToAdd = tagInDb.get();
        else tagToAdd = new Tag(tagString);

        // Check if film has tag already
        if (foundFilm.getTags().contains(tagToAdd)) {
            System.out.println("Film already has tag");
            return film;
        }

        // Add and save tag + log
        foundFilm.addTag(tagToAdd);
        logUpdateService.createGeneralLog(tagToAdd.getName() + " has been added to " + foundFilm.getTitle());
        return Optional.of(initialiseAndSaveFilm(foundFilm));
    }

    // DELETE methods

    public void deleteFilms(List<Film> films) {

        for (Film film: films) {
            deleteFilm(film);
        }
    }

    public void deleteFilm(Film film) {
        Optional<Film> existingFilm = findFilmByFilm(film);

        if (existingFilm.isEmpty()) {
            System.out.println("Can't delete a film with id not in database");
            return;
        } else {
            System.out.println("Deleting " + film.getTitle());
            logUpdateService.createGeneralLog("Deleted " + film.getTitle());
            filmRepo.deleteById(film.getId());
        }
    }



    // INTERNAL

    private Optional<Film> findFilmByFilm(Film film) {
        return findById(film.getId());
    }


    /** Saves film and all its unknown properties after all other checks have been completed
     * @param film
     * @return the initialised film with all its ids
     */
    private Film initialiseAndSaveFilm(Film film) {
        Film initialisedFilm = filmValidator.initialise(film);

        filmRepo.save(initialisedFilm);
        System.out.println("Saved the following Film:");
        System.out.println(film);

        return film;
    }


    /**
     * Goes through all the ratings of the film in the db, calculates the average and returns that average
     * @param filmId - the Id of the film from which the average-calculation is requested
     * @return Integer - the average
     */
    public Integer calculateAverageRatingByFilmId(String filmId) {
        List<Rating> ratings = ratingRepo.findByFilm_Id(filmId);

        if (ratings.size() == 0) return null;

        Integer runningTotal = 0;
        for (Rating rating: ratings) {
            runningTotal = runningTotal + rating.getRatingValue();
        }

        return runningTotal / ratings.size();
    }


}

package be.svend.goodviews.services.notification;

import be.svend.goodviews.models.*;
import be.svend.goodviews.models.notification.FilmSuggestion;
import be.svend.goodviews.models.notification.GenreSuggestion;
import be.svend.goodviews.models.notification.Notification;
import be.svend.goodviews.models.notification.TagSuggestion;
import be.svend.goodviews.repositories.GenreRepository;
import be.svend.goodviews.repositories.notification.FilmSuggestionRepository;
import be.svend.goodviews.repositories.notification.GenreSuggestionRepository;
import be.svend.goodviews.repositories.notification.NotificationRepository;
import be.svend.goodviews.repositories.notification.TagSuggestionRepository;
import be.svend.goodviews.services.film.FilmService;
import be.svend.goodviews.services.film.FilmValidator;
import be.svend.goodviews.services.users.UserValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SuggestionService {
    NotificationRepository notificationRepo;
    GenreRepository genreRepo;
    GenreSuggestionRepository genreSuggestionRepo;
    TagSuggestionRepository tagSuggestionRepo;
    FilmSuggestionRepository filmSuggestionRepo;

    FilmValidator filmValidator;
    UserValidator userValidator;

    FilmService filmService; // For creating genres and updating films
    NotificationService notificationService; // For deleting or saving the suggestions

    // CONSTRUCTOR

    public SuggestionService(NotificationRepository notificationRepo,
                             GenreRepository genreRepo,
                             FilmValidator filmValidator,
                             UserValidator userValidator,
                             FilmService filmService,
                             GenreSuggestionRepository genreSuggestionRepo,
                             TagSuggestionRepository tagSuggestionRepo,
                             NotificationService notificationService,
                             FilmSuggestionRepository filmSuggestionRepo) {
        this.notificationRepo = notificationRepo;
        this.genreRepo = genreRepo;
        this.filmValidator = filmValidator;
        this.userValidator = userValidator;
        this.filmService = filmService;
        this.genreSuggestionRepo = genreSuggestionRepo;
        this.tagSuggestionRepo = tagSuggestionRepo;
        this.notificationService = notificationService;
        this.filmSuggestionRepo = filmSuggestionRepo;
    }

    // FIND METHODS

    public List<Notification> findAllAdminNotifications() {
        return notificationRepo.findAllByTypeOfUser(TypeOfUser.ADMIN);
    }

    public List<Notification> findAllArchitectNotifications() {
        return notificationRepo.findAllByTypeOfUser(TypeOfUser.ARCHITECT);
    }

    public List<GenreSuggestion> findAllGenreSuggestions() {
        return genreSuggestionRepo.findAll();
    }

    public List<TagSuggestion> findAllTagSuggestions() {
        return tagSuggestionRepo.findAll();
    }

    public List<FilmSuggestion> findAllFilmSuggestions() { return filmSuggestionRepo.findAll(); }

    // CREATE METHODS
    public boolean sendGenreSuggestion(String suggestedGenreName, Film film, User suggester) {

        // See if film already has that genre
        if (film.getGenres().contains(new Genre(suggestedGenreName))) {
            System.out.println("Film already has that genre");
            return false;
        }

       // See if suggestion already exists
        Optional<GenreSuggestion> genreSuggestion = createGenreSuggestion(suggestedGenreName,film,suggester);
        if (genreSuggestion.isEmpty()) return false;

        // Send(Save)
        notificationRepo.save(genreSuggestion.get());
        System.out.println("Genre suggestion sent");
        return true;
    }

    public boolean sendTagSuggestion(String suggestedTagName, Film film, User suggester) {
        // Check if existing  TODO: Will have to move this to controller
        if (userValidator.isExistingUser(suggester).isEmpty()) return false;
        if (filmValidator.isExistingFilm(film).isEmpty()) return false;
        else film = filmValidator.isExistingFilm(film).get();

        // Check if film already has tag
        if (film.getTags().contains(new Tag(suggestedTagName))) {
            System.out.println("Film already has that tag");
            return false;
        }

        // Check if suggestion exists
        Optional<TagSuggestion> tagSuggestion = createTagSuggestion(suggestedTagName,film,suggester);
        if (tagSuggestion.isEmpty()) return false;

        // Send(Save)
        notificationRepo.save(tagSuggestion.get());
        System.out.println("Tag suggestion sent");
        return true;
    }

    public boolean sendFilmSuggestion(String suggestedFilmId, User suggester) {
        // Check if existing
        if (userValidator.isExistingUser(suggester).isEmpty()) {
            System.out.println("Invalid user");
            return false;
        }

        // Check if film is already in Db
        suggestedFilmId = suggestedFilmId.trim();
        if (filmValidator.isExistingFilmId(suggestedFilmId).isPresent()) {
            System.out.println("Film already exists in db");
            return false;
        }

        // Check if film exists on IMDB
        if (!filmValidator.isValidFilmIdFormat(suggestedFilmId)) return false;
        Optional<Film> filmFromIMDB = filmService.fetchFilmByImdbId(suggestedFilmId);
        if (filmFromIMDB.isEmpty()) {
            System.out.println("Invalid imdb id.");
            return false;
        }

        // Create notification
        Optional<FilmSuggestion> filmSuggestion = createFilmSuggestion(suggestedFilmId,suggester, filmFromIMDB.get().getTitle());
        if (filmSuggestion.isEmpty()) return false;

        // Send(Save)
        notificationRepo.save(filmSuggestion.get());
        System.out.println("Film suggestion sent");
        return true;
    }


    // UPDATE METHODS

    // TODO: This method should be in the controller?
    public void acceptGenre(GenreSuggestion genreSuggestion) {
        Genre genre = new Genre(genreSuggestion.getSuggestedGenreName());
        filmService.addGenreBasedOnFilmId(genreSuggestion.getFilm().getId(),genre);

        Notification accepted = new Notification();
        accepted.setTargetUser(genreSuggestion.getOriginUser());
        accepted.setMessage("Your genresuggestions: " + genreSuggestion.getSuggestedGenreName() + " for " + genreSuggestion.getFilm().getTitle() + " has been accepted");
        notificationRepo.save(accepted);

        notificationService.deleteNotification(genreSuggestion);
    }

    // TODO: This method should be in the controller?
    public void acceptTag(TagSuggestion tagSuggestion) {
        filmService.addTagBasedOnFilmIdAndTagString(tagSuggestion.getFilm().getId(),tagSuggestion.getSuggestedTagName());

        Notification accepted = new Notification();
        accepted.setTargetUser(tagSuggestion.getOriginUser());
        accepted.setMessage("Your tag suggestion: " + tagSuggestion.getSuggestedTagName() + " for " + tagSuggestion.getFilm().getTitle() + " has been accepted");
        notificationRepo.save(accepted);

        notificationService.deleteNotification(tagSuggestion);
    }

    // TODO: This method should be in the controller?
    public void acceptFilm(FilmSuggestion filmSuggestion) {
        filmService.createFilmByImdbId(filmSuggestion.getSuggestedFilmId());

        Notification accepted = new Notification();
        accepted.setTargetUser(filmSuggestion.getOriginUser());
        accepted.setMessage("Your film suggestion of " + filmSuggestion.getFilmTitle() + " has been accepted");
        notificationRepo.save(accepted);

        notificationService.deleteNotification(filmSuggestion);
    }

    // DELETE METHODS

    // TODO: Method should be in controller?
    public boolean denyGenre(GenreSuggestion genreSuggestion) {
        return notificationService.deleteNotification(genreSuggestion);
    }

    public boolean denyTag(TagSuggestion tagSuggestion) {
        return notificationService.deleteNotification(tagSuggestion);
    }

    public boolean denyFilm(FilmSuggestion filmSuggestion) {
        return notificationService.deleteNotification(filmSuggestion);
    }

    // INTERNAL METHODS

    private Optional<GenreSuggestion> createGenreSuggestion(String suggestedGenreName, Film film, User suggester) {
        GenreSuggestion genreSuggestion = new GenreSuggestion();
        genreSuggestion.setSuggestedGenreName(suggestedGenreName);
        genreSuggestion.setFilm(film);
        genreSuggestion.setSuggester(suggester);

        if (genreSuggestionRepo.findByFilmAndSuggestedGenreName(film,suggestedGenreName).isPresent()) {
            System.out.println("Genre suggestion already exists");
            return Optional.empty();
        }
        return Optional.of(genreSuggestion);
    }

    private Optional<TagSuggestion> createTagSuggestion(String suggestedTagName, Film film, User suggester) {
        TagSuggestion tagSuggestion = new TagSuggestion();
        tagSuggestion.setSuggestedTagName(suggestedTagName);
        tagSuggestion.setFilm(film);
        tagSuggestion.setOriginUser(suggester);

        if (tagSuggestionRepo.findByFilmAndSuggestedTagName(film,suggestedTagName).isPresent()) {
            System.out.println("Tag suggestion already exists");
            return Optional.empty();
        }

        return Optional.of(tagSuggestion);
    }

    private Optional<FilmSuggestion> createFilmSuggestion(String suggestedFilmId, User suggester, String title) {
        FilmSuggestion filmSuggestion = new FilmSuggestion();
        filmSuggestion.setSuggester(suggester);
        filmSuggestion.setSuggestedFilmId(suggestedFilmId);
        filmSuggestion.setFilmTitle(title);

        if (filmSuggestionRepo.findBySuggestedFilmId(suggestedFilmId).isPresent()) {
            System.out.println("Film suggestion already exists");
            return Optional.empty();
        }

        return Optional.of(filmSuggestion);
    }

}

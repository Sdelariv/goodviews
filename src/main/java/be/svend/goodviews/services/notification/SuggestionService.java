package be.svend.goodviews.services.notification;

import be.svend.goodviews.models.*;
import be.svend.goodviews.models.notification.GenreSuggestion;
import be.svend.goodviews.models.notification.Notification;
import be.svend.goodviews.models.notification.TagSuggestion;
import be.svend.goodviews.repositories.GenreRepository;
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
                             NotificationService notificationService) {
        this.notificationRepo = notificationRepo;
        this.genreRepo = genreRepo;
        this.filmValidator = filmValidator;
        this.userValidator = userValidator;
        this.filmService = filmService;
        this.genreSuggestionRepo = genreSuggestionRepo;
        this.tagSuggestionRepo = tagSuggestionRepo;
        this.notificationService = notificationService;
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

    // CREATE METHODS
    public boolean sendGenreSuggestion(String suggestedGenreName, Film film, User suggester) {
        // Check if existing  TODO: Will have to move this to controller
        if (userValidator.isExistingUser(suggester).isEmpty()) return false;
        if (filmValidator.isExistingFilm(film).isEmpty()) return false;
        else film = filmValidator.isExistingFilm(film).get();

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
        System.out.println("Sent suggestion");
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
        System.out.println("Sent suggestion");
        return true;
    }


    // UPDATE METHODS

    // TODO: This method should be in the controller?
    public void acceptGenre(GenreSuggestion genreSuggestion) {
        Genre genre = new Genre(genreSuggestion.getSuggestedGenreName());
        filmService.addGenreBasedOnFilmId(genreSuggestion.getFilm().getId(),genre);

        notificationService.deleteNotification(genreSuggestion);

        Notification accepted = new Notification();
        accepted.setTargetUser(genreSuggestion.getOriginUser());
        accepted.setMessage("Your genresuggestions: " + genreSuggestion.getSuggestedGenreName() + " for " + genreSuggestion.getFilm().getTitle() + " has been accepted");
        notificationRepo.save(accepted);

        return;
    }

    // TODO: This method should be in the controller?
    public void acceptTag(TagSuggestion tagSuggestion) {
        filmService.addTagBasedOnFilmIdAndTagString(tagSuggestion.getFilm().getId(),tagSuggestion.getSuggestedTagName());

        notificationService.deleteNotification(tagSuggestion);

        Notification accepted = new Notification();
        accepted.setTargetUser(tagSuggestion.getOriginUser());
        accepted.setMessage("Your tag suggestion: " + tagSuggestion.getSuggestedTagName() + " for " + tagSuggestion.getFilm().getTitle() + " has been accepted");
        notificationRepo.save(accepted);

        return;
    }

    // DELETE METHODS

    // TODO: Method should be in controller?
    public boolean denyGenre(GenreSuggestion genreSuggestion) {
        return notificationService.deleteNotification(genreSuggestion);
    }

    public boolean denyTag(TagSuggestion tagSuggestion) {
        return notificationService.deleteNotification(tagSuggestion);
    }

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

}

package be.svend.goodviews.services.notification;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.Genre;
import be.svend.goodviews.models.TypeOfUser;
import be.svend.goodviews.models.User;
import be.svend.goodviews.models.notification.GenreSuggestion;
import be.svend.goodviews.models.notification.Notification;
import be.svend.goodviews.repositories.GenreRepository;
import be.svend.goodviews.repositories.notification.GenreSuggestionRepository;
import be.svend.goodviews.repositories.notification.NotificationRepository;
import be.svend.goodviews.services.film.FilmService;
import be.svend.goodviews.services.film.FilmValidator;
import be.svend.goodviews.services.film.properties.GenreService;
import be.svend.goodviews.services.users.UserValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuggestionService {
    NotificationRepository notificationRepo;
    GenreRepository genreRepo;
    GenreSuggestionRepository genreSuggestionRepo;

    FilmValidator filmValidator;
    UserValidator userValidator;

    FilmService filmService; // For creating genres and updating films

    // CONSTRUCTOR

    public SuggestionService(NotificationRepository notificationRepo,
                             GenreRepository genreRepo,
                             FilmValidator filmValidator,
                             UserValidator userValidator,
                             FilmService filmService,
                             GenreSuggestionRepository genreSuggestionRepo) {
        this.notificationRepo = notificationRepo;
        this.genreRepo = genreRepo;
        this.filmValidator = filmValidator;
        this.userValidator = userValidator;
        this.filmService = filmService;
        this.genreSuggestionRepo = genreSuggestionRepo;
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

    // CREATE METHODS
    public boolean createGenreSuggestion(String suggestedGenreName, Film film, User suggester) {
        // Check if existing  TODO: Will have to move this to controller
        if (userValidator.isExistingUser(suggester).isEmpty()) return false;
        if (filmValidator.isExistingFilm(film).isEmpty()) return false;

        // Create and check if exists
        GenreSuggestion genreSuggestion = new GenreSuggestion();
        genreSuggestion.setSuggestedGenreName(suggestedGenreName);
        genreSuggestion.setFilm(film);
        genreSuggestion.setSuggester(suggester);
        if (genreSuggestionRepo.findByFilmAndSuggestedGenreName(film,suggestedGenreName).isPresent()) {
            System.out.println("Genre suggestion already exists");
            return false;
        }

        // Save
        notificationRepo.save(genreSuggestion);

        System.out.println("Sent suggestion");
        return true;
    }

    // TODO: This method should be in the controller?
    public void acceptGenre(GenreSuggestion genreSuggestion) {
        Genre genre = new Genre(genreSuggestion.getSuggestedGenreName());
        filmService.addGenreBasedOnFilmId(genreSuggestion.getFilm().getId(),genre);

        deleteGenreSuggestion(genreSuggestion);

        Notification accepted = new Notification();
        accepted.setTargetUser(genreSuggestion.getOriginUser());
        accepted.setMessage("Your genresuggestions: " + genreSuggestion.getSuggestedGenreName() + " for " + genreSuggestion.getFilm().getTitle() + " has been accepted");
        notificationRepo.save(accepted);

        return;
    }

    // TODO: Method should be in controller?
    public boolean denyGenre(GenreSuggestion genreSuggestion) {
        return deleteGenreSuggestion(genreSuggestion);
    }

    // DELETE METHODS

    public boolean deleteGenreSuggestion(GenreSuggestion genreSuggestion) {
        if (genreSuggestion == null || genreSuggestion.getId() == null ) return false;
        if (notificationRepo.findById(genreSuggestion.getId()).isEmpty()) return false;

        notificationRepo.delete(genreSuggestion);
        System.out.println("GenreSuggestion Notification deleted");
        return true;
    }
}

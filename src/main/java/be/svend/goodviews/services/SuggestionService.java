package be.svend.goodviews.services;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.Genre;
import be.svend.goodviews.models.User;
import be.svend.goodviews.models.notification.GenreSuggestion;
import be.svend.goodviews.repositories.GenreRepository;
import be.svend.goodviews.repositories.NotificationRepository;
import be.svend.goodviews.services.film.FilmValidator;
import be.svend.goodviews.services.film.properties.GenreService;
import be.svend.goodviews.services.users.UserValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuggestionService {
    NotificationRepository notificationRepo;
    GenreRepository genreRepo;
    FilmValidator filmValidator;
    UserValidator userValidator;

    GenreService genreService; // For creating accepted genres

    // CONSTRUCTOR

    public SuggestionService(NotificationRepository notificationRepo,
                             GenreRepository genreRepo,
                             FilmValidator filmValidator,
                             UserValidator userValidator,
                             GenreService genreService) {
        this.notificationRepo = notificationRepo;
        this.genreRepo = genreRepo;
        this.filmValidator = filmValidator;
        this.userValidator = userValidator;
        this.genreService = genreService;
    }

    // FIND METHODS
    public List<GenreSuggestion> findAllGenreSuggestions() {
        return notificationRepo.findAllGenreSuggestionNotifications();
    }

    // CREATE METHODS
    public boolean createGenreSuggestion(String suggestedGenreName, Film film, User suggester) {
        // Check if existing  TODO: Will have to move this to controller
        if (genreRepo.findByName(suggestedGenreName).isPresent()) {
            System.out.println("Genre already exists");
            return false;
        }
        if (userValidator.isExistingUser(suggester).isEmpty()) return false;
        if (filmValidator.isExistingFilm(film).isEmpty()) return false;

        // Create
        GenreSuggestion genreSuggestion = new GenreSuggestion();
        genreSuggestion.setSuggestedGenreName(suggestedGenreName);
        genreSuggestion.setFilm(film);
        genreSuggestion.setSuggester(suggester);
        notificationRepo.save(genreSuggestion);

        System.out.println("Sent suggestion");
        return true;
    }

    // TODO: This method should be in the controller
    public Genre acceptGenre(GenreSuggestion genreSuggestion) {
        Genre genre = new Genre(genreSuggestion.getSuggestedGenreName());
        Genre savedGenre = genreService.saveGenre(genre);

        deleteGenreSuggestion(genreSuggestion);
        return savedGenre;
    }

    // TODO: Method should be in controller
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

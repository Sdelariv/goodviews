package be.svend.goodviews.services.rating;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.User;
import be.svend.goodviews.models.WantToSee;
import be.svend.goodviews.repositories.WantToSeeRepository;
import be.svend.goodviews.services.film.FilmValidator;
import be.svend.goodviews.services.update.LogUpdateService;
import be.svend.goodviews.services.users.UserValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WantToSeeService {
    WantToSeeRepository wantToSeeRepo;

    FilmValidator filmValidator;
    UserValidator userValidator;
    RatingValidator ratingValidator;

    LogUpdateService logUpdateService;

    // CONSTRUCTOR

    public WantToSeeService(WantToSeeRepository wantToSeeRepo,
                            FilmValidator filmValidator,
                            UserValidator userValidator,
                            RatingValidator ratingValidator,
                            LogUpdateService logUpdateService) {
        this.wantToSeeRepo = wantToSeeRepo;
        this.filmValidator = filmValidator;
        this.userValidator = userValidator;
        this.ratingValidator = ratingValidator;

        this.logUpdateService = logUpdateService;
    }


    // FIND METHODS

    public List<WantToSee> findByUser(User user) {
        return wantToSeeRepo.findAllByUser(user);
    }

    public List<WantToSee> findByUsers(List<User> users) {
        List<WantToSee> allWantToSee = new ArrayList<>();

        for (User user: users){
            allWantToSee.addAll(findByUser(user));
        }

        return allWantToSee;
    }

    public Optional<WantToSee> findByUserAndFilm(User user, Film film) {
        return wantToSeeRepo.findByUserAndFilm(user,film);
    }

    // CREATE METHODS

    public Optional<WantToSee> createWantToSee(User user, Film film) {
        WantToSee wantToSee = new WantToSee();

        if (filmValidator.isExistingFilm(film).isEmpty()) {
            System.out.println("Invalid film");
            return Optional.empty();
        }
        wantToSee.setFilm(film);

        if (userValidator.isExistingUser(user).isEmpty()) {
            System.out.println("Invalid user");
            return Optional.empty();
        }
        wantToSee.setUser(user);

        if (ratingValidator.ratingIdInDatabase(user.getUsername() + film.getId()).isPresent()) {
            System.out.println("Film already rated");
            return Optional.empty();
        }

        if (wantToSeeRepo.findByUserAndFilm(user, film).isPresent()) {
            System.out.println("Want-To-See already exists");
            return Optional.empty();
        }

        logUpdateService.createWantToSeeUpdate(user,film);

        return Optional.of(wantToSeeRepo.save(wantToSee));
    }

    // DELETE METHODS

    public boolean deleteWantToSee(WantToSee wantToSee) {
        if (wantToSee == null || wantToSee.getId() == null) return false;

        Optional<WantToSee> existingWantToSee = wantToSeeRepo.findById(wantToSee.getId());
        if (existingWantToSee.isEmpty()) return false;

        wantToSeeRepo.delete(existingWantToSee.get());
        logUpdateService.createGeneralLog(wantToSee.getUser().getUsername() + "'s want-to-see of " + wantToSee.getFilm().getTitle() + " has been deleted");
        return true;
    }

    public void deleteByUser(User user) {
        List<WantToSee> allFromUser = wantToSeeRepo.findAllByUser(user);

        for (WantToSee wantToSee: allFromUser) {
            deleteWantToSee(wantToSee);
        }
    }

    public boolean deleteByUserAndFilm(User user, Film film) {
        Optional<WantToSee> wantToSee = findByUserAndFilm(user, film);
        if (wantToSee.isEmpty()) return false;

        wantToSeeRepo.delete(wantToSee.get());
        return true;
    }
}

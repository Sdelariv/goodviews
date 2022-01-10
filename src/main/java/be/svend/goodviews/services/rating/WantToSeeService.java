package be.svend.goodviews.services.rating;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.User;
import be.svend.goodviews.models.WantToSee;
import be.svend.goodviews.repositories.WantToSeeRepository;
import be.svend.goodviews.services.film.FilmValidator;
import be.svend.goodviews.services.users.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WantToSeeService {
    WantToSeeRepository wantToSeeRepo;

    FilmValidator filmValidator;
    UserValidator userValidator;

    // CONSTRUCTOR

    public WantToSeeService(WantToSeeRepository wantToSeeRepo, FilmValidator filmValidator, UserValidator userValidator) {
        this.wantToSeeRepo = wantToSeeRepo;
        this.filmValidator = filmValidator;
        this.userValidator = userValidator;
    }


    // FIND METHODS

    public List<WantToSee> findByUser(User user) {
        return wantToSeeRepo.findAllContainingUser(user);
    }

    public List<WantToSee> findByUsers(List<User> users) {
        List<WantToSee> allWantToSee = new ArrayList<>();

        for (User user: users){
            allWantToSee.addAll(findByUser(user));
        }

        return allWantToSee;
    }

    // CREATE METHODS

    public Optional<WantToSee> createWantToSee(User user, Film film) {
        WantToSee wantToSee = new WantToSee();

        if (filmValidator.isExistingFilm(film).isEmpty()) return Optional.empty();
        wantToSee.setFilm(film);

        if (userValidator.isExistingUser(user).isEmpty()) return Optional.empty();
        wantToSee.setUser(user);

        return Optional.of(wantToSeeRepo.save(wantToSee));
    }

    // DELETE METHODS

    public boolean deleteWantToSee(WantToSee wantToSee) {
        if (wantToSee == null || wantToSee.getId() == null) return false;

        Optional<WantToSee> existingWantToSee = wantToSeeRepo.findById(wantToSee.getId());
        if (existingWantToSee.isEmpty()) return false;

        wantToSeeRepo.delete(existingWantToSee.get());
        return true;
    }
}

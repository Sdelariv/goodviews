package be.svend.goodviews.repositories;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.User;
import be.svend.goodviews.models.WantToSee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WantToSeeRepository extends JpaRepository<WantToSee, Long> {

    List<WantToSee> findAllByUser(User user);

    Optional<WantToSee> findByUserAndFilm(User user, Film film);
}

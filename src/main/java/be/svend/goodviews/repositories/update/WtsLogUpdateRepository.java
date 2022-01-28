package be.svend.goodviews.repositories.update;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.User;
import be.svend.goodviews.models.update.WtsLogUpdate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WtsLogUpdateRepository extends JpaRepository<WtsLogUpdate, Long> {

    List<WtsLogUpdate> findByUserAndFilm(User user, Film film);
}

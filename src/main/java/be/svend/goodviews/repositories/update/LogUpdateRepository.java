package be.svend.goodviews.repositories.update;

import be.svend.goodviews.models.User;
import be.svend.goodviews.models.update.LogUpdate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogUpdateRepository extends JpaRepository<LogUpdate, Long> {

    List<LogUpdate> findByUser(User user);

    List<LogUpdate> findByOtherUser(User user);

    List<LogUpdate> findByUserAndIsClassifiedFalse(User user);

    List<LogUpdate> findByOtherUserAndIsClassifiedFalse(User user);
}

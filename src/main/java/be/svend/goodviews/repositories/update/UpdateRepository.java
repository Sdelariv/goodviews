package be.svend.goodviews.repositories.update;

import be.svend.goodviews.models.update.LogUpdate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UpdateRepository extends JpaRepository<LogUpdate, Long> {

}

package be.svend.goodviews.repositories;

import be.svend.goodviews.models.User;
import be.svend.goodviews.models.WantToSee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WantToSeeRepository extends JpaRepository<WantToSee, Long> {

    List<WantToSee> findAllContainingUser(User user);
}

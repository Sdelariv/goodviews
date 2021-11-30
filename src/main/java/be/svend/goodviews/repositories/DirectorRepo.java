package be.svend.goodviews.repositories;

import be.svend.goodviews.models.Director;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DirectorRepo extends JpaRepository<Director, Long> {

    Optional<Director> findByName(String name);
}

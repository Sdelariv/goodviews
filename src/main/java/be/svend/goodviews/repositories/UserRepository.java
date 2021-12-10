package be.svend.goodviews.repositories;

import be.svend.goodviews.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<Object> findByUsername(String username);
}

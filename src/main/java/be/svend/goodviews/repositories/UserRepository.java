package be.svend.goodviews.repositories;

import be.svend.goodviews.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

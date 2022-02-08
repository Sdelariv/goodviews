package be.svend.goodviews.repositories;

import be.svend.goodviews.models.TypeOfUser;
import be.svend.goodviews.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameIgnoreCase(String username);

    List<User> findByTypeOfUser(TypeOfUser typeOfUser);
}

package be.svend.goodviews.repositories;

import be.svend.goodviews.models.Login;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoginRepository extends JpaRepository<Login, Long> {

    List<Login> findAll();

    Optional<Login> findByIp(String ip);

    Optional<Login> findByUser_Username(String username);

}

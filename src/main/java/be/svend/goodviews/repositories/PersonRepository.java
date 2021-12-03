package be.svend.goodviews.repositories;


import be.svend.goodviews.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByName(String name);
}


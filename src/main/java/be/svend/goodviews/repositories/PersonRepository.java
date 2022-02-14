package be.svend.goodviews.repositories;


import be.svend.goodviews.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, String> {

    List<Person> findByName(String name);

    List<Person> findByNameContaining(String name);

}


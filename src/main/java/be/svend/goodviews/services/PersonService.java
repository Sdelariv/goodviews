package be.svend.goodviews.services;

import be.svend.goodviews.models.Person;
import be.svend.goodviews.repositories.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
    PersonRepository personRepo;

    public PersonService(PersonRepository personRepo) {
        this.personRepo = personRepo;
    }

    public List<Person> saveDirectors(List<Person> directors) {
        List<Person> savedDirectors = new ArrayList<>();

        for (Person director: directors) {
            savedDirectors.add(saveDirector(director));
        }

        return savedDirectors;
    }

    public Person saveDirector(Person director) {
        Optional<Person> foundDirector = personRepo.findByName(director.getName());

        // TODO: deal with duplicate names

        if (foundDirector.isEmpty()) {
            System.out.println("Saving director: " + director.getName());
            return personRepo.save(director);
        } else {
            System.out.println("Not saving " + director.getName() + " because it already exists in the database");
            return foundDirector.get();
        }
    }
}

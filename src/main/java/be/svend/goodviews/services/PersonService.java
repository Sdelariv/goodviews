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

    public List<Person> savePersons(List<Person> persons) {
        List<Person> foundPersons = new ArrayList<>();

        if (persons == null) return foundPersons;

        for (Person person: persons) {
            foundPersons.add(savePerson(person));
        }

        return foundPersons;
    }
    public Person savePerson(Person person) {
        Optional<Person> foundPerson = personRepo.findById(person.getId());

        if (foundPerson.isEmpty()) {
            System.out.println("Saving person: " + person.getName());
            return personRepo.save(person);
        } else {
            System.out.println("Not saving " + person.getName() + " because it already exists in the database");
            return foundPerson.get();
        }
    }


}

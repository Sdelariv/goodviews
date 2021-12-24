package be.svend.goodviews.services.crew;

import be.svend.goodviews.models.Person;
import be.svend.goodviews.repositories.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
    PersonRepository personRepo;
    PersonValidator personValidator;

    // CONSTRUCTORS

    public PersonService(PersonRepository personRepo, PersonValidator personValidator) {
        this.personRepo = personRepo;
        this.personValidator = personValidator;
    }

    // FIND METHODS

    public Optional<Person> findPersonById(String id) {
       if (!personValidator.isValidIdFormat(id)) return Optional.empty(); // TODO: Move to controller?

       return personRepo.findById(id);
    }

    /**
     * Looks for everyone whose name contains the name given
     * @param name
     * @return List<Person> list of people where their name contains the name given
     */
    public List<Person> findPersonsByName(String name) {

        return personRepo.findByNameContaining(name);
    }

    // CREATE METHODS

    public List<Person> createPersons(List<Person> persons) {
        List<Person> foundPersons = new ArrayList<>();

        if (persons == null) return foundPersons;

        for (Person person: persons) {
            foundPersons.add(createPerson(person));
        }

        return foundPersons;
    }
    public Person createPerson(Person person) {
        // Check whether id is valid
        if (!personValidator.hasValidIdFormat(person)) return null;

        // Check whether person is already in db
        Optional<Person> existingPerson = personRepo.findById(person.getId());
        if (existingPerson.isPresent()) {
            System.out.println("Not saving " + person.getName() + " because it already exists in the database");
            return existingPerson.get();
        }

        // Saving person
        System.out.println("Saving person: " + person.getName());
        return personRepo.save(person);

    }

    // UPDATE METHODS

    public Optional<Person> updatePerson(Person person) {
        if (!personValidator.hasValidIdFormat(person)) {
            System.out.println("Trying to update a person without a valid id");
            return Optional.empty();
        }

        Optional<Person> existingPerson = personRepo.findById(person.getId());
        if (existingPerson.isEmpty()) {
            System.out.println("Can't update a person that is not yet in the db");
            return Optional.empty();
        }

        System.out.println("Updating " +person.getId());
        return Optional.of(personRepo.save(person));
    }

    // DELETE METHODS

    public boolean deletePerson(Person person) {
        if (!personValidator.hasValidIdFormat(person)) return false;

        Optional<Person> existingPerson = personRepo.findById(person.getId());
        if (existingPerson.isEmpty()) {
            System.out.println("Can't delete a person that is not in the db");
            return false;
        }

        System.out.println("Updating " +person.getId());
        personRepo.deleteById(person.getId());
        return true;
    }

}

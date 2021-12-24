package be.svend.goodviews.services.crew;

import be.svend.goodviews.models.Person;
import be.svend.goodviews.repositories.PersonRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PersonValidator {

    PersonRepository personRepo;

    public PersonValidator(PersonRepository personRepo) {
        this.personRepo = personRepo;
    }


    /**
     * Checks whether id is null and whether it is valid (starts with "nm")
     * @param person - the Person to check the id of
     * @return true if valid, false if not
     */
    public boolean hasValidIdFormat(Person person) {
        if (person.getId() == null) return false;

        if (!isValidIdFormat(person.getId())) return false;

        return true;
    }

    /**
     * Checks whether the id has a valid format (starts with "nm")
     * @param id
     * @return
     */
    public boolean isValidIdFormat(String id) {
        if (id == null) return false;

        return id.startsWith("nm");
    }

    public Optional<Person> isExistingPerson(Person person) {
        if (person == null) return Optional.empty();

        if (person.getId() == null) return Optional.empty();

        return personRepo.findById(person.getId());
    }
}

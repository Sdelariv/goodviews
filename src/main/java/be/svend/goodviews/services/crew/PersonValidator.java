package be.svend.goodviews.services.crew;

import be.svend.goodviews.models.Person;
import org.springframework.stereotype.Component;

@Component
public class PersonValidator {


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
        return id.startsWith("nm");
    }
}

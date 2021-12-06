package be.svend.goodviews.services;

import be.svend.goodviews.models.Person;
import org.springframework.stereotype.Component;

@Component
public class PersonValidator {


    public boolean hasValidIdFormat(Person person) {
        if (person.getId() == null) return false;

        if (!isValidIdFormat(person.getId())) return false;

        return true;
    }

    public boolean isValidIdFormat(String id) {
        return id.startsWith("nm");
    }
}

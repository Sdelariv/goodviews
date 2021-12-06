package be.svend.goodviews.services;

import be.svend.goodviews.models.Person;
import org.springframework.stereotype.Component;

@Component
public class PersonValidator {


    public boolean hasValidIdFormat(Person person) {
        if (person.getId() == null) return false;

        if (!person.getId().startsWith("nm")) return false;

        return true;
    }
}

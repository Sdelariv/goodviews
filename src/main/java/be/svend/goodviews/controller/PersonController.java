package be.svend.goodviews.controller;

import be.svend.goodviews.models.Person;
import be.svend.goodviews.services.crew.PersonService;
import be.svend.goodviews.services.crew.PersonValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/person")
public class PersonController {
    PersonService personService;
    PersonValidator personValidator;

    public PersonController(PersonService personService, PersonValidator personValidator) {
        this.personService = personService;
        this.personValidator = personValidator;
    }

    // FIND METHODS

    @GetMapping("/{id}")
    public ResponseEntity findPersonById(@PathVariable String id) {
        System.out.println("FIND PERSON BY ID called with " + id);

        Optional<Person> foundPerson = personService.findPersonById(id);
        if (foundPerson.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(foundPerson);
    }
}

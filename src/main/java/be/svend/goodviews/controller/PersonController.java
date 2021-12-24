package be.svend.goodviews.controller;

import be.svend.goodviews.models.Person;
import be.svend.goodviews.services.crew.PersonService;
import be.svend.goodviews.services.crew.PersonValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static be.svend.goodviews.services.StringValidator.isValidString;

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

        if (!isValidString(id)) return ResponseEntity.badRequest().body("Invalid input");

        Optional<Person> foundPerson = personService.findPersonById(id);
        if (foundPerson.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(foundPerson);
    }

    @GetMapping
    public ResponseEntity findByName(@RequestParam String name) {
        System.out.println("FIND PERSON BY NAME CALLED for" + name);

        if (!isValidString(name)) return ResponseEntity.badRequest().body("Invalid input");

        List<Person> foundPerson = personService.findPersonsByName(name);
        if (foundPerson.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(foundPerson);
    }
}

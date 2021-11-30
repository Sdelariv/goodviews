package be.svend.goodviews.models;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;

@Entity
public class Director {

    @Id
    @GeneratedValue
    private Long id;

    private String name;


    // CONSTRUCTORS

    public Director() {

    }

    public Director(String name) {
        this.name = name;
    }

    // GETTERS & SETTERS


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

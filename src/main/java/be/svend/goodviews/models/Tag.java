package be.svend.goodviews.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tag {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    // CONSTRUCTORS

    public Tag() {
    }

    public Tag(String name) {
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
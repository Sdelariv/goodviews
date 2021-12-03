package be.svend.goodviews.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints= @UniqueConstraint(columnNames= "name"))
public class Person {

    @Id
    private String id;

    private String name;


    // CONSTRUCTORS

    public Person() {

    }

    public Person(String name) {
        this.name = name;
    }

    public Person(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // GETTERS & SETTERS


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // OTHER

    @Override
    public String toString() {
        return "Person {" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

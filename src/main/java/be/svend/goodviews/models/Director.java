package be.svend.goodviews.models;

import javax.persistence.*;


@Entity
@Table(uniqueConstraints= @UniqueConstraint(columnNames= "name"))
public class Director {

    @Id
    private String id;

    private String name;


    // CONSTRUCTORS

    public Director() {

    }

    public Director(String name) {
        this.name = name;
    }

    public Director(String id, String name) {
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
        return "Director{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

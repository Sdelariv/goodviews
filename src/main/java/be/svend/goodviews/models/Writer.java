package be.svend.goodviews.models;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints= @UniqueConstraint(columnNames= "name"))
public class Writer {

    @Id
    private String id;

    private String name;

    // CONSTRUCTORS

    public Writer() {

    }

    public Writer(String name) {
        this.id = id;
        this.name = name;
    }

    public Writer(String id, String name) {
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
        return "Writer{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

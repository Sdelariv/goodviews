package be.svend.goodviews.models;

import javax.persistence.*;


@Entity
@Table(uniqueConstraints= @UniqueConstraint(columnNames= "name"))
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

    // OTHER

    @Override
    public String toString() {
        return "Director{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

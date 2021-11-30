package be.svend.goodviews.models;

import javax.persistence.*;

/**
 * Keyword to describe the film (may overlap with genre in a few cases)
 */
@Entity
@Table(uniqueConstraints= @UniqueConstraint(columnNames= "name"))
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

    // OTHER

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

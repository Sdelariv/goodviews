package be.svend.goodviews.models;


import javax.persistence.*;
import java.util.Objects;

/**
 * Genre of the film
 */
@Entity
@Table(uniqueConstraints= @UniqueConstraint(columnNames= "name"))
public class Genre {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    // CONSTRUCTORS

    public Genre() {
    }

    public Genre(String name) {
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
        return "Genre{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genre genre = (Genre) o;
        if (genre.getName().equals(this.getName())) return true;

        return false;
    }


}

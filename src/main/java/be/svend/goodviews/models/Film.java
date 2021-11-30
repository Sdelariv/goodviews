package be.svend.goodviews.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Date;
import java.util.Set;

@Entity
public class Film {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String translatedTitle;

    private Date releaseDate;

    private String posterUrl;

    @ManyToMany
    private Set<Genre> genres;

    @ManyToMany
    private Set<Tag> tags;

    private Double averageRating;

}

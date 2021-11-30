package be.svend.goodviews.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    private List<Genre> genres;

    @ManyToMany
    private List<Tag> tags;

    private Double averageRating;

    // GETTERS & SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTranslatedTitle() {
        return translatedTitle;
    }

    public void setTranslatedTitle(String translatedTitle) {
        this.translatedTitle = translatedTitle;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public List<Genre> getGenres() {
        return genres.stream().distinct().collect(Collectors.toList());
    }

    public void addGenre(Genre genre) {
        if (genres == null) {
            genres = new ArrayList<>();
        }

        if (!genres.contains(genre)) {
            genres.add(genre);
        }
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void addTag(Tag tag) {
        if (tags == null) {
            tags = new ArrayList<>();
        }

        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
}

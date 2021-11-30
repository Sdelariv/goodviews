package be.svend.goodviews.models;

import javax.persistence.*;
import java.time.LocalDate;
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

    private LocalDate releaseDate;

    private String posterUrl;

    @ManyToMany
    private List<Genre> genres;

    @ManyToMany
    private List<Tag> tags;

    private Integer averageRating; // 0-100

    @ManyToMany
    private List<Director> director;

    private Integer runTime;

    // TODO: Add list of Ratings, list of pictures, Director as an entity

    // CONSTURCTORS

    public Film() {
    }

    public Film(String title) {
        this.title = title;
    }

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

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
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

    public void addGenre(String name) {
        Genre genre = new Genre(name);

        addGenre(genre);
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
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

    public void addTag(String name) {
        Tag tag = new Tag(name);

        addTag(tag);
    }

    public Integer getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Integer averageRating) {
        this.averageRating = averageRating;
    }

    public List<Director> getDirector() {
        return director;
    }

    public void setDirector(Director director) {
        this.director = new ArrayList<>(List.of(director));
    }

    public void setDirector(String name) {
        setDirector(new Director(name));
    }

    public void setDirector(List<Director> directors) {
        this.director = directors;
    }

    public Integer getRunTime() {
        return runTime;
    }

    public void setRunTime(Integer runTime) {
        this.runTime = runTime;
    }
}

package be.svend.goodviews.models;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Film, which consists of a title, a translated title (if the original one is not in English),
 * releasedate, poster, a list of genres, a list of tags, an average rating, director and runtime
 */
@Entity
public class Film {

    @Id
    private String id;

    private String title;

    private String translatedTitle;

    private Integer releaseYear; // TODO: Decide between date and year

    private LocalDate releaseDate;

    private String posterUrl;

    @ManyToMany (fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Genre> genres;

    @ManyToMany (fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Tag> tags;

    private Integer averageRating; // 0-100

    private Integer averageRatingImdb;

    @ManyToMany (fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Person> director;

    @ManyToMany (fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Person> writer;

    private Integer runTime; // in minutes

    // TODO: Add list of Ratings, list of pictures, Director as an entity

    // CONSTURCTORS

    public Film() {
    }

    public Film(String title) {
        this.title = title;
    }

    // GETTERS & SETTERS

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
        if (this.genres == null) return null;
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

    public List<Person> getDirector() {
        return director;
    }

    public void setDirector(Person director) {
        this.director = new ArrayList<>(List.of(director));
    }

    public void setDirector(String name) {
        setDirector(new Person(name));
    }

    public void setDirector(List<Person> directors) {
        this.director = directors;
    }

    public List<Person> getWriter() {
        return writer;
    }

    public void setWriter(List<Person> writer) {
        this.writer = writer;
    }

    public Integer getRunTime() {
        return runTime;
    }

    public void setRunTime(Integer runTime) {
        this.runTime = runTime;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        if (releaseYear > 0 && releaseYear < 3000) this.releaseYear = releaseYear;
    }

    public Integer getAverageRatingImdb() {
        return averageRatingImdb;
    }

    public void setAverageRatingImdb(Integer averageRatingImdb) {
        this.averageRatingImdb = averageRatingImdb;
    }

    // OTHER

    @Override
    public String toString() {
        return "Film{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", translatedTitle='" + translatedTitle + '\'' +
                ", releaseYear=" + releaseYear +
                ", posterUrl='" + posterUrl + '\'' +
                ", genres=" + genres +
                ", tags=" + tags +
                ", averageRating=" + averageRating +
                ", averageRating (Imdb)=" + averageRatingImdb +
                ", director=" + director +
                ", writer=" + writer +
                ", runTime=" + runTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return Objects.equals(id, film.id);
    }

}

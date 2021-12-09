package be.svend.goodviews.services;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.Genre;
import be.svend.goodviews.models.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FilmMerger {

    /**
     * Merges the original film with data from the newFilm (fetched from IMDB web).
     * ! Doesn't merge tags or average rating as those are not from IMDB
     * @param oldFilm film to be merged (from our own DB)
     * @param newFilm film to merge from (from the IMDB web)
     * @return returns empty Optional if either film has no id, or if their id is not the same
     */
    public static Optional<Film> mergeFilms(Film oldFilm, Film newFilm) {
        // Check whether it is indeed the same film
        if (oldFilm.getId() == null || newFilm.getId() == null) return Optional.empty();

        if (!oldFilm.getId().equals(newFilm.getId())) return Optional.empty();

        // Fill everything in, if it is present
        Film mergedFilm = new Film();

        mergedFilm.setId(oldFilm.getId());

        mergedFilm = mergeTitle(mergedFilm,newFilm);

        mergedFilm = mergeTranslatedTitle(mergedFilm,newFilm);

        mergedFilm = mergePosterUrl(mergedFilm,newFilm);

        mergedFilm = mergeWriters(mergedFilm,newFilm);

        mergedFilm = mergeDirectors(mergedFilm,newFilm);

        mergedFilm = mergeGenres(mergedFilm,newFilm);

        mergedFilm = mergeRuntime(mergedFilm,newFilm);

        mergedFilm = mergeAverageRatingImdb(mergedFilm,newFilm);

        mergedFilm = mergeReleaseYear(mergedFilm,newFilm);


        return Optional.of(mergedFilm);

    }

    private static Film mergeReleaseYear(Film mergedFilm, Film newFilm) {
        if (newFilm.getReleaseYear() == null) return mergedFilm;
        if (newFilm.getReleaseYear() == 0) return mergedFilm;

        mergedFilm.setReleaseYear(newFilm.getReleaseYear());

        return mergedFilm;
    }


    private static Film mergeAverageRatingImdb(Film mergedFilm, Film newFilm) {
        System.out.println("Old average rating = " + mergedFilm.getAverageRatingImdb());
        if (newFilm.getAverageRatingImdb() == null) return mergedFilm;

        System.out.println("New average rating = " + newFilm.getAverageRatingImdb());
        mergedFilm.setAverageRatingImdb(newFilm.getAverageRatingImdb());

        return mergedFilm;
    }

    private static Film mergeRuntime(Film mergedFilm, Film newFilm) {
        if (newFilm.getRunTime() != null) {
            mergedFilm.setRunTime(newFilm.getRunTime());
        }

        return mergedFilm;
    }

    private static Film mergeGenres(Film mergedFilm, Film newFilm) {
        if (newFilm.getGenres() == null) return mergedFilm;

        List<Genre> genres = new ArrayList<>();
        if (mergedFilm.getGenres() != null) genres.addAll(mergedFilm.getGenres());

        genres.addAll(newFilm.getGenres());
        genres = genres.stream().distinct().collect(Collectors.toList());
        mergedFilm.setGenres(genres);

        return mergedFilm;
    }

    private static Film mergeDirectors(Film mergedFilm, Film newFilm) {
        if (newFilm.getDirector() == null) return mergedFilm;

        List<Person> directors = new ArrayList<>();
        if (mergedFilm.getDirector() != null) directors.addAll(mergedFilm.getDirector());

        directors.addAll(newFilm.getDirector());
        directors = directors.stream().distinct().collect(Collectors.toList());
        mergedFilm.setDirector(directors);

        return mergedFilm;
    }

    private static Film mergePosterUrl(Film mergedFilm, Film newFilm) {
        if (newFilm.getPosterUrl() != null) mergedFilm.setPosterUrl(newFilm.getPosterUrl());

        return mergedFilm;
    }

    private static Film mergeTitle(Film mergedFilm, Film newFilm) {
        if (newFilm.getTitle() != null) mergedFilm.setTitle(newFilm.getTitle());

        return mergedFilm;
    }

    private static Film mergeTranslatedTitle(Film mergedFilm, Film newFilm) {
        if (newFilm.getTranslatedTitle() != null) mergedFilm.setTranslatedTitle(newFilm.getTranslatedTitle());

        return mergedFilm;
    }

    private static Film mergeWriters(Film mergedFilm, Film newFilm) {
        if (newFilm.getWriter() == null) return mergedFilm;

        List<Person> writers = new ArrayList<>();
        if (mergedFilm.getWriter() != null) writers.addAll(mergedFilm.getWriter());
        writers.addAll(newFilm.getWriter());
        writers = writers.stream().distinct().collect(Collectors.toList());
        mergedFilm.setWriter(writers);

        return mergedFilm;
    }
}

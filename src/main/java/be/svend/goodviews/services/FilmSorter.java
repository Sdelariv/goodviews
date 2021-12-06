package be.svend.goodviews.services;

import be.svend.goodviews.models.Film;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FilmSorter {

    public static List<Film> orderByRatingAscending(List<Film> films) {
        return films.stream().sorted(Comparator.comparingInt(Film::getAverageRatingImdb)).collect(Collectors.toList());
    }

    public static List<Film> orderByRatingDescending(List<Film> films) {
        return films.stream().sorted(Comparator.comparingInt(Film::getAverageRatingImdb).reversed()).collect(Collectors.toList());
    }
}

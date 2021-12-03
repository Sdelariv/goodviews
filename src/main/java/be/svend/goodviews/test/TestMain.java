package be.svend.goodviews.test;

import be.svend.goodviews.models.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestMain {

    public static void main(String[] args) {
        Film film1 = new Film("Film1");
        Film film2 = new Film("Film2");

        List<Film> filmList = new ArrayList<>();
        filmList.add(film1);
        filmList.add(film2);

        filmList.stream().map(f -> f = new Film("test")).collect(Collectors.toList());

        System.out.println(filmList);
    }
}

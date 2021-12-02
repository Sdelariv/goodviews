package be.svend.goodviews.test;


import be.svend.goodviews.factory.ImdbScraper;
import be.svend.goodviews.models.Film;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        ImdbScraper imdbScraper = new ImdbScraper();

        List<Film> filmList = imdbScraper.scrapeImdb();

        for (Film film : filmList) {
           System.out.println("Found " + film);
        }

        System.out.println(filmList.size());

    }
}

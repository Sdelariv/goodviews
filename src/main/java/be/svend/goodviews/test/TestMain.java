package be.svend.goodviews.test;

import be.svend.goodviews.factory.FilmFactory;
import be.svend.goodviews.factory.scraper.webscraper.FilmDeserialiser;
import be.svend.goodviews.factory.scraper.webscraper.WebScraper;
import be.svend.goodviews.models.Film;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TestMain {

    public static void main(String[] args) throws IOException {

        String id = "tt0010323";
        String imdbUrl = "https://www.imdb.com/title/" + id + "/releaseinfo";


        // Getting the full HTML page of the film
        Document doc = Jsoup.connect(imdbUrl).get();
        String fullHtml = doc.body().toString();

        // Extracting the world wide title








    }
}

package be.svend.goodviews.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import java.io.IOException;

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

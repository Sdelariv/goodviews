package be.svend.goodviews.factory.scraper.webscraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Optional;

public class PosterScraper {

    public static Optional<String> scrapePoster(String id) {
        Document doc = null;
        String imdbUrl = "https://www.imdb.com/title/" + id + "/";
        Optional<String> posterUrl = Optional.empty();

        try {
            doc = Jsoup.connect(imdbUrl).get();

            String fullHtml = doc.body().toString();

            String[] splittedHtml = fullHtml.split("img alt"); // Locate the img alt div

            String img_alt = splittedHtml[1].split("</div>")[0].split("src")[1]; // Get the first url out
            posterUrl = Optional.of(img_alt.substring(2,img_alt.length()-2)); // Remove " and "

            System.out.println(doc.title());
            System.out.println(posterUrl.get());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return posterUrl;
    }
}

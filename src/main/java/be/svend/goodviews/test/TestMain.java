package be.svend.goodviews.test;

import be.svend.goodviews.factory.FilmFactory;
import be.svend.goodviews.factory.scraper.webscraper.FilmDeserialiser;
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
import java.util.stream.Collectors;

public class TestMain {

    public static void main(String[] args) {



        String url = "/title/tt0107290/";
        System.out.println(url.split("/")[2]);
        String json = "{\"@context\":\"https://schema.org\",\"@type\":\"Movie\",\"url\":\"/title/tt0107290/\",\"name\":\"Jurassic Park\",\"image\":\"https://m.media-amazon.com/images/M/MV5BMjM2MDgxMDg0Nl5BMl5BanBnXkFtZTgwNTM2OTM5NDE@._V1_.jpg\",\"description\":\"A pragmatic paleontologist touring an almost complete theme park on an island in Central America is tasked with protecting a couple of kids after a power failure causes the park&apos;s cloned dinosaurs to run loose.\",\"review\":{\"@type\":\"Review\",\"itemReviewed\":{\"@type\":\"CreativeWork\",\"url\":\"/title/tt0107290/\"},\"author\":{\"@type\":\"Person\",\"name\":\"Rob Paul\"},\"dateCreated\":\"1999-07-09\",\"inLanguage\":\"English\",\"name\":\"Still a land mark in film making\",\"reviewBody\":\"It&apos;s hard to believe that it&apos;s 6 years since this film appeared at the cinemas. At the time it was a truly ground-breaking film, managing for the first time to portray dinosaurs in an extremely realistic manner, unlike those plasticine monsters that we were forced to watch in previous monster movies. Jurassic Park will also be remembered as the movie that finally made George Lucas sit up and realise that it was possible for him to make his prequels.\\n\\nIn hindsight, while it is still an extremely well directed and tension filled movie, the script did seemed dumbed down in order to appeal to as many people as possible - but that&apos;s really irrelevant since this film is all about dinosaurs. One of Spielberg&apos;s brilliantly directed films, it even managed to make me forget Spielberg&apos;s previous disaster of a movie - do you recall HOOK?\\n\\n8/10.\"},\"aggregateRating\":{\"@type\":\"AggregateRating\",\"ratingCount\":919224,\"bestRating\":10,\"worstRating\":1,\"ratingValue\":8.1},\"contentRating\":\"PG\",\"genre\":[\"Action\",\"Adventure\",\"Sci-Fi\"],\"datePublished\":\"1993-07-16\",\"keywords\":\"dinosaur,sneeze,bipedal dinosaur,national film registry,tyrannosaurus rex\",\"trailer\":{\"@type\":\"VideoObject\",\"name\":\"3D Re-Release Version\",\"embedUrl\":\"/video/imdb/vi177055257\",\"thumbnail\":{\"@type\":\"ImageObject\",\"contentUrl\":\"https://m.media-amazon.com/images/M/MV5BMjE2NzI2MTg2Ml5BMl5BanBnXkFtZTgwMzc2MzIyMzE@._V1_.jpg\"},\"thumbnailUrl\":\"https://m.media-amazon.com/images/M/MV5BMjE2NzI2MTg2Ml5BMl5BanBnXkFtZTgwMzc2MzIyMzE@._V1_.jpg\",\"description\":\" During a preview tour, a theme park suffers a major power breakdown that allows its cloned dinosaur exhibits to run amok. \"},\"actor\":[{\"@type\":\"Person\",\"url\":\"/name/nm0000554/\",\"name\":\"Sam Neill\"},{\"@type\":\"Person\",\"url\":\"/name/nm0000368/\",\"name\":\"Laura Dern\"},{\"@type\":\"Person\",\"url\":\"/name/nm0000156/\",\"name\":\"Jeff Goldblum\"}],\"director\":[{\"@type\":\"Person\",\"url\":\"/name/nm0000229/\",\"name\":\"Steven Spielberg\"}],\"creator\":[{\"@type\":\"Organization\",\"url\":\"/company/co0005073/\"},{\"@type\":\"Organization\",\"url\":\"/company/co0009119/\"},{\"@type\":\"Person\",\"url\":\"/name/nm0000341/\",\"name\":\"Michael Crichton\"},{\"@type\":\"Person\",\"url\":\"/name/nm0462895/\",\"name\":\"David Koepp\"}],\"duration\":\"PT2H7M\"}";

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Film.class,new FilmDeserialiser());
        objectMapper.registerModule(module);


        try {
            Film film = objectMapper.readValue(json, Film.class);
            System.out.println(film);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

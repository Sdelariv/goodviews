package be.svend.goodviews.factory.scraper.webscraper;

import be.svend.goodviews.models.Film;

import java.util.List;
import java.util.Optional;

public class WebScraper {


    public static List<Film> addPosters(List<Film> films) {

        for (Film film: films) {
            Optional<String> posterUrl = PosterScraper.scrapePoster(film.getId());

            if (posterUrl.isPresent()) film.setPosterUrl(posterUrl.get());
        }

        return films;
    }


}

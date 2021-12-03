package be.svend.goodviews.factory.imdbscraper;

import be.svend.goodviews.models.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Responsible for scraping duties based on tsv files of the database
 * @Author: Sven Delarivière
 */
@Component
public class ImdbScraper {
    private final int voteMinimum = 25000;

    private RatingScraper ratingScraper;
    private FilmScraper filmScraper;
    private CrewScraper crewScraper;


    public ImdbScraper() {
        String parentFolderPath = "D:/imdb/";
        this.filmScraper = new FilmScraper(parentFolderPath);
        this.ratingScraper = new RatingScraper(parentFolderPath, voteMinimum);
        this.crewScraper = new CrewScraper(parentFolderPath);

    }

    // GENERAL SCRAPER
    /**
     * Goes through all the relevant methods to scrape IMDB and returns a list of all the films with data
     * @return List<Film> - a list of Films with all the data
     */
    public List<Film> scrapeImdb() {
        // Gather ProjectIds with sufficient ratings
        Map<String, Integer> desiredIds = ratingScraper.findIdsWithSufficientRatings();

        // Gather only films and their info based on those ids
        List<Film> filmList = filmScraper.gatherFilmInfoFromIds(desiredIds);

        // Gather crewInfo based on the films (and their ids)
        filmList = crewScraper.addCrewIdsToFilms(filmList);
        filmList = crewScraper.addCrewDataToFilmsBasedOnCrewId(filmList);

        return filmList;
    }

}



package be.svend.goodviews.factory.imdbscraper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static be.svend.goodviews.factory.imdbscraper.LineValidator.HasSufficientVotesInDataLine;

/**
 * Responsible for scraping duties based on tsv files of the ratingData
 * @Author: Sven Delarivière
 */
public class RatingScraper {
    private File ratingData;
    private int voteMinimum;

    private final int ID_INDEX = 0;
    private final int AVERAGE_RATING_INDEX = 1;
    private final int NUMBER_RATINGS_INDEX = 2;

    public RatingScraper(String parentFolderPath, int voteMinimum) {
        ratingData = new File(parentFolderPath + "title.ratings.tsv/data.tsv");
        this.voteMinimum = voteMinimum;
    }

    // RATINGS-BASED GATHERING

    /**
     * Goes through the ratings-dataset to find films with enough ratings, and then saves that (and the average rating)
     * @return Map<String-desiredIds, Integer-averageImdbRating> - a map of filmIds with their average rating on IMDB
     */
    public Map<String, Integer> findIdsWithSufficientRatings() {
        Map<String, Integer> ids = new HashMap<>();

        try (BufferedReader basicReader = new BufferedReader(new FileReader(ratingData))) {
            String line = null;
            while ((line = basicReader.readLine()) != null) {
                String[] lineItems = line.split("\t");
                if (lineItems[ID_INDEX].equals("tconst")) continue;

                if (HasSufficientVotesInDataLine(lineItems, voteMinimum)) {
                    String id = lineItems[ID_INDEX];
                    Integer averageRating = getAverageRatingImdbFromDataLine(lineItems);
                    ids.put(id, averageRating);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ids;
    }

    /**
     * Takes the average rating from a dataline and converts it to an integer between 0 and 100
     * @param lineItems String[] - the lineItem read
     * @return Integer - integer of rating between 0 and 100
     */
    private Integer getAverageRatingImdbFromDataLine(String[] lineItems) {
        double averageRating = Double.parseDouble(lineItems[AVERAGE_RATING_INDEX]);
        averageRating = averageRating * 10;

        return (int) averageRating;
    }

}

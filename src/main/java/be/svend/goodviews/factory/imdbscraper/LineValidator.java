package be.svend.goodviews.factory.imdbscraper;

import java.util.List;
import java.util.Map;

public class LineValidator {
    private static final int ID_INDEX = 0;

    // For basicData
    private static final int TYPE_INDEX = 1;

    // For ratingData
    private static final int AVERAGE_RATING_INDEX = 1;
    private static final int NUMBER_RATINGS_INDEX = 2;

    // VALIDATING METHODS

    /**
     * Validates whether the line is about a relevant id
     * @param lineItems the line read from a dataset
     * @param idsDesired List<String-id>the list of Ids that are considered relevant
     * @return boolean - true if about a relevant id, false if not.
     */
    public static boolean isLineContainingRelevantId(String[] lineItems, List<String> idsDesired) {
        String foundId = lineItems[ID_INDEX];

        if (foundId.equals("tconst")) return false;

        if (!idsDesired.contains(foundId)) return false;

        return true;
    }

    /**
     * Validates whether the line is about a relevant id
     * @param lineItems String[] the read lineItem
     * @param idsDesiredCrew Map<String-id,Integer-averageRating> used solely for the ids
     * @return boolean - true if about a relevant id, false if not.
     */
    public static boolean isLineContainingRelevantId(String[] lineItems, Map<String,Integer> idsDesiredCrew) {
        String foundId = lineItems[ID_INDEX];

        if (foundId.equals("tconst")) return false;

        if (!idsDesiredCrew.containsKey(foundId)) return false;

        return true;
    }

    /**
     * Checks whether the line in basicData is a film or not
     * @param lineItems - a lineItem from basicData
     * @return boolean - true if it's about a film, false if not
     */
    public static boolean isFilmInDataLine(String[] lineItems) {
        if (lineItems[TYPE_INDEX].equals("movie")) return true;
        else return false;
    }

    /**
     * Checks whether the dataline from ratingData has enough votes
     * @param lineItems lineItem from ratingData
     * @return boolean - true if sufficient votes, false if not
     */
    public static boolean HasSufficientVotesInDataLine(String[] lineItems, int voteMinimum){
        return Integer.parseInt(lineItems[NUMBER_RATINGS_INDEX]) > voteMinimum;
    }

}

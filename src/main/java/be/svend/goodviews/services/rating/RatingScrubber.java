package be.svend.goodviews.services.rating;

import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.User;
import be.svend.goodviews.services.users.UserScrubber;

import java.util.ArrayList;
import java.util.List;

public class RatingScrubber {

    public static Rating scrubRatingOfUserInfo(Rating rating) {
        rating.setUser(UserScrubber.scrubAllExceptUsername(rating.getUser()));

        List<User> scrubbedUserLikes = new ArrayList<>();
        rating.getUserLikes().forEach(u -> scrubbedUserLikes.add(UserScrubber.scrubAllExceptUsername(u)));
        rating.setUserLikes(scrubbedUserLikes);

        return rating;
    }
}

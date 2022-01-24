package be.svend.goodviews.models.update;

import be.svend.goodviews.models.Comment;
import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.User;

import java.time.LocalDateTime;
import java.util.List;

public class TimelineDTO {

    private Long id;

    private User user;

    private User otherUser;

    private LocalDateTime dateTime;

    private String updateString;

    private String type;

    private Film film;

    private Rating rating;

    private List<Comment> commentList;

}

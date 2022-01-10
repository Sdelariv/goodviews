package be.svend.goodviews.services.comment;

import be.svend.goodviews.GoodviewsApplication;
import be.svend.goodviews.models.Comment;
import be.svend.goodviews.services.film.FilmService;
import be.svend.goodviews.services.rating.RatingService;
import be.svend.goodviews.services.users.UserService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommentServiceTest {
    static CommentService commentService;
    static RatingService ratingService;
    static FilmService filmService;
    static UserService userService;

    static Comment comment;
    static Comment commentToCreate;
    static Comment commentToDelete;
    static Comment comment3;

    @BeforeAll
    static void generalInit() {
        ConfigurableApplicationContext ctx = SpringApplication.run(GoodviewsApplication.class);
        commentService = ctx.getBean(CommentService.class);
        ratingService = ctx.getBean(RatingService.class);
        filmService = ctx.getBean(FilmService.class);
        userService = ctx.getBean(UserService.class);

        comment = new Comment();
        comment.setComment("Why not 100????");
        comment.setUser(userService.findByUsername("bibi").get());
        // commentService.createNewComment(comment,"sdelarivtt4468740");

        commentToCreate = new Comment();
        commentToCreate.setComment("Sorry!");
        commentToCreate.setUser(userService.findByUsername("sdelariv").get());

        commentToDelete = new Comment();
        commentToDelete.setComment("Sory");
        commentToDelete.setUser(userService.findByUsername("sdelariv").get());
        // commentService.createNewComment(commentToDelete,"sdelarivtt4468740");

        comment3 = new Comment();
        comment3.setUser(userService.findByUsername("sdelariv").get());
        comment3.setComment("Comment from user that will be deleted");
        // commentService.createNewComment(comment3,"sdelarivtt4468740");
    }

    @Test
    void findById() {
        assertTrue(commentService.findById(comment.getId()).isPresent());
    }

    @Test
    void findByRatingId() {
        assertTrue(commentService.findByRatingId("sdelarivtt4468740").size() > 0);
    }

    @Test
    void findByUsername() {
        assertTrue(commentService.findByUsername("bibi").size() > 0);
    }

    @Test
    void createNewComment() {
        // assertTrue(commentService.createNewComment(commentToCreate,"sdelarivtt4468740").isPresent());
    }

    @Test
    void updateComment() {
        comment.setComment("Why not 100????!");
        commentService.updateComment(comment);
        assertTrue(commentService.findById(comment.getId()).isPresent());
    }

    @Test
    void deleteUserFromCommentsByUsername() {
        List<Comment> comments = commentService.deleteUserFromCommentsByUsername("sdelariv");
        assertTrue(commentService.findByUsername("sdelariv").size() == 0);
    }

    @Test
    void deleteComment() {
        Long commentId = commentToDelete.getId();
        commentService.deleteComment(commentToDelete);
        assertTrue(commentService.findById(commentId).isEmpty());
    }

    @AfterAll
    static void after() {
        commentService.deleteComment(commentToCreate);
    }
}
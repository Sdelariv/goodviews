package be.svend.goodviews.services;

import be.svend.goodviews.models.Comment;
import be.svend.goodviews.models.Rating;
import be.svend.goodviews.repositories.CommentRepository;
import be.svend.goodviews.repositories.GenreRepository;
import be.svend.goodviews.repositories.RatingRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    CommentRepository commentRepo;
    RatingRepository ratingRepo;

    public CommentService(CommentRepository commentRepo,
                          RatingRepository ratingRepo) {
        this.commentRepo = commentRepo;
        this.ratingRepo = ratingRepo;
    }

    // FIND METHODS

    public Optional<Comment> findById(Long id) {
        return commentRepo.findById(id);
    }

    public List<Comment> findByRatingId(String ratingId) {
        Optional<Rating> ratingInDb = ratingRepo.findById(ratingId);
        if (ratingInDb.isEmpty()) return Collections.emptyList();

        return ratingInDb.get().getCommentList();
    }

    public List<Comment> findByUsername(String username) {
        return commentRepo.findAllByUser_Username(username);
    }

    // CREATE METHODS

    // TODO: fill in

}

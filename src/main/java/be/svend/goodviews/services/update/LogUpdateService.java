package be.svend.goodviews.services.update;

import be.svend.goodviews.DTOs.*;
import be.svend.goodviews.models.*;
import be.svend.goodviews.models.update.*;
import be.svend.goodviews.repositories.CommentRepository;
import be.svend.goodviews.repositories.RatingRepository;
import be.svend.goodviews.repositories.WantToSeeRepository;
import be.svend.goodviews.repositories.update.CommentLogUpdateRepository;
import be.svend.goodviews.repositories.update.FriendshipLogUpdateRepository;
import be.svend.goodviews.repositories.update.LogUpdateRepository;
import be.svend.goodviews.repositories.update.RatingLogUpdateRepository;

import be.svend.goodviews.services.users.FriendFinder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LogUpdateService {
    LogUpdateRepository logUpdateRepo;
    RatingLogUpdateRepository ratingLogUpdateRepo;
    FriendshipLogUpdateRepository friendshipLogUpdateRepo;
    CommentLogUpdateRepository commentLogUpdateRepo;

    CommentRepository commentRepo;
    RatingRepository ratingRepo;
    WantToSeeRepository wtsRepo;

    FriendFinder friendFinder;

    public LogUpdateService(LogUpdateRepository logUpdateRepo, RatingLogUpdateRepository ratingLogUpdateRepo, FriendshipLogUpdateRepository friendshipLogUpdateRepo, CommentLogUpdateRepository commentLogUpdateRepo, CommentRepository commentRepo, RatingRepository ratingRepo, WantToSeeRepository wtsRepo, FriendFinder friendFinder) {
        this.logUpdateRepo = logUpdateRepo;
        this.ratingLogUpdateRepo = ratingLogUpdateRepo;
        this.friendshipLogUpdateRepo = friendshipLogUpdateRepo;
        this.commentLogUpdateRepo = commentLogUpdateRepo;
        this.commentRepo = commentRepo;
        this.ratingRepo = ratingRepo;
        this.wtsRepo = wtsRepo;
        this.friendFinder = friendFinder;
    }





    // FIND METHODS

    public List<TimelineDTO> findTimelinebyUserAndOffset(User user, Integer offset) {
        List<User> friends = friendFinder.findAllFriendsByUser(user);

        List<LogUpdate> logUpdatesInvolvingFriends = new ArrayList<>();

        for (User friend: friends) {
            logUpdatesInvolvingFriends.addAll(logUpdateRepo.findByUserAndIsClassifiedFalse(friend));
            logUpdatesInvolvingFriends.addAll(logUpdateRepo.findByOtherUserAndIsClassifiedFalse(friend));
        }

        // Sort
        logUpdatesInvolvingFriends.stream().sorted(Comparator.comparing(e -> e.getDateTime())).collect(Collectors.toList());
        Collections.reverse(logUpdatesInvolvingFriends);

        // Offset
        if (offset >= logUpdatesInvolvingFriends.size()) return Collections.emptyList();
        logUpdatesInvolvingFriends.subList(offset,logUpdatesInvolvingFriends.size());

        // Create DTO
        List<TimelineDTO> timeline = createDTOs(logUpdatesInvolvingFriends, user);

        return timeline;

    }



    public List<LogUpdate> findByUserFriendsExcludingClassified(User user) {
        List<User> friends = friendFinder.findAllFriendsByUser(user);

        List<LogUpdate> logUpdatesInvolvingFriends = new ArrayList<>();

        for (User friend: friends) {
            logUpdatesInvolvingFriends.addAll(logUpdateRepo.findByUserAndIsClassifiedFalse(friend));
            logUpdatesInvolvingFriends.addAll(logUpdateRepo.findByOtherUserAndIsClassifiedFalse(friend));
        }

        return logUpdatesInvolvingFriends.stream().distinct().sorted(Comparator.comparing(lu -> lu.getDateTime())).collect(Collectors.toList());
    }

    public List<LogUpdate> findByUserIncludingClassified(User user) {
        List<LogUpdate> logsByUser = new ArrayList<>();
        logsByUser.addAll(logUpdateRepo.findByUser(user));
        logsByUser.addAll(logUpdateRepo.findByOtherUser(user));
        return logsByUser;
    }

    public List<LogUpdate> findByUserExcludingClassified(User user) {
        List<LogUpdate> logsByUser = new ArrayList<>();
        logsByUser.addAll(logUpdateRepo.findByUserAndIsClassifiedFalse(user));
        logsByUser.addAll(logUpdateRepo.findByOtherUserAndIsClassifiedFalse(user));
        return logsByUser;
    }

    public List<LogUpdate> findByUsersExcludingClassified(List<User> users) {
        List<LogUpdate> updatesOfUsers = new ArrayList<>();
        for (User user: users) {
            List<LogUpdate> updatesOfUser = findByUserExcludingClassified(user);
            if (updatesOfUser != null) updatesOfUsers.addAll(updatesOfUser);
        }

        return updatesOfUsers.stream().sorted(Comparator.comparing(LogUpdate::getDateTime)).collect(Collectors.toList());
    }

    //  CREATE METHODS

    public void createGeneralLog(String logUpdateString) {
        LogUpdate logUpdate = new LogUpdate(logUpdateString);
        logUpdate.setClassified(true);
        save(logUpdate);
    }
    public void createGeneralLog(User user, String logUpdateString) {
        LogUpdate logUpdate = new LogUpdate(logUpdateString);
        logUpdate.setUser(user);
        logUpdate.setClassified(true);
        save(logUpdate);
    }

    public void createRatingUpdate(Rating rating) {
        RatingLogUpdate ratingLogUpdate = new RatingLogUpdate(rating);
        save(ratingLogUpdate);

        // Make older notifications classified so they don't show up in the timeline anymore
        List<RatingLogUpdate> previousRatingLogUpdates = ratingLogUpdateRepo.findByRating(ratingLogUpdate.getRating());
        for (RatingLogUpdate previousRatingLogUpdate: previousRatingLogUpdates) {
            previousRatingLogUpdate.setClassified(true);
            save(previousRatingLogUpdate);
        }
    }

    public void createCommentUpdate(Rating rating, Comment comment) {
        CommentLogUpdate commentUpdate = new CommentLogUpdate(rating,comment);
        save(commentUpdate);
    }

    public void createUpdateCommentUpdate(Comment comment) {
        CommentLogUpdate commentLogUpdate = new CommentLogUpdate();
        commentLogUpdate.setUser(comment.getUser());
        commentLogUpdate.setComment(comment);
        commentLogUpdate.setUpdateString(comment.getUser().getUsername() + " has updated their comment with id " + comment.getId());
        commentLogUpdate.setClassified(true);
        save(commentLogUpdate);
    }

    public void createFriendshipUpdate(Friendship friendship) {
        FriendshipLogUpdate friendshipLogUpdate = new FriendshipLogUpdate(friendship);
        save(friendshipLogUpdate);
    }

    public void createWantToSeeUpdate(User user, Film film) {
        WtsLogUpdate wtsLogUpdate = new WtsLogUpdate(user.getUsername() + " wants to see " + film.getTitle());
        wtsLogUpdate.setUser(user);
        wtsLogUpdate.setClassified(false);
        wtsLogUpdate.setFilm(film);
        save(wtsLogUpdate);
    }

    // DELETE METHODS

    public List<LogUpdate> deleteUserFromLogByUser(User user) {
        List<LogUpdate> logUpdatesWithUser = findByUserIncludingClassified(user);

        System.out.println("Deleting user from their logs");
        for (LogUpdate logUpdate: logUpdatesWithUser) {
            if (logUpdate.getUser().equals(user)) logUpdate.setUser(null);
            if (logUpdate.getOtherUser() != null && logUpdate.getOtherUser().equals(user)) logUpdate.setOtherUser(null);
            if (logUpdate instanceof FriendshipLogUpdate) ((FriendshipLogUpdate) logUpdate).setFriendship(null);
            logUpdateRepo.save(logUpdate);
        }

        return logUpdatesWithUser;
    }

    public void deleteRatingFromLogByRating(Rating rating) {
        List<RatingLogUpdate> ratingLogUpdatesWithRating = ratingLogUpdateRepo.findByRating(rating);
        List<CommentLogUpdate> commentLogUpdatesWithRating = commentLogUpdateRepo.findByRating(rating);

        System.out.println("Deleting ratings from the logs");
        for (RatingLogUpdate logUpdate: ratingLogUpdatesWithRating) {
            logUpdate.setRating(null);
            logUpdate.setClassified(true);
            save(logUpdate);
        }
        for (CommentLogUpdate logUpdate: commentLogUpdatesWithRating) {
            logUpdate.setRating(null);
            logUpdate.setClassified(true);
            save(logUpdate);
        }

    }

    public List<FriendshipLogUpdate> deleteFriendshipFromLogByFriendship(Friendship friendship) {
        List<FriendshipLogUpdate> logUpdatesWithFriendship = friendshipLogUpdateRepo.findByFriendship(friendship);

        System.out.println("Deleting friendships from the logs");
        for (FriendshipLogUpdate logUpdate: logUpdatesWithFriendship) {
            logUpdate.setFriendship(null);
            save(logUpdate);
        }

        return logUpdatesWithFriendship;
    }

    // INTERNAL

    private void save(LogUpdate logUpdate) {
        logUpdateRepo.save(logUpdate);
        System.out.println("Log updated");
    }

    public void deleteCommentIdFromLog(Comment comment) {
        List<CommentLogUpdate> updatesWithComment = commentLogUpdateRepo.findByComment(comment);
        for (CommentLogUpdate logUpdate: updatesWithComment) {
            logUpdate.setComment(null);
            commentLogUpdateRepo.save(logUpdate);
        }
    }

    private List<TimelineDTO> createDTOs(List<LogUpdate> logUpdatesInvolvingFriends, User user) {
        List<TimelineDTO> timeline = new ArrayList<>();

        for (LogUpdate update: logUpdatesInvolvingFriends) {
            if (update instanceof RatingLogUpdate) {
                List<Comment> commentList = commentRepo.findAllByRating(((RatingLogUpdate) update).getRating());
                boolean userHasSeen = wtsRepo.findByUserAndFilm(user, ((RatingLogUpdate) update).getRating().getFilm()).isPresent();
                int userHasRated = findUserRating(((RatingLogUpdate) update).getRating().getFilm(), user);

                RatingUpdateDTO ratingDTO = new RatingUpdateDTO((RatingLogUpdate) update, commentList, userHasSeen, userHasRated);
                timeline.add(ratingDTO);
            }
            if (update instanceof CommentLogUpdate) {
                List<Comment> commentList = commentRepo.findAllByRating(((CommentLogUpdate) update).getRating());
                boolean userHasSeen = wtsRepo.findByUserAndFilm(user, ((CommentLogUpdate) update).getRating().getFilm()).isPresent();
                int userHasRated = findUserRating(((CommentLogUpdate) update).getRating().getFilm(), user);

                CommentUpdateDTO commentDTO = new CommentUpdateDTO((CommentLogUpdate) update, commentList, userHasSeen, userHasRated);
                timeline.add(commentDTO);
            }
            if (update instanceof FriendshipLogUpdate) {
                FriendUpdateDTO friendDTO = new FriendUpdateDTO((FriendshipLogUpdate) update);
                timeline.add(friendDTO);
            }
            if (update instanceof WtsLogUpdate) {
                boolean userHasSeen = wtsRepo.findByUserAndFilm(user, (((WtsLogUpdate) update).getFilm())).isPresent();
                int userHasRated = findUserRating(((WtsLogUpdate) update).getFilm(), user);

                WtsUpdateDTO wtsDTO = new WtsUpdateDTO((WtsLogUpdate) update, userHasRated, userHasSeen);
                timeline.add(wtsDTO);
            }
        }

        // Sort
        timeline = timeline.stream().sorted(Comparator.comparing(u -> u.getDateTime())).collect(Collectors.toList());
        Collections.reverse(timeline);

        return timeline;
    }

    private int findUserRating(Film film, User user) {
        int userHasRated = -1;
        Optional<Rating> rating = ratingRepo.findById(user.getUsername() + film.getId());


        if (rating.isPresent()) {
            userHasRated = rating.get().getRatingValue();
        }

        return userHasRated;
    }
}

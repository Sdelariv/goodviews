package be.svend.goodviews.services.update;

import be.svend.goodviews.models.*;
import be.svend.goodviews.models.update.CommentLogUpdate;
import be.svend.goodviews.models.update.FriendshipLogUpdate;
import be.svend.goodviews.models.update.LogUpdate;
import be.svend.goodviews.models.update.RatingLogUpdate;
import be.svend.goodviews.repositories.update.CommentLogUpdateRepository;
import be.svend.goodviews.repositories.update.FriendshipLogUpdateRepository;
import be.svend.goodviews.repositories.update.LogUpdateRepository;
import be.svend.goodviews.repositories.update.RatingLogUpdateRepository;
import be.svend.goodviews.services.users.FriendFinder;
import be.svend.goodviews.services.users.FriendshipService;
import org.apache.juli.logging.Log;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogUpdateService {
    LogUpdateRepository logUpdateRepo;
    RatingLogUpdateRepository ratingLogUpdateRepo;
    FriendshipLogUpdateRepository friendshipLogUpdateRepo;
    CommentLogUpdateRepository commentLogUpdateRepo;
    FriendFinder friendFinder;

    public LogUpdateService(LogUpdateRepository logUpdateRepo,
                            RatingLogUpdateRepository ratingLogUpdateRepo,
                            FriendshipLogUpdateRepository friendshipLogUpdateRepo,
                            CommentLogUpdateRepository commentLogUpdateRepo,

                            FriendFinder friendFinder) {
        this.logUpdateRepo = logUpdateRepo;
        this.ratingLogUpdateRepo = ratingLogUpdateRepo;
        this.friendshipLogUpdateRepo = friendshipLogUpdateRepo;
        this.commentLogUpdateRepo = commentLogUpdateRepo;
        this.friendFinder = friendFinder;
    }

    // FIND METHODS

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
        RatingLogUpdate ratingUpdate = new RatingLogUpdate(rating);
        save(ratingUpdate);
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
        LogUpdate logUpdate = new LogUpdate(user.getUsername() + " wants to see " + film.getTitle());
        logUpdate.setUser(user);
        logUpdate.setClassified(false);
        save(logUpdate);
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
}

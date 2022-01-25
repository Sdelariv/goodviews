package be.svend.goodviews.DTOs;

import be.svend.goodviews.models.update.FriendshipLogUpdate;
import be.svend.goodviews.services.users.UserScrubber;

public class FriendUpdateDTO extends TimelineDTO {

    public FriendUpdateDTO(FriendshipLogUpdate friendshipLogUpdate) {
        super.setType(UpdateType.FRIENDS);
        super.setId(friendshipLogUpdate.getId());
        super.setUpdateString(friendshipLogUpdate.getUpdateString());
        super.setDateTime(friendshipLogUpdate.getDateTime());
        super.setUser(UserScrubber.scrubAllExceptUsername(friendshipLogUpdate.getUser()));
        super.setOtherUser(UserScrubber.scrubAllExceptUsername(friendshipLogUpdate.getOtherUser()));
    }
}

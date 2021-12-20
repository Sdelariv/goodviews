package be.svend.goodviews.services.update;

import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.update.RatingLogUpdate;
import be.svend.goodviews.repositories.update.UpdateRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateService {
    UpdateRepository updateRepo;

    public UpdateService(UpdateRepository updateRepo) {
        this.updateRepo = updateRepo;
    }

    //  CREATE METHODS

    public void createRatingUpdate(Rating rating) {
        RatingLogUpdate ratingUpdate = new RatingLogUpdate(rating);
        updateRepo.save(ratingUpdate);
    }
}

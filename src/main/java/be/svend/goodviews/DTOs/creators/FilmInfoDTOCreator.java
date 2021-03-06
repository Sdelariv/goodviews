package be.svend.goodviews.DTOs.creators;

import be.svend.goodviews.DTOs.FilmInfoDTO;
import be.svend.goodviews.models.Film;
import be.svend.goodviews.models.Rating;
import be.svend.goodviews.models.User;
import be.svend.goodviews.models.WantToSee;
import be.svend.goodviews.services.rating.RatingService;
import be.svend.goodviews.services.rating.WantToSeeService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FilmInfoDTOCreator {
    RatingService ratingService;
    WantToSeeService wtsService;

    public FilmInfoDTOCreator(RatingService ratingService, WantToSeeService wtsService) {
        this.ratingService = ratingService;
        this.wtsService = wtsService;
    }

    public FilmInfoDTO createFilmInfoDTO(Film film, User user) {
        FilmInfoDTO filmInfoDTO = new FilmInfoDTO();
        filmInfoDTO.setFilm(film);

        Optional<Rating> foundUserRating = ratingService.findById(user.getUsername() + film.getId());
        if (foundUserRating.isPresent()) {
            filmInfoDTO.setUserRating(foundUserRating.get());
        } else {
            Rating rating = new Rating();
            rating.setFilm(film);
            filmInfoDTO.setUserRating(rating);
        }

        List<Rating> foundRatings = ratingService.findByFilmId(film.getId());
        foundRatings = foundRatings.stream().sorted(Comparator.comparing(Rating::getDateOfRating)).collect(Collectors.toList());
        Collections.reverse(foundRatings);
        filmInfoDTO.setRatings(foundRatings);

        Optional<WantToSee> foundWts = wtsService.findByUserAndFilm(user, film);
        foundWts.ifPresent(wantToSee -> filmInfoDTO.setUserWtsId(wantToSee.getId()));

        return filmInfoDTO;
    }
}

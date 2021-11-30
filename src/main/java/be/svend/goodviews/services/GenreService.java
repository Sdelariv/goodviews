package be.svend.goodviews.services;

import be.svend.goodviews.models.Genre;
import be.svend.goodviews.repositories.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GenreService {
    GenreRepository genreRepo;

    public GenreService(GenreRepository genreRepo) {
        this.genreRepo = genreRepo;
    }

    public List<Genre> saveGenres(List<Genre> genres) {
        List<Genre> savedGenres = new ArrayList<>();

        for (Genre genre:genres) {
            savedGenres.add(saveGenre(genre));
        }

        return savedGenres;
    }

    public Genre saveGenre(Genre genre) {
        Optional<Genre> foundGenre = genreRepo.findByName(genre.getName());
        if (foundGenre.isEmpty()) {
            System.out.println("Saving " + genre.getName());
            return genreRepo.save(genre);
        } else {
            System.out.println("Not saving " + genre.getName() + " because it already exists");
            return foundGenre.get();
        }
    }
}

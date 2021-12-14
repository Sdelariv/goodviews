package be.svend.goodviews.services.film.properties;

import be.svend.goodviews.models.Genre;
import be.svend.goodviews.repositories.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GenreService {
    GenreRepository genreRepo;

    // CONSTRUCTORS

    public GenreService(GenreRepository genreRepo) {
        this.genreRepo = genreRepo;
    }

    // FIND METHODS

    public Optional<Genre> findByName(String name) {
        return genreRepo.findByName(name);
    }

    // SAVE METHODS

    public List<Genre> saveGenres(List<Genre> genres) {
        List<Genre> savedGenres = new ArrayList<>();

        if (genres == null) return savedGenres;

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

    // DELETE METHODS

    public boolean deleteGenre(Genre genre) {

        Optional<Genre> genreInDb = genreRepo.findByName(genre.getName());
        if (genreInDb.isEmpty()) return false;

        System.out.println("Deleting " + genre.getName());
        genreRepo.delete(genreInDb.get());

        return true;
    }
}

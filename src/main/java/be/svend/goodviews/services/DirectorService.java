package be.svend.goodviews.services;

import be.svend.goodviews.models.Director;
import be.svend.goodviews.repositories.DirectorRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DirectorService {
    DirectorRepo directorRepo;

    public DirectorService(DirectorRepo directorRepo) {
        this.directorRepo = directorRepo;
    }

    public List<Director> saveDirectors(List<Director> directors) {
        List<Director> savedDirectors = new ArrayList<>();

        for (Director director: directors) {
            savedDirectors.add(saveDirector(director));
        }

        return savedDirectors;
    }

    public Director saveDirector(Director director) {
        Optional<Director> foundDirector = directorRepo.findByName(director.getName());

        // TODO: deal with duplicate names

        if (foundDirector.isEmpty()) {
            System.out.println("Saving director: " + director.getName());
            return directorRepo.save(director);
        } else {
            System.out.println("Not saving " + director.getName() + " because it already exists in the database");
            return foundDirector.get();
        }
    }
}

package be.svend.goodviews.factory;

import be.svend.goodviews.models.Film;
import be.svend.goodviews.repositories.FilmRepository;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Responsible for scraping duties based on the moktok Server and exporting to a tsv harcopy (for ease)
 * @Author: Sven Delarivière
 */
@Component
public class HardcopyMaker {
    FilmRepository filmRepo;


    public HardcopyMaker(FilmRepository filmRepo) {
        this.filmRepo = filmRepo;
    }

    public void scrapeDatabaseToHardCopy() {
        List<Film> films = filmRepo.findAll();

        makeHardCopy(films);
    }

    public static void makeHardCopy(List<Film> films) {
        try {
            // Tab delimited file will be written to data with the name tab-file.csv
            FileWriter fos = new FileWriter("D:/moktok.hardcopy/data.tsv");
            PrintWriter dos = new PrintWriter(fos);
            dos.println("filmId\t" +
                    "filmTitle\t" +
                    "translatedTitle\t" +
                    "releaseYear\t" +
                    "posterUrl\t" +
                    "genres\t" +
                    "tags\t" +
                    "averageRating\t" +
                    "averageRating(Imdb)\t" +
                    "directors\t" +
                    "writers");

            // loop through all your data and print it to the file
            for (Film film: films) {
                dos.print(film.getId()+"\t");
                dos.print(film.getTitle()+"\t");
                dos.print(film.getTranslatedTitle()+"\t");
                dos.print(film.getReleaseYear()+"\t");
                dos.print(film.getPosterUrl()+"\t");
                dos.print(film.getGenres()+"\t");
                dos.print(film.getTags()+"\t");
                dos.print(film.getAverageRating()+"\t");
                dos.print(film.getAverageRatingImdb()+"\t");
                dos.print(film.getDirector()+"\t");
                dos.print(film.getWriter());
                dos.println();
            }
            dos.close();
            fos.close();
        } catch (IOException e) {
            System.out.println("Error Printing Tab Delimited File");
        }
    }
}

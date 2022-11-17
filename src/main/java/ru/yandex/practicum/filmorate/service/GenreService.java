package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.films.GenreStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GenreService {

    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre getGenreById(long id) {
        Optional<Genre> genre = genreStorage.loadGenreById(id);
        if (genre.isPresent()) {
            log.debug("Loading {}.", genre.get());
            return genre.get();
        } else {
            throw new NotFoundException("Genre #" + id + " not found");
        }
    }

    public List<Genre> getFilmGenresById(long id) {
        return genreStorage.loadGenresByFilmId(id);
    }

    public void addGenresToFilm(long id, List<Genre> genres) {
        genreStorage.saveGenresToFilm(id, genres);
    }

    public void updateFilmGenres(long id, List<Genre> genres) {
        genreStorage.deleteGenresFromFilm(id);
        genreStorage.saveGenresToFilm(id, genres);
    }

    public void deleteFilmGenres(long id) {
        genreStorage.deleteGenresFromFilm(id);
    }

    public List<Genre> getAllGenres() {
        List<Genre> genre = genreStorage.loadAllGenres();
        log.debug("Loading {} genres.", genre.size());
        return genre;
    }
}

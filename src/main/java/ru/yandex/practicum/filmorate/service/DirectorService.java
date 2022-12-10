package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.films.DirectorStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DirectorService {
    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public Director getDirectorById(long id) {
        return directorStorage.loadDirector(id)
                .orElseThrow(() -> new NotFoundException("**Director** #" + id + " not found."));
    }

    public List<Director> getDirectorsByFilmId(long id) {
        return directorStorage.loadDirectorsByFilmId(id);
    }

    public Director createNewDirector(Director director) {
        long id = directorStorage.saveDirector(director);
        log.debug("Creating new director {}.", director);
        return getDirectorById(id);
    }

    public Director updateDirector(Director director) {
        Director loadedDirector = directorStorage.loadDirector(director.getId())
                .orElseThrow(() -> new NotFoundException("**Director** #" + director.getId() + " not found."));
        loadedDirector.setName(director.getName());
        directorStorage.updateDirector(loadedDirector);
        log.debug("Updating director {}.", loadedDirector);
        return getDirectorById(director.getId());
    }

    public void deleteDirector(long id) {
        directorStorage.deleteDirector(id);
    }

    public void addDirectorsToFilm(long id, List<Director> directors) {
        directorStorage.saveDirectorsToFilm(id, directors);
    }

    public void updateFilmDirectors(long id, List<Director> directors) {
        directorStorage.deleteDirectorsOfFilm(id);
        directorStorage.saveDirectorsToFilm(id, directors);
    }

    public void deleteFilmDirectors(long id) {
        directorStorage.deleteDirectorsOfFilm(id);
    }

    public List<Director> getAllDirectors() {
        List<Director> directors = directorStorage.getAllDirectors();
        log.debug("Loading {} directors.", directors.size());
        return directors;
    }
}

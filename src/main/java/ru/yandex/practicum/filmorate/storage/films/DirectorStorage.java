package ru.yandex.practicum.filmorate.storage.films;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    List<Director> getAllDirectors();
    Optional<Director> loadDirector(long id);
    long saveDirector(Director director);
    void updateDirector(Director director);
    void deleteDirector(long id);
    List<Director> loadDirectorsByFilmId(long id);
    void saveDirectorsToFilm(long id, List<Director> directors);
    void deleteDirectorsOfFilm(long id);
}

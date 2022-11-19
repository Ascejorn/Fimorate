package ru.yandex.practicum.filmorate.storage.films;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {

    Optional<Genre> loadGenreById(long id);

    List<Genre> loadGenresByFilmId(long id);

    void saveGenresToFilm(long id, List<Genre> genres);

    void deleteGenresFromFilm(long id);

    List<Genre> loadAllGenres();
}

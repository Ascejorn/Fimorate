package ru.yandex.practicum.filmorate.storage.films;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    Optional<Film> getFilmById(long id);

    Film addFilm(Film film);

    List<Film> getAllFilms();

    void saveLikes(long id, Set<Long> scores);

    Optional<Set<Long>> loadLikes(long id);

}


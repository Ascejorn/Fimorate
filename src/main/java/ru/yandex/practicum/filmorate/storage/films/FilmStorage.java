package ru.yandex.practicum.filmorate.storage.films;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Optional<Film> loadFilm(long id);

    long saveFilm(Film film);

    void updateFilm(Film film);

    List<Film> loadFilms();

    void saveLikeFromUser(long filmId, long userId);

    void deleteLikeFromUser(long filmId, long userId);

    boolean hasFilmLikeFromUser(long filmId, long userId);

    List<Film> loadPopularFilms(long count);

    void deleteFilm(long filmId); //удаление film
}
package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.films.FilmStorage;
import ru.yandex.practicum.filmorate.storage.users.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film createNewFilm(Film film) {
        Film newFilm = filmStorage.addFilm(film);
        log.debug("Создан новый фильм {}.", newFilm);
        return newFilm;
    }

    public Film updateFilm(Film film) {
        log.debug("Фильм до обновления {}.", film);
        Film updatedFilm = getFilmById(film.getId());
        if (film.getDescription() == null) {
            film.setDescription(updatedFilm.getDescription());
        }
        if (film.getReleaseDate() == null) {
            film.setReleaseDate(updatedFilm.getReleaseDate());
        }
        if (film.getDuration() == null) {
            film.setDuration(updatedFilm.getDuration());
        }
        if (film.getName() == null || film.getName().isBlank()) {
            film.setName(updatedFilm.getName());
        }
        log.debug("Фильм после обновления {}.", film);
        return filmStorage.addFilm(film);
    }

    public List<Film> getAllFilms() {
        List<Film> films = filmStorage.getAllFilms();
        log.debug("Загрузка {} фильмов.", films.size());
        return films;
    }

    public Film getFilmById(long id) {
        Optional<Film> film = filmStorage.getFilmById(id);
        if (film.isPresent()) {
            log.debug("Загрузка из памяти фильма {}.", film);
            return film.get();
        } else {
            log.debug("Фильм #{} не найден.", id);
            throw new NotFoundException("Фильм не найден.");
        }
    }

    public void addLike(long id, long userId) {
        if (hasNotFilmId(id)) {
            throw new NotFoundException("Фильм не найден.");
        }
        if (hasNotUserId(userId)) {
            throw new NotFoundException("Пользователь не найден.");
        }
        log.debug("Создаем лайк для фильма #{} от пользователя #{}.",  id, userId);
        Set<Long> likes = getLikes(id);
        log.debug("Количество лайков перед добавлением {}.", likes.size());
        likes.add(userId);
        log.debug("Количество лайков после добавления {}.", likes.size());
        filmStorage.saveLikes(id, likes);
    }

    public void deleteLike(long id, long userId) {
        if (hasNotFilmId(id)) {
            throw new NotFoundException("Фильм не найден.");
        }
        if (hasNotUserId(userId)) {
            throw new NotFoundException("Пользователь не найден.");
        }
        log.debug("Удаляем лайки с фильма #{} от пользвателя #{}.",  id, userId);
        Set<Long> likes = getLikes(id);
        log.debug("Количество лайков перед удалением {}.", likes.size());
        likes.remove(userId);
        log.debug("Количество лайков после удаления {}.", likes.size());
        filmStorage.saveLikes(id, likes);
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> popular = getAllFilms().stream()
                .sorted((f1, f2) -> {
                    if (filmStorage.loadLikes(f1.getId()).isEmpty() &&
                            filmStorage.loadLikes(f1.getId()).isEmpty()) {
                        return 0;
                    } else if (filmStorage.loadLikes(f2.getId()).isEmpty()) {
                        return -1;
                    } else if (filmStorage.loadLikes(f1.getId()).isEmpty()) {
                        return 1;
                    } else {
                        return filmStorage.loadLikes(f2.getId()).get().size()
                                - filmStorage.loadLikes(f1.getId()).get().size();
                    }
                })
                .limit(count)
                .collect(Collectors.toList());
        log.debug("Возвращаем {} популярных фильмов.", popular.size());
        return popular;
    }

    private Set<Long> getLikes(long id) {
        return filmStorage.loadLikes(id).orElseGet(HashSet::new);
    }

    private boolean hasNotFilmId(long id) {
        return filmStorage.getFilmById(id).isEmpty();
    }

    private boolean hasNotUserId(long id) {
        return userStorage.getUserById(id).isEmpty();
    }
}

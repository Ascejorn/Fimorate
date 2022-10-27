package ru.yandex.practicum.filmorate.storage.films;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component("filmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private long filmIdGenerator;
    private final Map<Long, Film> films;
    private final Map<Long, Set<Long>> likes;

    public InMemoryFilmStorage() {
        filmIdGenerator = 0;
        films = new HashMap<>();
        likes = new HashMap<>();
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        log.debug("Получение фильма из памяти {}", films.get(id));
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film addFilm(Film film) {
        if (film.getId() == 0) film.setId(++filmIdGenerator);
        log.debug("Генерация ID для фильма {}", film);
        films.put(film.getId(), film);
        log.debug("Сохранение в память {} фильма.", film);
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        log.debug("Получение всех ({}) фильмов.", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public void saveLikes(long id, Set<Long> newLikes) {
        likes.put(id, newLikes);
        log.debug("Сохраняем для id #{} в память {} лайки.", id, newLikes.size());
    }

    @Override
    public Optional<Set<Long>> loadLikes(long id) {
        int count = (likes.get(id) == null) ? 0 : likes.get(id).size();
        log.debug(
                "Загрузка из памяти {} лайков для id #{}",
                count,
                id
        );
        return Optional.ofNullable(likes.get(id));
    }
}

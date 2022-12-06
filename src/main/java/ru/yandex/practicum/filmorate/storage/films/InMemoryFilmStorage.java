package ru.yandex.practicum.filmorate.storage.films;


import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private long filmIdGenerator;
    private final Map<Long, Film> films;
    private final Map<Long, Set<Long>> likes;

    public InMemoryFilmStorage() {
        filmIdGenerator = 0L;
        films = new HashMap<>();
        likes = new HashMap<>();
    }

    @Override
    public Optional<Film> loadFilm(long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public long saveFilm(Film film) {
        if (film.getId() == 0) film.setId(++filmIdGenerator);
        films.put(film.getId(), film);
        return film.getId();
    }

    @Override
    public void updateFilm(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public ArrayList<Film> loadFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void saveLikeFromUser(long filmId, long userId) {
        Set<Long> filmLikes = likes.get(filmId);
        filmLikes.add(userId);
        likes.put(filmId, filmLikes);
    }

    @Override
    public void deleteLikeFromUser(long filmId, long userId) {
        Set<Long> filmLikes = likes.get(filmId);
        filmLikes.remove(userId);
        likes.put(filmId, filmLikes);
    }

    @Override
    public boolean hasFilmLikeFromUser(long filmId, long userId) {
        Optional<Set<Long>> filmLikes =  Optional.ofNullable(likes.get(filmId));
        return filmLikes.map(l -> l.contains(userId)).orElse(false);
    }

    @Override
    public List<Film> loadPopularFilms(long count) {
        return new ArrayList<>(films.values()).stream()
                .sorted((f1, f2) -> {
                    if (loadLikes(f1.getId()) == 0 && loadLikes(f1.getId()) == 0) {
                        return 0;
                    } else if (loadLikes(f2.getId()) == 0) {
                        return -1;
                    } else if (loadLikes(f1.getId()) == 0) {
                        return 1;
                    } else {
                        return loadLikes(f2.getId()) - loadLikes(f1.getId());
                    }
                })
                .limit(count)
                .collect(Collectors.toList());
    }

    public void deleteFilm(long filmId) { 
        films.remove(filmId);
        
    }
    @Override
    public List<Film> loadFilmsOfDirectorSortedByYears(long directorId) {
        return null;
    }

    @Override
    public List<Film> loadFilmsOfDirectorSortedByLikes(long directorId) {
        return null;
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        return null;
    }

    private int loadLikes(long filmId) {
        return likes.get(filmId).size();
    }
}
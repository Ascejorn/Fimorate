package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class FilmController {

    private Map<Integer, Film> films = new HashMap<>();
    private int generatedId = 1;

    private void validate(Film film) {
        if (!StringUtils.hasLength(film.getName())) {
            log.error("Название не может быть пустым.");
            throw new ValidationException("Название не может быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            log.error("Максимальная длина описания — 200 символов превышена.");
            throw new ValidationException("Максимальная длина описания — 200 символов превышена.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))) {
            log.error("Дата релиза указана неверно.");
            throw new ValidationException("Дата релиза указана неверно.");
        }
        if (film.getDuration() < 0.0) {
            log.error("Продолжительность фильма должна быть положительной.");
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
    }

    @PostMapping("/films")
    public Film createFilm(@RequestBody Film film) {
        film.setId(generatedId++);
        validate(film);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Такой фильм не существует.");
            throw new ValidationException("Такой фильм не существует.");
        } else {
            films.put(film.getId(), film);
            return film;
        }
    }

    @GetMapping("films")
    public List<Film> getFilms() {
        log.info("Получен запрос на получение списка фильмов.");
        return new ArrayList<>(films.values());
    }
}

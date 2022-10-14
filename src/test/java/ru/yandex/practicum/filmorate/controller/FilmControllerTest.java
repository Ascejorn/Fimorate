package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {

    @Autowired
    private FilmController filmController;

    @Test
    void createFilmOrdinaryEmailTest() {
        Film testFilm = new Film(1,"Jaws10","Film about sharks.",
                LocalDate.of(2025,01,30),120);
        filmController.createFilm(testFilm);
        assertTrue(filmController.getFilms().contains(testFilm));
        assertEquals(testFilm,filmController.getFilms().get(0));
    }

    @Test
    void createFilmWithBlankNameTest() {
        Film testFilm = new Film(1,"","Film about sharks.",
                LocalDate.of(2025,01,30),120);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.createFilm(testFilm));
        assertEquals(exception.getMessage(),"Название не может быть пустым.");
    }

    @Test
    void createFilmWithMoreThan200SymbolsDescriptionTest() {
        Film testFilm = new Film(1,"Jaws10","Film about sharks and sharks and more sharks " +
                "and more sharks and more sharks and more sharks and more sharks and more sharks and more sharks " +
                "and more sharks and more sharks and more sharks and more sharks and more sharks and more sharks " +
                "and more sharks and more sharks and more sharks and more sharks and more sharks and more sharks " +
                "and more sharks.", LocalDate.of(2025,01,30),120);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.createFilm(testFilm));
        assertEquals(exception.getMessage(),"Максимальная длина описания — 200 символов превышена.");
    }

    @Test
    void createFilmWithDateReleaseInIncorrectPastTest() {
        Film testFilm = new Film(1,"Jaws10","Film about sharks.",
                LocalDate.of(1895,12,27),120);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.createFilm(testFilm));
        assertEquals(exception.getMessage(),"Дата релиза указана неверно.");
    }

    @Test
    void createFilmWithMinusDurationTest() {
        Film testFilm = new Film(1,"Jaws10","Film about sharks.",
                LocalDate.of(2025,12,27),-120);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.createFilm(testFilm));
        assertEquals(exception.getMessage(),"Продолжительность фильма должна быть положительной.");
    }
}
package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.films.FilmStorage;
import ru.yandex.practicum.filmorate.storage.films.GenreStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreStorageTest {

    private final GenreStorage genreStorage;
    private final FilmStorage filmStorage;

    @Test
    @DisplayName("Get genre by id")
    void shouldReturnGenre() {
        Optional<Genre> genreOptional = genreStorage.loadGenreById(1);
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    @DisplayName("Get genre size 6")
    void shouldReturnArrayListGenreSize_6() {
        List<Genre> genre = genreStorage.loadAllGenres();
        assertThat(genre).hasSize(6);
    }

    @Test
    @DisplayName("Get genres by film id")
    void shouldReturnGenresOfFilmById() {
        long filmId = filmStorage.saveFilm(
                Film.builder()
                        .name("New Film")
                        .description("Film description")
                        .duration(122)
                        .releaseDate(LocalDate.parse("2020-08-01"))
                        .mpa(Mpa.builder().id(1).build())
                        .build()
        );
        List<Genre> testGenres = genreStorage.loadAllGenres().subList(0,3);
        genreStorage.saveGenresToFilm(filmId, testGenres);
        List<Genre> genres = genreStorage.loadGenresByFilmId(filmId);
        assertThat(genres).hasSize(3).containsAll(testGenres);
    }
}


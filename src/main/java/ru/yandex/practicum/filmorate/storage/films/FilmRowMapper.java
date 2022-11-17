package ru.yandex.practicum.filmorate.storage.films;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmRowMapper implements RowMapper<Film> {

    private final GenreService genreService;

    public FilmRowMapper(GenreService genreService) {
        this.genreService = genreService;
    }


    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = Mpa.builder().
                id(resultSet.getLong("mpa_id"))
                .name(resultSet.getString("mpa"))
                .build();
        return Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getLong("duration"))
                .mpa(mpa)
                .genres(genreService.getFilmGenresById(resultSet.getLong("id")))
                .build();
    }
}
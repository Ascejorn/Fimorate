package ru.yandex.practicum.filmorate.storage.films;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository("filmStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Film> loadFilm(long id) {
        String sqlQuery =
                "SELECT f.id, " +
                        "f.name, " +
                        "f.description, " +
                        "f.release_date, " +
                        "f.duration, " +
                        "f.mpa_id, " +
                        "m.name mpa " +
                        "FROM films f " +
                        "JOIN mpa m" +
                        "    ON m.id = f.mpa_id " +
                        "WHERE f.id = ?;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapRow(rs, id), id).stream().findAny();
    }

    @Override
    public long saveFilm(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"id"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setLong(4, film.getDuration());
            statement.setLong(5, film.getMpa().getId());
            return statement;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public void updateFilm(Film film) {
        String sqlQuery = "UPDATE films " +
                "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE id = ?;";
        jdbcTemplate.update(
                sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
    }

    @Override
    public List<Film> loadFilms() {
        String sqlQuery =
                "SELECT f.id, " +
                        "f.name, " +
                        "f.description, " +
                        "f.release_date, " +
                        "f.duration, " +
                        "f.mpa_id, " +
                        "m.name mpa " +
                        "FROM films f " +
                        "JOIN mpa m" +
                        "    ON m.id = f.mpa_id;";
        return jdbcTemplate.query(sqlQuery, this::mapRow);
    }

    @Override
    public void saveLikeFromUser(long filmId, long userId) {
        String sqlQuery = "INSERT INTO LIKES (film_id, user_id) VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void deleteLikeFromUser(long filmId, long userId) {
        String sqlQuery = "DELETE FROM LIKES WHERE film_id = ? AND user_id = ?;";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public boolean hasFilmLikeFromUser(long filmId, long userId) {
        String sqlQuery = "SELECT COUNT(user_id) FROM LIKES WHERE film_id = ? AND user_id = ?;";
        int rating = jdbcTemplate.queryForObject(sqlQuery, Integer.class, filmId, userId);
        return rating > 0;
    }

    @Override
    public List<Film> loadPopularFilms(long count) {
        String sqlQuery =
                "SELECT f.id, " +
                        "f.name, " +
                        "f.description, " +
                        "f.release_date, " +
                        "f.duration, " +
                        "f.mpa_id, " +
                        "m.name mpa " +
                        "FROM films f " +
                        "JOIN mpa m" +
                        "    ON m.id = f.mpa_id " +
                        "LEFT JOIN (SELECT film_id, " +
                        "      COUNT(user_id) rating " +
                        "      FROM LIKES " +
                        "      GROUP BY film_id " +
                        ") r ON f.id =  r.film_id " +
                        "ORDER BY r.rating DESC " +
                        "LIMIT ?;";
        return jdbcTemplate.query(sqlQuery, this::mapRow, count);
    }

    public void deleteFilm(long filmId){
        String sql = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private Film mapRow(ResultSet resultSet, long rowNum) throws SQLException {
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
                .genres(getFilmGenresById(resultSet.getLong("id")))
                .build();
    }

    private List<Genre> getFilmGenresById(long id) {
        String sqlQuery =
                "SELECT g.id, g.name " +
                        "FROM films_genres f " +
                        "JOIN genres g " +
                        "    ON g.id = f.genre_id " +
                        "WHERE f.film_id = ?;";
        return jdbcTemplate.query(sqlQuery, new BeanPropertyRowMapper<>(Genre.class), id);
    }
}
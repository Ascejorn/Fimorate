package ru.yandex.practicum.filmorate.storage.films;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Genre> loadGenreById(long id) {
        String sqlQuery = "SELECT id, name FROM genres WHERE id = ?;";
        return jdbcTemplate.query(sqlQuery, new BeanPropertyRowMapper<>(Genre.class), id).stream().findAny();
    }

    @Override
    public List<Genre> loadGenresByFilmId(long id) {
        String sqlQuery =
                "SELECT g.id, g.name " +
                        "FROM films_genres f " +
                        "JOIN genres g " +
                        "    ON g.id = f.genre_id " +
                        "WHERE f.film_id = ?;";
        return jdbcTemplate.query(sqlQuery, new BeanPropertyRowMapper<>(Genre.class), id);
    }

    @Override
    public void saveGenresToFilm(long filmId, List<Genre> genres) {
        List<Genre> genresDistinct = genres.stream().distinct().collect(Collectors.toList());
        jdbcTemplate.batchUpdate(
                "INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?);",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement statement, int i) throws SQLException {
                        statement.setLong(1, filmId);
                        statement.setLong(2, genresDistinct.get(i).getId());
                    }

                    public int getBatchSize() {
                        return genresDistinct.size();
                    }
                });
    }

    @Override
    public void deleteGenresFromFilm(long id) {
        String sqlQuery = "DELETE FROM films_genres WHERE film_id = ?;";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<Genre> loadAllGenres() {
        String sqlQuery = "SELECT id, name FROM genres;";
        return jdbcTemplate.query(sqlQuery, new BeanPropertyRowMapper<>(Genre.class));
    }
}

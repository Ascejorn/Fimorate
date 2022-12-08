package ru.yandex.practicum.filmorate.storage.films;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository("filmStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
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
        String sqlQuery = "INSERT INTO likes (film_id, user_id) VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void deleteLikeFromUser(long filmId, long userId) {
        String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?;";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public boolean hasFilmLikeFromUser(long filmId, long userId) {
        String sqlQuery = "SELECT COUNT(user_id) FROM likes WHERE film_id = ? AND user_id = ?;";
        int rating = jdbcTemplate.queryForObject(sqlQuery, Integer.class, filmId, userId);
        return rating > 0;
    }

    @Override
    public List<Film> loadPopularFilms(long count, Long genreId, Integer year) {

        String sqlYear = "SELECT * FROM films WHERE YEAR(release_date) = ?";

        String sqlGenre = "SELECT f.* FROM films AS f JOIN films_genres AS fg ON fg.film_id = f.id AND fg.genre_id = ?";

        String sqlGenreAndYear = "SELECT f.* FROM films AS f " +
                "JOIN films_genres AS fg ON fg.genre_id = ? " +
                "GROUP BY f.id HAVING YEAR(f.release_date) = ?";

        String sqlQuery =
                "SELECT f.id, " +
                        "f.name, " +
                        "f.description, " +
                        "f.release_date, " +
                        "f.duration, " +
                        "f.mpa_id, " +
                        "m.name mpa " +
                        "FROM ({}) f " +
                        "JOIN mpa m" +
                        "    ON m.id = f.mpa_id " +
                        "LEFT JOIN (SELECT film_id, " +
                        "      COUNT(user_id) rating " +
                        "      FROM likes " +
                        "      GROUP BY film_id " +
                        ") r ON f.id =  r.film_id " +
                        "ORDER BY r.rating DESC " +
                        "LIMIT ?";

        Object[] sqlParams;

        if (genreId != null && year != null) {
            sqlQuery = sqlQuery.replace("{}", sqlGenreAndYear);
            sqlParams = new Object[] {genreId, year, count};
        } else if (genreId != null) {
            sqlQuery = sqlQuery.replace("{}", sqlGenre);
            sqlParams = new Object[] {genreId, count};
        } else if (year != null) {
            sqlQuery = sqlQuery.replace("{}", sqlYear);
            sqlParams = new Object[] {year, count};
        } else {
            sqlQuery = sqlQuery.replace("({})", "films");
            sqlParams = new Object[] {count};
        }

        return jdbcTemplate.query(sqlQuery, this::mapRow, sqlParams);
    }

    public void deleteFilm(long filmId){
        String sql = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    public List<Film> getRecommendation(long id){
        String sql =
                "SELECT fl.id, fl.name, fl.description, fl.release_date, fl.duration, fl.mpa_id, m.name mpa" +
                        " FROM films fl JOIN mpa m ON m.id = fl.mpa_id " +
                        " WHERE fl.id IN (" +
                        "       SELECT DISTINCT l.film_id FROM likes  l " +
                        " WHERE l.user_id IN (" +
                        "       SELECT l.user_id FROM likes AS l" +
                        " WHERE l.film_id IN (" +
                        "       SELECT f.id FROM films AS f" +
                        " RIGHT JOIN likes  l ON f.id = l.film_id" +
                        " WHERE l.user_id = ? )" +
                        "   AND l.user_id <> ?)" +
                        "   AND l.film_id NOT IN " +
                        "       (SELECT l.film_id FROM likes  l" +
                        " WHERE l.user_id = ?))";
        return jdbcTemplate.query(sql, this::mapRow, id, id, id);
    }

    @Override
    public List<Film> loadFilmsOfDirectorSortedByYears(long directorId) {
        String sqlQuery =
                "SELECT f.id, " +
                        "f.name, " +
                        "f.description, " +
                        "f.release_date, " +
                        "f.duration, " +
                        "f.mpa_id, " +
                        "m.name mpa, " +
                        "YEAR(f.release_date) years " +
                        "FROM films f " +
                        "JOIN mpa m" +
                        "    ON m.id = f.mpa_id " +
                        "JOIN films_directors fd " +
                        "    ON fd.film_id = f.id " +
                        "WHERE fd.director_id = ? " +
                        "ORDER BY years ASC;";
        return jdbcTemplate.query(sqlQuery, this::mapRow, directorId);
    }

    @Override
    public List<Film> loadFilmsOfDirectorSortedByLikes(long directorId) {
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
                        "JOIN films_directors fd " +
                        "    ON fd.film_id = f.id " +
                        "LEFT JOIN (SELECT film_id, " +
                        "      COUNT(user_id) rating " +
                        "      FROM likes " +
                        "      GROUP BY film_id " +
                        ") r ON f.id =  r.film_id " +
                        "WHERE fd.director_id = ? " +
                        "ORDER BY r.rating ASC;";
        return jdbcTemplate.query(sqlQuery, this::mapRow, directorId);
    }

    private Film mapRow(ResultSet resultSet, long rowNum) throws SQLException {
        Mpa mpa = Mpa.builder()
                .id(resultSet.getLong("mpa_id"))
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
                .directors(getFilmDirectorsById(resultSet.getLong("id")))
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

    private List<Director> getFilmDirectorsById(long id) {
        String sqlQuery =
                "SELECT d.id, d.name " +
                        "FROM directors d " +
                        "JOIN films_directors f " +
                        "ON f.director_id = d.id " +
                        "WHERE f.film_id = ?;";
        return jdbcTemplate.query(sqlQuery, new BeanPropertyRowMapper<>(Director.class), id);
    }

    @Override
    public List<Film> searchFilm(String query, String by) {
        String sqlSearchPopularFilms = "SELECT f.*, m.name AS mpa, COUNT(fl.user_id) AS rating FROM films AS f " +
                "LEFT JOIN mpa m ON m.id = f.mpa_id " +
                "LEFT JOIN likes AS fl ON fl.film_id = f.id " +
                "WHERE f.id IN ({}) " +
                "GROUP BY f.id " +
                "ORDER BY rating DESC";

        String subSqlTitle = "SELECT id FROM films WHERE name ILIKE CONCAT ('%', ?1, '%')";

        String subSqlDirector = "SELECT f.id AS id FROM films AS f " +
                "INNER JOIN films_directors AS fd ON fd.film_id = f.id " +
                "INNER JOIN directors AS d ON d.id = fd.director_id " +
                "WHERE d.name ILIKE CONCAT ('%', ?1, '%')";

        String subSqlTitleDirector = String.format("%s UNION %s", subSqlTitle, subSqlDirector);

        String sqlQuery;

        switch (by) {
            case "title":
                sqlQuery = sqlSearchPopularFilms.replace("{}", subSqlTitle);
                break;
            case "director":
                sqlQuery = sqlSearchPopularFilms.replace("{}", subSqlDirector);
                break;
            case "title,director":
            case "director,title":
                sqlQuery = sqlSearchPopularFilms.replace("{}", subSqlTitleDirector);
                break;
            default:
                return new ArrayList<>();
        }

        return jdbcTemplate.query(sqlQuery, this::mapRow, query);
    }


    @Override
    public List<Film> getCommonFilms(long id, long friendId){
        String sqlQuery ="SELECT f.id, " +
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
                "      FROM likes " +
                "      GROUP BY film_id " +
                ") r ON f.id =  r.film_id " +
                "WHERE f.id IN (SELECT l.film_id FROM likes AS l" +
                " WHERE l.user_id = ? or l.user_id = ? " +
                " GROUP BY l.film_id HAVING Count(*)>1)"+
                "ORDER BY r.rating DESC;";

        return jdbcTemplate.query(sqlQuery, this::mapRow,id,friendId);
    }
}
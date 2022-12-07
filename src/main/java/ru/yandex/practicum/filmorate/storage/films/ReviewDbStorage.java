package ru.yandex.practicum.filmorate.storage.films;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Review> loadReview(long id) {

        String sqlQuery =
                "SELECT  id ,r.film_id, r.user_id, " +
                        "r.is_positive, r.content, " +
                        "COALESCE(SUM(CASE WHEN rr.useful THEN 1 WHEN rr.USEFUL = false THEN -1 ELSE 0 END), 0) as useful " +
                        "FROM reviews r " +
                        "LEFT JOIN review_rating rr" +
                        "  ON r.id = rr.review_id " +
                        "WHERE r.id = ? " +
                        "GROUP BY  r.id;";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapRow(rs, id), id).stream().findAny();
    }

    @Override
    public long saveReview(Review review) {

        String sqlQueryCheckData = "SELECT COUNT(*) FROM(" +
                "SELECT id " +
                "FROM USERS u " +
                "WHERE id = ? " +
                "UNION ALL " +
                "SELECT id " +
                "FROM FILMS " +
                "WHERE id = ?)";


        if (jdbcTemplate.queryForObject(sqlQueryCheckData, Integer.class, review.getUserId(), review.getFilmId()) != 2) {
            throw new NotFoundException("film or user not found");
        }

        String sqlQuery = "INSERT INTO REVIEWS (film_id, user_id, is_positive, content) " +
                "VALUES (?, ?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(sqlQuery, new String[]{"id"});
            statement.setLong(1, review.getFilmId());
            statement.setLong(2, review.getUserId());
            statement.setBoolean(3, review.getIsPositive());
            statement.setString(4, review.getContent());

            return statement;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public void updateReview(Review review) {
        String sqlQuery = "UPDATE reviews " +
                "SET content = ?, is_positive = ? " +
                "WHERE id = ?;";
        jdbcTemplate.update(
                sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()
        );
    }

    @Override
    public List<Review> loadReviewsByFilm(long film_id, int count) {

        String sqlQuery = "SELECT r.id, r.film_id, r.user_id, " +
                "r.is_positive, r.content, " +
                "COALESCE(SUM(CASE WHEN rr.useful THEN 1 WHEN rr.useful = false THEN -1 ELSE 0 END), 0) as useful " +
                "FROM reviews r " +
                "LEFT JOIN review_rating rr" +
                "  ON r.id = rr.review_id " +
                "WHERE r.film_id = ? " +
                "GROUP BY r.film_id, id " +
                "ORDER BY useful DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sqlQuery, this::mapRow, film_id, count);
    }

    @Override
    public List<Review> loadReviews() {
        String sqlQuery = "SELECT r.id as id, r.film_id, r.user_id, " +
                "r.is_positive, r.content, " +
                "COALESCE(SUM(CASE WHEN rr.useful THEN 1 WHEN rr.USEFUL = false THEN -1 ELSE 0 END), 0) as useful " +
                "FROM reviews r " +
                "LEFT JOIN review_rating rr" +
                "  ON r.id = rr.review_id " +
                "GROUP BY film_id, ID " +
                "ORDER BY USEFUL DESC ";

        return jdbcTemplate.query(sqlQuery, this::mapRow);
    }

    @Override
    public void saveLikeFromUser(long reviewId, long userId) {
        String sqlQuery = getQueryForLike();
        jdbcTemplate.update(sqlQuery, reviewId, userId, true);
    }

    @Override
    public void saveDislikeFromUser(long reviewId, long userId) {
        String sqlQuery = getQueryForLike();
        jdbcTemplate.update(sqlQuery, reviewId, userId, false);
    }

    private String getQueryForLike() {
        return "INSERT INTO REVIEW_RATING (review_id, user_id, useful) " +
                "VALUES (?, ?, ?)";
    }

    @Override
    public void deleteLikeFromUser(long reviewId, long userId) {
        String sqlQuery = "DELETE FROM review_rating WHERE review_id = ? AND user_id = ? AND useful = TRUE";
        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }

    @Override
    public void deleteDislikeFromUser(long reviewId, long userId) {
        String sqlQuery = "DELETE FROM REVIEW_RATING WHERE review_id = ? AND user_id = ? AND useful = FALSE";
        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }

    @Override
    public void deleteReview(long reviewId) {
        String sqlQuery = "DELETE FROM reviews WHERE id= ?";
        jdbcTemplate.update(sqlQuery, reviewId);
    }

    private Review mapRow(ResultSet resultSet, long rowNum) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getLong("id"))
                .userId(resultSet.getLong("user_id"))
                .filmId(resultSet.getLong("film_id"))
                .content(resultSet.getString("content"))
                .useful(resultSet.getInt("useful"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .build();
    }


}

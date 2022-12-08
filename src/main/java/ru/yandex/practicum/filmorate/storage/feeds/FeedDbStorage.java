package ru.yandex.practicum.filmorate.storage.feeds;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Feed;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@Repository("feedStorage")
public class FeedDbStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    private Feed makeFeed(ResultSet resultSet, long rowNum) throws SQLException {
        return Feed.builder()
                .timestamp(resultSet.getTimestamp("event_time").toInstant().toEpochMilli())
                .userId(resultSet.getLong("user_id"))
                .eventType(resultSet.getString("event_type"))
                .operation(resultSet.getString("operation"))
                .eventId(resultSet.getLong("event_id"))
                .entityId(resultSet.getLong("entity_id"))
                .build();
    }

    @Autowired
    public FeedDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void saveFeed(long id, long entityId, EventType eventType, Operation operation){
        String sqlQuery = "INSERT INTO feeds (event_time," +
                "user_id," +
                "event_type, " +
                "operation," +
                "entity_id) " +
                "VALUES (?, ?, ?, ?, ?);";
        jdbcTemplate.update(sqlQuery, Instant.now(), id, eventType.name(), operation.name(), entityId);
    }

    @Override
    public List<Feed> getNewsFeed(long userId){
        String sql = "SELECT event_time," +
                "user_id," +
                "event_type," +
                "operation," +
                "event_id," +
                "entity_id " +
                "FROM feeds WHERE user_id = ?" +
                "ORDER BY event_id;";
        return  jdbcTemplate.query(sql, this::makeFeed,userId);
    }
}

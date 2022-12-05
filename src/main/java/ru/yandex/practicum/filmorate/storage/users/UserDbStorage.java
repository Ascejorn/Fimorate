package ru.yandex.practicum.filmorate.storage.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository("userStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<User> loadUser(long id) {
        String sqlQuery = "SELECT id, login, name, email, birthday FROM users WHERE id = ?;";
        return jdbcTemplate.query(sqlQuery, new BeanPropertyRowMapper<>(User.class), id).stream().findAny();
    }

    @Override
    public long saveUser(User user) {
        String sqlQuery = "INSERT INTO users (login, name, email, birthday) VALUES (?, ?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"id"});
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getName());
            statement.setString(3, user.getEmail());
            if (user.getBirthday() == null) {
                statement.setDate(4, null);
            } else {
                statement.setDate(4, Date.valueOf(user.getBirthday()));
            }
            return statement;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }

    @Override
    public void updateUser(User user) {
        String sqlQuery = "UPDATE users SET login = ?, name = ?, email = ?, birthday = ? WHERE id = ?;";
        jdbcTemplate.update(
                sqlQuery, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(), user.getId()
        );
    }

    @Override
    public List<User> loadUsers() {
        String sqlQuery = "SELECT id, email, login, name, birthday FROM users;";
        return jdbcTemplate.query(sqlQuery, new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public void saveFriendshipRequest(long userId, long friendId, FriendshipStatus status) {
        String sqlQuery = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?);";
        jdbcTemplate.update(sqlQuery, userId, friendId, status.name());
    }

    @Override
    public boolean isExistFriendship(long userId, long friendId) {
        String sqlQuery = "SELECT COUNT(user_id) FROM friends WHERE user_id = ? AND friend_id = ?;";
        int friendship = jdbcTemplate.queryForObject(sqlQuery, Integer.class, userId, friendId);
        return friendship > 0;
    }

    @Override
    public void deleteFriendshipRequest(long userId, long friendId) {
        String sqlQuery = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?;";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public void updateFriendshipStatus(long userId, long friendId, FriendshipStatus status) {
        String sqlQuery = "UPDATE friends SET status = ? WHERE user_id = ? AND friend_id = ?;";
        jdbcTemplate.update(sqlQuery, status.name(), userId, friendId);
    }

    @Override
    public List<User> loadUserFriends(long id) {
        String sqlQuery = "SELECT id, login, name, email, birthday " +
                "FROM (" +
                "    SELECT u.id, u.login, u.name, u.email, u.birthday" +
                "    FROM friends f" +
                "    JOIN users u" +
                "        ON f.friend_id = u.id" +
                "    WHERE f.user_id = ?" +
                "    UNION ALL" +
                "    SELECT u.id, u.login, u.name, u.email, u.birthday" +
                "    FROM friends f" +
                "    JOIN users u" +
                "        ON f.user_id = u.id" +
                "    WHERE f.friend_id = ? AND f.status = 'ACCEPTED'" +
                ") AS friends " +
                "ORDER BY id ASC;";
        return jdbcTemplate.query(sqlQuery, new BeanPropertyRowMapper<>(User.class), id, id);
    }

    @Override
    public boolean isNotExistEmail(String email) {
        String sqlQuery = "SELECT COUNT(email) FROM users WHERE email = ?";
        int user = jdbcTemplate.queryForObject(sqlQuery, Integer.class, email);
        return user == 0;
    }

    @Override
    public boolean isNotExistLogin(String login) {
        String sqlQuery = "SELECT COUNT(login) FROM users WHERE login = ?";
        int user = jdbcTemplate.queryForObject(sqlQuery, Integer.class, login);
        return user == 0;
    }

    @Override
    public void deleteUser(long userId) { //удаление User
        String sql = "DELETE FROM users WHERE id = ? ";
        jdbcTemplate.update(sql, userId);
    }
}
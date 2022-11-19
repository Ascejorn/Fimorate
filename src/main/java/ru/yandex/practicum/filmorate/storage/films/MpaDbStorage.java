package ru.yandex.practicum.filmorate.storage.films;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Repository("mpaStorage")
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Mpa> loadMpaById(long id) {
        String sqlQuery = "SELECT id, name FROM mpa WHERE id = ?;";
        return jdbcTemplate.query(sqlQuery, new BeanPropertyRowMapper<>(Mpa.class), id).stream().findAny();
    }

    @Override
    public List<Mpa> loadAllMpa() {
        String sqlQuery = "SELECT id, name FROM mpa;";
        return jdbcTemplate.query(sqlQuery, new BeanPropertyRowMapper<>(Mpa.class));
    }
}
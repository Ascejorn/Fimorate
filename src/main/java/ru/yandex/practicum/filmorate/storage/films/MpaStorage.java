package ru.yandex.practicum.filmorate.storage.films;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {

    Optional<Mpa> loadMpaById(long id);

    List<Mpa> loadAllMpa();
}
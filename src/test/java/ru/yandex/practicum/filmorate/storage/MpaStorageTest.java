package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.films.MpaStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaStorageTest {

    private final MpaStorage mpaStorage;

    @Test
    @DisplayName("Get MPA by id")
    void shouldReturnMpa() {
        Optional<Mpa> userOptional = mpaStorage.loadMpaById(1);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    @DisplayName("Get empty optional with wrong id")
    void shouldReturnEmptyOptionalIfWrongId() {
        Optional<Mpa> userOptional = mpaStorage.loadMpaById(99);
        assertThat(userOptional)
                .isNotPresent();
    }

    @Test
    @DisplayName("Get mpa size 5")
    void shouldReturnMpaSize_5() {
        List<Mpa> mpa = mpaStorage.loadAllMpa();
        assertThat(mpa).hasSize(5);
    }
}


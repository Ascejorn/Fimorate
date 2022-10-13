package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class UserController {

    private Map<Long,User> users = new HashMap<>();
    private long generatedId = 1;

    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        user.setId(generatedId++);
        validate(user);
        users.put(user.getId(), user);
        log.info("Пользователь с id " + user.getId() + " добавлен.");
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Такой пользователь не существует. Укажите пользователя с существующим id.");
            throw new ValidationException("Такой пользователь не существует.");
        }
        log.info("Пользователь с id " + user.getId() + " обновлен.");
        users.put(user.getId(), user);
        return user;
    }

    @GetMapping("users")
    public List<User> getUsers() {
        log.info("Получен запрос на получение списка пользователей.");
        return new ArrayList<>(users.values());
    }

    private void validate(User user) {
        if (!StringUtils.hasLength(user.getEmail()) || !user.getEmail().contains("@")) {
            log.error("Электронная почта не может быть пустой и должна содержать символ @ ." +
                    " Укажите email в соответствующем формате.");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @ .");
        }
        if (!StringUtils.hasLength(user.getLogin()) || user.getLogin().contains(" ")) {
            log.error("Логин не может быть пустым и содержать пробелы. Укажите логин в соответствующем формате.");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        }
        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем. Укажите дату ранее текущего момента.");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }
}

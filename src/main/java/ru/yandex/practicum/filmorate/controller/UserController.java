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

    private Map<Integer,User> users = new HashMap<>();
    private int generatedId = 1;

    private void validate(User user) {
        if (!StringUtils.hasLength(user.getEmail()) || !user.getEmail().contains("@")) {
            log.error("Электронная почта не может быть пустой и должна содержать символ @ .");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @ .");
        }
        if (!StringUtils.hasLength(user.getLogin()) || user.getLogin().contains(" ")) {
            log.error("Логин не может быть пустым и содержать пробелы.");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        }
        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем.");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        user.setId(generatedId++);
        validate(user);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Такой пользователь не существует.");
            throw new ValidationException("Такой пользователь не существует.");
        } else {
            users.put(user.getId(), user);
            return user;
        }
    }

    @GetMapping("users")
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }
}

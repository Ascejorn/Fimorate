package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.users.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage storage) {
        this.userStorage = storage;
    }

    public User createNewUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        User newUser = userStorage.addUser(user);
        log.debug("Создаем нового пользователя {}", newUser);
        return newUser;
    }

    public User updateUser(User user) {
        log.debug("User before update {}", user);
        User updatedUser = getUserById(user.getId());
        if (user.getBirthday() == null) user.setBirthday(updatedUser.getBirthday());
        if (user.getLogin() == null) user.setLogin(updatedUser.getLogin());
        if (user.getEmail() == null) user.setEmail(updatedUser.getEmail());
        log.debug("User after update {}", user);
        return userStorage.addUser(user);
    }

    public List<User> getAllUsers() {
        List<User> users = userStorage.getAllUsers();
        log.debug("Return {} users", users.size());
        return users;
    }

    public User getUserById(long id) {
        Optional<User> user = userStorage.getUserById(id);
        if (user.isPresent()) {
            log.debug("Загрузка из хранилища пользователя {}.", user);
            return user.get();
        } else {
            log.debug("Пользователь #{} не найден.", id);
            throw new NotFoundException("Пользователь не найден.");
        }
    }

    public void addFriendsToEachOther(long id, long friendId) {
        if (hasNotUserId(id) || hasNotUserId(friendId)) {
            throw new NotFoundException("Пользователь не найден.");
        }
        addFriend(id, friendId);
        addFriend(friendId, id);
    }

    private void addFriend(long id, long friendId) {
        Set<Long> likes = getLikes(id);
        likes.add(friendId);
        log.debug("Создаем дружбу от пользователя #{} у пользователю #{}.", friendId, id);
        userStorage.saveFriends(id, likes);
    }

    public void deleteFriendsFromEachOther(long id, long friendId) {
        if (hasNotUserId(id) || hasNotUserId(friendId)) {
            throw new NotFoundException("Пользователь не найден.");
        }
        deleteFriend(id, friendId);
        deleteFriend(friendId, id);
    }

    private void deleteFriend(long id, long friendId) {
        Set<Long> likes = getLikes(id);
        likes.remove(friendId);
        userStorage.saveFriends(id, likes);
        log.debug("Удаляем дружбу от пользователя #{} к пользователю #{}.",  id, friendId);
    }

    public List<User> getFriendsOfUserById(long id) {
        List<User> friends = getLikes(id).stream()
                .map(userStorage::getUserById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        log.debug("Возвращаем {} друзей.", friends.size());
        return friends;
    }

    public List<User> getCommonFriends(long id, long otherId) {
        List<User> friends = getLikes(id).stream()
                .filter(getLikes(otherId)::contains)
                .map(userStorage::getUserById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        log.debug("Возвращаем {} общих друзей.", friends.size());
        return friends;
    }

    private Set<Long> getLikes(long id) {
        return userStorage.loadFriends(id).orElseGet(HashSet::new);
    }

    private boolean hasNotUserId(long id) {
        return userStorage.getUserById(id).isEmpty();
    }
}

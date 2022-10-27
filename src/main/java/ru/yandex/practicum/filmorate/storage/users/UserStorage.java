package ru.yandex.practicum.filmorate.storage.users;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {

    Optional<User> getUserById(long id);

    User addUser(User user);

    List<User> getAllUsers();

    void saveFriends(long id, Set<Long> likes);

    Optional<Set<Long>> loadFriends(long id);

}

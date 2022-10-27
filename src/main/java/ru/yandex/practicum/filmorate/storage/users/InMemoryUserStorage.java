package ru.yandex.practicum.filmorate.storage.users;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private int userIdGenerator;
    private final Map<Long, User> users;
    private final Map<Long, Set<Long>> friends;

    public InMemoryUserStorage() {
        userIdGenerator = 0;
        users = new HashMap<>();
        friends = new HashMap<>();
    }

    @Override
    public Optional<User> getUserById(long id) {
        log.debug("Получение из памяти пользователя {}.", users.get(id));
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User addUser(User user) {
        if (user.getId() == 0) user.setId(++userIdGenerator);
        log.debug("Генерация ID для пользователя {}.", user);
        users.put(user.getId(), user);
        log.debug("Сохранения в память пользователя [{}].", user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("Получение всех ({}) пользователей.", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public void saveFriends(long id, Set<Long> newLikes) {
        friends.put(id, newLikes);
        log.debug("Сохранение для id #{} в память {} друзей.", id, newLikes.size());
    }

    @Override
    public Optional<Set<Long>> loadFriends(long id) {
        int count = (friends.get(id) == null) ? 0 : friends.get(id).size();
        log.debug(
                "Загрузка из памяти {} друзей для id #{}.",
                count,
                id
        );
        return Optional.ofNullable(friends.get(id));
    }
}

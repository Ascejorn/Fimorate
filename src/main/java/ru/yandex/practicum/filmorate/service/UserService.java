package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.users.FriendshipStatus;
import ru.yandex.practicum.filmorate.storage.users.UserStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FeedService feedService;

    @Autowired
    public UserService(UserStorage userStorage, FeedService feedService) {
        this.userStorage = userStorage;
        this.feedService = feedService;
    }

    public User getUserById(long id) {
        Optional<User> user = userStorage.loadUser(id);
        if (user.isPresent()) {
            log.debug("Loading {}.", user.get());
            return user.get();
        } else {
            throw new NotFoundException("User #" + id + " not found.");
        }
    }

    public User createNewUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        User savedUser = getUserById(userStorage.saveUser(user));
        log.debug("Creating {}.", savedUser);
        return savedUser;
    }

    public User updateUser(User user) {
        User updatedUser = getUserById(user.getId());
        if (user.getBirthday() == null) {
            user.setBirthday(updatedUser.getBirthday());
        }
        if (user.getLogin() == null) {
            user.setLogin(updatedUser.getLogin());
        }
        if (user.getEmail() == null) {
            user.setEmail(updatedUser.getEmail());
        }
        userStorage.updateUser(user);
        User savedUser = getUserById(user.getId());
        log.debug("Updating {}.", savedUser);
        return getUserById(user.getId());
    }

    public List<User> getAllUsers() {
        List<User> users = userStorage.loadUsers();
        log.debug("Returning all ({}) users.", users.size());
        return users;
    }

    public void addFriendship(long userId, long friendId) {
        getUserById(userId);
        getUserById(friendId);
        if (userStorage.isExistFriendship(userId, friendId) || userStorage.isExistFriendship(friendId, userId)) {
            log.debug("Attempting to create an existing request for user #{} from user #{}.", userId, friendId);
        } else {
            userStorage.saveFriendshipRequest(userId, friendId, FriendshipStatus.REQUEST);
            log.debug("Creating friendship request for user #{} from user #{}.",  userId, friendId);
            feedService.saveFeed(userId, friendId, EventType.FRIEND, Operation.ADD);
        }
    }

    public void confirmFriendship(long userId, long friendId) {
        getUserById(userId);
        getUserById(friendId);
        if (userStorage.isExistFriendship(userId, friendId)) {
            userStorage.updateFriendshipStatus(userId, friendId, FriendshipStatus.ACCEPTED);
            userStorage.deleteFriendshipRequest(friendId, userId);
            log.debug("User #{} confirmed friendship request from user #{}.", userId, friendId);
        } else if (userStorage.isExistFriendship(friendId, userId)) {
            userStorage.updateFriendshipStatus(friendId, userId, FriendshipStatus.ACCEPTED);
            log.debug("User #{} confirmed friendship request of user #{}", userId, friendId);
        } else {
            log.debug("Attempting to confirm a non-existent request from user #{} to user #{}.", friendId, userId);
        }
    }

    public void refuseFriendship(long userId, long friendId) {
        getUserById(userId);
        getUserById(friendId);
        if (userStorage.isExistFriendship(userId, friendId)) {
            userStorage.deleteFriendshipRequest(userId, friendId);
            log.debug("User #{} refused friendship request from user #{}.", userId, friendId);
            feedService.saveFeed(userId, friendId, EventType.FRIEND, Operation.REMOVE);
        } else {
            log.debug("Attempting to refuse a non-existent request from user #{} to user #{}.", friendId, userId);
        }
    }

    public List<User> getUserFriends(long userI) {
        getUserById(userI);
        List<User> friends = userStorage.loadUserFriends(userI);
        log.debug("Returning {} friends.", friends.size());
        return friends;
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        List<User> friends = getUserFriends(userId);
        friends.retainAll(getUserFriends(otherUserId));
        log.debug("Returning {} common friends.", friends.size());
        return friends;
    }

    public boolean isNotExistEmail(String email) {
        return userStorage.isNotExistEmail(email);
    }

    public boolean isNotExistLogin(String login) {
        return userStorage.isNotExistLogin(login);
    }

    public void deleteUser(long userId){
        userStorage.deleteUser(userId);
        log.debug("Delete {}.", userId);
    }
}
package ru.yandex.practicum.filmorate.storage.feeds;

import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.List;

public interface FeedStorage {
    List<Feed> getNewsFeed(long userId);

    void saveFeed(long id, long entityId, EventType eventType, Operation operation);
}

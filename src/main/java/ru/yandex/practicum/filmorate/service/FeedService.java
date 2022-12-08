package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.feeds.EventType;
import ru.yandex.practicum.filmorate.storage.feeds.FeedStorage;
import ru.yandex.practicum.filmorate.storage.feeds.Operation;

import java.util.List;

@Slf4j
@Service
public class FeedService {
    private final FeedStorage feedStorage;

    public FeedService(FeedStorage feedStorage) {
        this.feedStorage = feedStorage;
    }

    public void saveFeed(long id, long entityId, EventType eventType, Operation operation){
        feedStorage.saveFeed(id, entityId, eventType, operation);
        log.debug("Event saved: User #{} {} {} #{}.",id,operation.toString().toLowerCase(), eventType.toString().toLowerCase(), entityId );
    }
    public List<Feed> getNewsFeed(long userId) {
        List<Feed> feeds = feedStorage.getNewsFeed(userId);
        log.debug("Loading {} events.", feeds.size());
        return feeds;
    }
}

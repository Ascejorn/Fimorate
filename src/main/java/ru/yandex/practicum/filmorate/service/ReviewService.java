package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.feeds.EventType;
import ru.yandex.practicum.filmorate.storage.feeds.Operation;
import ru.yandex.practicum.filmorate.storage.films.ReviewStorage;

import java.util.List;

@Slf4j
@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FeedService feedService;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage, FeedService feedService) {
        this.reviewStorage = reviewStorage;
        this.feedService = feedService;
    }

    public Review getReviewById(long id) {
        return reviewStorage.loadReview(id)
                .orElseThrow(() -> new NotFoundException("**Review** #" + id + " not found."));
    }

    public Review updateReview(Review review) {
        reviewStorage.updateReview(review);
        Review savedReview = getReviewById(review.getReviewId());
        log.debug("Updating view {}.", savedReview);
        feedService.saveFeed(savedReview.getUserId(), savedReview.getReviewId(), EventType.REVIEW, Operation.UPDATE);
        return savedReview;
    }

    public Review createNewReview(Review review) {
        long reviewId = reviewStorage.saveReview(review);
        Review savedReview = getReviewById(reviewId);
        log.debug("Creating new review {}.", savedReview);
        feedService.saveFeed(savedReview.getUserId(), savedReview.getReviewId(), EventType.REVIEW, Operation.ADD);
        return savedReview;
    }

    public List<Review> getReviews(Long filmId, int count) {
        List<Review> reviews;
        if (filmId == null) {
            reviews = reviewStorage.loadReviews();
        } else {
            reviews = reviewStorage.loadReviewsByFilm(filmId, count);
        }
        log.debug("Loading {} reviews.", reviews.size());
        return reviews;
    }

    public void addLikeFromUser(long reviewId, long userId) {
        reviewStorage.saveLikeFromUser(reviewId, userId);
        log.debug("Saved like for review #{} from user #{}.",  reviewId, userId);
    }

    public void addDislikeFromUser(long reviewId, long userId) {
        reviewStorage.saveDislikeFromUser(reviewId, userId);
        log.debug("Saved dislike for review #{} from user #{}.",  reviewId, userId);
    }

    public void deleteLikeFromUser(long reviewId, long userId) {
        reviewStorage.deleteLikeFromUser(reviewId, userId);
        log.debug("Deleted like for review #{} from user #{}.",  reviewId, userId);
    }

    public void deleteDislikeFromUser(long reviewId, long userId) {
        reviewStorage.deleteDislikeFromUser(reviewId, userId);
        log.debug("Deleted dislike for review #{} from user #{}.",  reviewId, userId);
    }

    public void deleteReview(long reviewId) {
        Review review = getReviewById(reviewId);
        reviewStorage.deleteReview(reviewId);
        feedService.saveFeed(review.getUserId(), review.getReviewId(), EventType.REVIEW, Operation.REMOVE);
    }
}

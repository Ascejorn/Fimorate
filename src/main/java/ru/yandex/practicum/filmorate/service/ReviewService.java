package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.films.ReviewStorage;

import java.util.List;

@Slf4j
@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }

    public Review getReviewById(long id) {
        return reviewStorage.loadReview(id)
                .orElseThrow(() -> new NotFoundException("**Review** #" + id + " not found."));
    }

    public Review updateReview(Review review) {
        reviewStorage.updateReview(review);
        Review savedReview = getReviewById(review.getReviewId());
        log.debug("Updating view {}.", savedReview);
        return savedReview;
    }

    public Review createNewReview(Review review) {
        long reviewId = reviewStorage.saveReview(review);

        Review savedReview = getReviewById(reviewId);
        log.debug("Creating new review {}.", savedReview);

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
        reviewStorage.deleteReview(reviewId);
    }
}

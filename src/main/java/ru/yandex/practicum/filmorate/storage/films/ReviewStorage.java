package ru.yandex.practicum.filmorate.storage.films;


import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Optional<Review> loadReview(long id);

    long saveReview(Review review);

    void updateReview(Review review);

    List<Review> loadReviews();

    List<Review> loadReviewsByFilm(long filmId, int count);

    void saveLikeFromUser(long reviewId, long userId);

    void saveDislikeFromUser(long reviewId, long userId);

    void deleteLikeFromUser(long reviewId, long userId);

    void deleteDislikeFromUser(long reviewId, long userId);

    void deleteReview(long reviewId);
}

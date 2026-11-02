package com.movierating.models;

public class Review {

    private int reviewId;
    private int movieId;
    private int userId;
    private int rating;
    private String reviewText;
    private String reviewerUsername;

    public Review() {
    }

    public Review(int movieId, int userId, int rating, String reviewText) {
        this.movieId = movieId;
        this.userId = userId;
        setRating(rating);
        setReviewText(reviewText);
    }

    public Review(int reviewId, int movieId, int userId, int rating, String reviewText, String reviewerUsername) {
        this.reviewId = reviewId;
        this.movieId = movieId;
        this.userId = userId;
        this.rating = rating;
        this.reviewText = (reviewText != null) ? reviewText.trim() : "";
        this.reviewerUsername = reviewerUsername;
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1) {
            this.rating = 1;
        } else if (rating > 5) {
            this.rating = 5;
        } else {
            this.rating = rating;
        }
    }

    public String getReviewText() {
        return (reviewText != null) ? reviewText : "";
    }

    public void setReviewText(String reviewText) {
        this.reviewText = (reviewText != null) ? reviewText.trim() : "";
    }

    public String getReviewerUsername() {
        return reviewerUsername;
    }

    public void setReviewerUsername(String reviewerUsername) {
        this.reviewerUsername = reviewerUsername;
    }

    public String getRatingStars() {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < rating; i++) {
            stars.append("★");
        }
        for (int i = rating; i < 5; i++) {
            stars.append("☆");
        }
        return stars.toString();
    }

    @Override
    public String toString() {
        String author = (reviewerUsername != null && !reviewerUsername.isEmpty()) ? reviewerUsername : "User #" + userId;
        String text = (reviewText != null && !reviewText.isEmpty()) ? reviewText : "No review written";
        return "[" + getRatingStars() + " (" + rating + "/5)] " + author + ": " + text;
    }
}

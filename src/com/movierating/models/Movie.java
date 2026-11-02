package com.movierating.models;

import com.movierating.exceptions.EmptyFieldException;

public class Movie {

    private int movieId;
    private int userId;
    private String title;
    private String genre;
    private String description;
    private double averageRating;
    private boolean rewatchable;
    private String userReview;

    public Movie() {
    }

    public Movie(String title, String genre, String description, int userId, double averageRating, boolean rewatchable) {
        this.title = title;
        this.genre = genre;
        this.description = description;
        this.userId = userId;
        this.averageRating = averageRating;
        this.rewatchable = rewatchable;
    }

    public Movie(int movieId, int userId, String title, String genre, String description, double averageRating, boolean rewatchable) {
        this.movieId = movieId;
        this.userId = userId;
        this.title = title;
        this.genre = genre;
        this.description = description;
        this.averageRating = averageRating;
        this.rewatchable = rewatchable;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) throws EmptyFieldException {
        if (title == null || title.trim().isEmpty()) {
            throw new EmptyFieldException("Movie title cannot be empty.");
        }
        this.title = title.trim();
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) throws EmptyFieldException {
        if (genre == null || genre.trim().isEmpty()) {
            throw new EmptyFieldException("Movie genre cannot be empty.");
        }
        this.genre = genre.trim();
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public void setDescription(String description) {
        this.description = (description != null) ? description.trim() : "";
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        if (averageRating < 0.0) {
            this.averageRating = 0.0;
        } else if (averageRating > 5.0) {
            this.averageRating = 5.0;
        } else {
            this.averageRating = averageRating;
        }
    }

    public boolean isRewatchable() {
        return rewatchable;
    }

    public void setRewatchable(boolean rewatchable) {
        this.rewatchable = rewatchable;
    }

    public String getUserReview() {
        return userReview != null ? userReview : "";
    }

    public void setUserReview(String userReview) {
        this.userReview = userReview;
    }

    public String getFormattedRating() {
        if (averageRating <= 0.0) {
            return "No rating";
        }
        return String.format("%.1f / 5.0", averageRating);
    }

    @Override
    public String toString() {
        return title + " (" + genre + ") - " + getFormattedRating();
    }
}

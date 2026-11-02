package com.movierating.database;

import com.movierating.models.BaseUser;
import com.movierating.models.Movie;
import com.movierating.models.Review;
import com.movierating.exceptions.EmptyFieldException;

import java.sql.SQLException;
import java.util.List;

public interface DatabaseOperations {

    void initializeDatabase() throws SQLException;

    BaseUser authenticateUser(String username, String password) throws SQLException, EmptyFieldException;

    boolean registerUser(BaseUser user) throws SQLException, EmptyFieldException;

    List<BaseUser> getAllUsers() throws SQLException;

    void deleteUser(int userId) throws SQLException;

    void updateUserRole(int userId, String newRole) throws SQLException;

    List<Movie> getMoviesForUser(int userId) throws SQLException;

    Movie getMovieById(int movieId) throws SQLException;

    List<Movie> searchMovies(int userId, String title) throws SQLException;

    List<Movie> searchMovies(int userId, String title, String genre) throws SQLException;

    void addMovieWithReview(Movie movie, Review review) throws SQLException, EmptyFieldException;

    void addReview(Review review) throws SQLException, EmptyFieldException;

    List<Review> getReviewsForMovie(int movieId) throws SQLException;

    void updateMovieAverageRating(int movieId) throws SQLException;
}

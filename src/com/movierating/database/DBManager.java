package com.movierating.database;

import com.movierating.exceptions.EmptyFieldException;
import com.movierating.models.AdminUser;
import com.movierating.models.BaseUser;
import com.movierating.models.Movie;
import com.movierating.models.Review;
import com.movierating.models.ViewerUser;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBManager implements DatabaseOperations {

    private static final String DB_URL = "jdbc:sqlite:cinematica.db";

    public DBManager() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found: " + e.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    @Override
    public void initializeDatabase() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON;");

            String createUsersTable = "CREATE TABLE IF NOT EXISTS Users (" +
                    "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "role TEXT NOT NULL CHECK(role IN ('Admin', 'Viewer'))" +
                    ");";
            stmt.execute(createUsersTable);

            String createMoviesTable = "CREATE TABLE IF NOT EXISTS Movies (" +
                    "movie_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER NOT NULL, " +
                    "title TEXT NOT NULL, " +
                    "genre TEXT NOT NULL, " +
                    "description TEXT, " +
                    "average_rating REAL DEFAULT 0.0, " +
                    "rewatchable INTEGER DEFAULT 0, " +
                    "FOREIGN KEY(user_id) REFERENCES Users(user_id) ON DELETE CASCADE" +
                    ");";
            stmt.execute(createMoviesTable);

            String createReviewsTable = "CREATE TABLE IF NOT EXISTS Reviews (" +
                    "review_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "movie_id INTEGER NOT NULL, " +
                    "user_id INTEGER NOT NULL, " +
                    "rating INTEGER NOT NULL CHECK(rating >= 1 AND rating <= 5), " +
                    "review_text TEXT DEFAULT '', " +
                    "FOREIGN KEY(movie_id) REFERENCES Movies(movie_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY(user_id) REFERENCES Users(user_id) ON DELETE CASCADE" +
                    ");";
            stmt.execute(createReviewsTable);

            seedInitialDataIfEmpty(conn);
        }
    }

    private void seedInitialDataIfEmpty(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM Users")) {
            if (rs.next() && rs.getInt("total") == 0) {
                String insertUsers = "INSERT INTO Users (username, password, role) VALUES " +
                        "('admin', 'admin123', 'Admin'), " +
                        "('alice', 'alice123', 'Viewer'), " +
                        "('bob', 'bob123', 'Viewer');";
                stmt.executeUpdate(insertUsers);
            }
        }

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM Movies")) {
            if (rs.next() && rs.getInt("total") == 0) {
                String insertMovies = "INSERT INTO Movies (user_id, title, genre, description, average_rating, rewatchable) VALUES " +
                        "(1, 'Inception', 'Sci-Fi', 'A skilled thief who steals secrets through dream-sharing technology.', 5.0, 1), " +
                        "(1, 'The Dark Knight', 'Action', 'Batman must face his greatest psychological and physical test.', 5.0, 1), " +
                        "(2, 'Interstellar', 'Sci-Fi', 'Explorers travel through a wormhole to find humanity a new home.', 4.8, 1), " +
                        "(2, 'Spirited Away', 'Animation', 'A young girl wanders into a spirit world to save her parents.', 5.0, 1), " +
                        "(3, 'Pulp Fiction', 'Crime', 'The lives of mob hitmen and a boxer intertwine in violent tales.', 4.5, 0);";
                stmt.executeUpdate(insertMovies);

                String insertReviews = "INSERT INTO Reviews (movie_id, user_id, rating, review_text) VALUES " +
                        "(1, 1, 5, 'Mind-bending visual masterpiece and stunning soundtrack!'), " +
                        "(2, 1, 5, 'Heath Ledger gives the greatest villain performance of all time.'), " +
                        "(3, 2, 5, 'Emotional and scientifically fascinating journey.'), " +
                        "(4, 2, 5, 'Visual poetry. Miyazaki at his absolute peak.'), " +
                        "(5, 3, 5, 'Classic storytelling with unforgettable dialogue.');";
                stmt.executeUpdate(insertReviews);
            }
        }
    }

    @Override
    public BaseUser authenticateUser(String username, String password) throws SQLException, EmptyFieldException {
        if (username == null || username.trim().isEmpty()) {
            throw new EmptyFieldException("Please enter your username.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new EmptyFieldException("Please enter your password.");
        }

        String sql = "SELECT user_id, username, password, role FROM Users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username.trim());
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("user_id");
                    String uname = rs.getString("username");
                    String pwd = rs.getString("password");
                    String role = rs.getString("role");

                    if ("Admin".equalsIgnoreCase(role)) {
                        return new AdminUser(id, uname, pwd);
                    } else {
                        return new ViewerUser(id, uname, pwd);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean registerUser(BaseUser user) throws SQLException, EmptyFieldException {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new EmptyFieldException("Username cannot be empty.");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new EmptyFieldException("Password cannot be empty.");
        }

        String sql = "INSERT INTO Users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername().trim());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public List<BaseUser> getAllUsers() throws SQLException {
        List<BaseUser> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password, role FROM Users ORDER BY user_id ASC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("user_id");
                String uname = rs.getString("username");
                String pwd = rs.getString("password");
                String role = rs.getString("role");

                if ("Admin".equalsIgnoreCase(role)) {
                    users.add(new AdminUser(id, uname, pwd));
                } else {
                    users.add(new ViewerUser(id, uname, pwd));
                }
            }
        }
        return users;
    }

    @Override
    public void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM Users WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void updateUserRole(int userId, String newRole) throws SQLException {
        String sql = "UPDATE Users SET role = ? WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newRole);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Movie> getMoviesForUser(int userId) throws SQLException {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT m.movie_id, m.user_id, m.title, m.genre, m.description, m.average_rating, m.rewatchable, r.review_text " +
                     "FROM Movies m " +
                     "LEFT JOIN Reviews r ON m.movie_id = r.movie_id AND m.user_id = r.user_id " +
                     "WHERE m.user_id = ? " +
                     "ORDER BY m.movie_id DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Movie movie = new Movie(
                            rs.getInt("movie_id"),
                            rs.getInt("user_id"),
                            rs.getString("title"),
                            rs.getString("genre"),
                            rs.getString("description"),
                            rs.getDouble("average_rating"),
                            rs.getInt("rewatchable") == 1
                    );
                    movie.setUserReview(rs.getString("review_text"));
                    movies.add(movie);
                }
            }
        }
        return movies;
    }

    @Override
    public Movie getMovieById(int movieId) throws SQLException {
        String sql = "SELECT m.movie_id, m.user_id, m.title, m.genre, m.description, m.average_rating, m.rewatchable, r.review_text " +
                     "FROM Movies m " +
                     "LEFT JOIN Reviews r ON m.movie_id = r.movie_id AND m.user_id = r.user_id " +
                     "WHERE m.movie_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, movieId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Movie movie = new Movie(
                            rs.getInt("movie_id"),
                            rs.getInt("user_id"),
                            rs.getString("title"),
                            rs.getString("genre"),
                            rs.getString("description"),
                            rs.getDouble("average_rating"),
                            rs.getInt("rewatchable") == 1
                    );
                    movie.setUserReview(rs.getString("review_text"));
                    return movie;
                }
            }
        }
        return null;
    }

    @Override
    public List<Movie> searchMovies(int userId, String title) throws SQLException {
        List<Movie> results = new ArrayList<>();
        String sql = "SELECT m.movie_id, m.user_id, m.title, m.genre, m.description, m.average_rating, m.rewatchable, r.review_text " +
                     "FROM Movies m " +
                     "LEFT JOIN Reviews r ON m.movie_id = r.movie_id AND m.user_id = r.user_id " +
                     "WHERE m.user_id = ? AND m.title LIKE ? " +
                     "ORDER BY m.movie_id DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, "%" + (title != null ? title.trim() : "") + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Movie movie = new Movie(
                            rs.getInt("movie_id"),
                            rs.getInt("user_id"),
                            rs.getString("title"),
                            rs.getString("genre"),
                            rs.getString("description"),
                            rs.getDouble("average_rating"),
                            rs.getInt("rewatchable") == 1
                    );
                    movie.setUserReview(rs.getString("review_text"));
                    results.add(movie);
                }
            }
        }
        return results;
    }

    @Override
    public List<Movie> searchMovies(int userId, String title, String genre) throws SQLException {
        if (genre == null || genre.trim().isEmpty() || "All".equalsIgnoreCase(genre.trim()) || "All Genres".equalsIgnoreCase(genre.trim())) {
            return searchMovies(userId, title);
        }

        List<Movie> results = new ArrayList<>();
        String sql = "SELECT m.movie_id, m.user_id, m.title, m.genre, m.description, m.average_rating, m.rewatchable, r.review_text " +
                     "FROM Movies m " +
                     "LEFT JOIN Reviews r ON m.movie_id = r.movie_id AND m.user_id = r.user_id " +
                     "WHERE m.user_id = ? AND m.title LIKE ? AND m.genre = ? " +
                     "ORDER BY m.movie_id DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, "%" + (title != null ? title.trim() : "") + "%");
            pstmt.setString(3, genre.trim());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Movie movie = new Movie(
                            rs.getInt("movie_id"),
                            rs.getInt("user_id"),
                            rs.getString("title"),
                            rs.getString("genre"),
                            rs.getString("description"),
                            rs.getDouble("average_rating"),
                            rs.getInt("rewatchable") == 1
                    );
                    movie.setUserReview(rs.getString("review_text"));
                    results.add(movie);
                }
            }
        }
        return results;
    }

    @Override
    public void addMovieWithReview(Movie movie, Review review) throws SQLException, EmptyFieldException {
        if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
            throw new EmptyFieldException("Movie title cannot be empty.");
        }
        if (movie.getGenre() == null || movie.getGenre().trim().isEmpty()) {
            throw new EmptyFieldException("Movie genre cannot be empty.");
        }

        String insertMovieSql = "INSERT INTO Movies (user_id, title, genre, description, average_rating, rewatchable) " +
                                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                int generatedMovieId = -1;
                try (PreparedStatement pstmtMovie = conn.prepareStatement(insertMovieSql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmtMovie.setInt(1, movie.getUserId());
                    pstmtMovie.setString(2, movie.getTitle().trim());
                    pstmtMovie.setString(3, movie.getGenre().trim());
                    pstmtMovie.setString(4, movie.getDescription().trim());
                    pstmtMovie.setDouble(5, review.getRating());
                    pstmtMovie.setInt(6, movie.isRewatchable() ? 1 : 0);

                    pstmtMovie.executeUpdate();

                    try (ResultSet generatedKeys = pstmtMovie.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            generatedMovieId = generatedKeys.getInt(1);
                        }
                    }
                }

                if (generatedMovieId != -1) {
                    String insertReviewSql = "INSERT INTO Reviews (movie_id, user_id, rating, review_text) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement pstmtReview = conn.prepareStatement(insertReviewSql)) {
                        pstmtReview.setInt(1, generatedMovieId);
                        pstmtReview.setInt(2, movie.getUserId());
                        pstmtReview.setInt(3, review.getRating());
                        String reviewText = (review.getReviewText() != null) ? review.getReviewText().trim() : "";
                        pstmtReview.setString(4, reviewText);
                        pstmtReview.executeUpdate();
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public void addReview(Review review) throws SQLException, EmptyFieldException {
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new EmptyFieldException("Rating must be between 1 and 5.");
        }

        String sql = "INSERT INTO Reviews (movie_id, user_id, rating, review_text) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, review.getMovieId());
            pstmt.setInt(2, review.getUserId());
            pstmt.setInt(3, review.getRating());
            String reviewText = (review.getReviewText() != null) ? review.getReviewText().trim() : "";
            pstmt.setString(4, reviewText);

            pstmt.executeUpdate();
        }

        updateMovieAverageRating(review.getMovieId());
    }

    @Override
    public List<Review> getReviewsForMovie(int movieId) throws SQLException {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.review_id, r.movie_id, r.user_id, r.rating, r.review_text, u.username " +
                     "FROM Reviews r " +
                     "JOIN Users u ON r.user_id = u.user_id " +
                     "WHERE r.movie_id = ? " +
                     "ORDER BY r.review_id DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, movieId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Review review = new Review(
                            rs.getInt("review_id"),
                            rs.getInt("movie_id"),
                            rs.getInt("user_id"),
                            rs.getInt("rating"),
                            rs.getString("review_text"),
                            rs.getString("username")
                    );
                    reviews.add(review);
                }
            }
        }
        return reviews;
    }

    @Override
    public void updateMovieAverageRating(int movieId) throws SQLException {
        String calcSql = "SELECT AVG(rating) AS avg_score FROM Reviews WHERE movie_id = ?";
        double avgScore = 0.0;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(calcSql)) {

            pstmt.setInt(1, movieId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    avgScore = rs.getDouble("avg_score");
                }
            }
        }

        double roundedAvg = Math.round(avgScore * 10.0) / 10.0;

        String updateSql = "UPDATE Movies SET average_rating = ? WHERE movie_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {

            pstmt.setDouble(1, roundedAvg);
            pstmt.setInt(2, movieId);
            pstmt.executeUpdate();
        }
    }
}

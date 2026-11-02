# Cinematica

A desktop Movie Rating and Review System built with Java Swing and SQLite for university Object-Oriented Programming (OOP) coursework.

## Requirements

- Java Development Kit (JDK 17 or higher)
- Windows, macOS, or Linux

## How to Run

### Using Batch Files (Windows)
1. Run `compile.bat` to compile the source code into the `bin/` directory.
2. Run `run.bat` to launch the application.

### Using Command Line
```bash
# Compile all source files
javac -cp "lib/*" -d bin src/com/movierating/exceptions/*.java src/com/movierating/models/*.java src/com/movierating/database/*.java src/com/movierating/ui/*.java src/com/movierating/*.java

# Run the application
java -cp "bin;lib/*" com.movierating.Main
```

## Pre-seeded Accounts

- Admin: username `admin`, password `admin123`
- Viewer: username `alice`, password `alice123`
- Viewer: username `bob`, password `bob123`

## OOP Concepts

- Encapsulation: Model classes (`BaseUser`, `Movie`, `Review`) use private fields with public getter and setter methods.
- Inheritance: `AdminUser` and `ViewerUser` inherit from abstract class `BaseUser`. Custom `EmptyFieldException` inherits from `Exception`.
- Polymorphism: Overloaded search methods (`searchMovies`) by title or genre. Overridden methods (`getDashboardGreeting`, `canManageUsers`) for user roles.
- Abstraction: `DatabaseOperations` interface hides database logic from the UI. `BaseUser` abstract class defines the user template.
- Exception Handling: Checked exception `EmptyFieldException` validates user input, alongside standard `SQLException` handling with resource cleanup.

## Database Schema (SQLite)

The local database file `cinematica.db` contains three tables:

1. Users: `user_id` (PK), `username`, `password`, `role` (Admin/Viewer)
2. Movies: `movie_id` (PK), `user_id` (FK), `title`, `genre`, `description`, `average_rating`, `rewatchable`
3. Reviews: `review_id` (PK), `movie_id` (FK), `user_id` (FK), `rating`, `review_text`

## Application Features

- User Authentication: Login and registration with role assignment (Viewer or Admin).
- Personal Collection: Movies added by a user are stored in their personal collection.
- Single-Step Movie Addition: Enter title, genre, rating (1-5), rewatchable flag, optional review, and optional description in one dialog.
- Search and Filter: Search collection by title keyword or filter by genre.
- Movie Details: View movie information, rewatchable status, and list of reviews.
- Admin Console: Administrators can view all registered users, toggle user roles, and delete accounts.

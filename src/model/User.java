package model;

import java.util.Objects;

/**
 * Represents a user in the intelliTask system.
 * While intelliTask is initially CLI-based and single-user, defining a User model
 * now allows for future expansion into multi-user environments without major refactoring.
 * This class is a POJO and primarily holds user data.
 */
public class User {
    private String id;           // Unique identifier for the user
    private String username;     // The user's chosen username
    private String email;        // The user's email address (optional, but good for future auth)
    // In a real application, you'd never store passwords directly like this.
    // They would be hashed and salted. For this CLI app, we'll keep it simple or remove if not needed.
    // private String passwordHash; // For future authentication scenarios

    /**
     * Constructor to create a new User instance.
     *
     * @param id The unique ID for the user.
     * @param username The username of the user.
     * @param email The email address of the user.
     */
    public User(String id, String username, String email) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        // Email validation could be added here for a more robust application
        this.id = id;
        this.username = username;
        this.email = email;
    }

    /**
     * Retrieves the unique identifier of the user.
     * @return The user's ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the username of the user.
     * @return The user's username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     * @param username The new username.
     */
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        this.username = username;
    }

    /**
     * Retrieves the email address of the user.
     * @return The user's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     * @param email The new email address.
     */
    public void setEmail(String email) {
        // Email validation could be added here
        this.email = email;
    }

    /**
     * Generates a string representation of the User object.
     * @return A string showing the user's ID, username, and email.
     */
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    /**
     * Compares this User object to another object for equality.
     * Two users are considered equal if their IDs are the same.
     * @param o The object to compare with.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    /**
     * Returns a hash code value for the object based on the user's ID.
     * @return A hash code based on the user's ID.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

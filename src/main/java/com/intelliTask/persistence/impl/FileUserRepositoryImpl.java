// src/main/java/com/intelliTask/persistence/impl/FileUserRepositoryImpl.java

package persistence.impl;

import model.User;
import persistence.UserRepository;

import java.util.ArrayList;
import java.util.Collections; // For unmodifiableMap
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors; // Useful for future queries like findByUsername

/**
 * File-based implementation of the UserRepository interface.
 * This class stores User objects in an in-memory HashMap for quick access,
 * and will eventually handle saving/loading these users to/from a file.
 *
 * It adheres to the Repository pattern and the Dependency Inversion Principle (DIP)
 * by implementing the UserRepository interface.
 * Designed with future multi-user capabilities in mind, even if initially single-user.
 */
public class FileUserRepositoryImpl implements UserRepository {

    // A HashMap to store users in memory. Key: userId, Value: User object.
    // This acts as our "in-memory database" for this phase.
    private final Map<String, User> users = new HashMap<>();

    /**
     * Constructor for FileUserRepositoryImpl.
     * In a real application, this might load initial data from a file.
     */
    public FileUserRepositoryImpl() {
        // For now, it's empty. Later, we might add logic here to
        // load existing users from a file upon instantiation.
        // For simplicity, we assume users are loaded via a central DataStorageManager
        // which will then populate these repositories.
    }

    /**
     * Saves a User entity to the persistence layer.
     * If the user already exists (based on their ID), they will be updated.
     * Otherwise, they will be added as a new user.
     *
     * @param user The User object to save.
     * @return The saved or updated User object.
     * @throws IllegalArgumentException if the user or their ID is null.
     */
    @Override
    public User save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        if (user.getId() == null || user.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty.");
        }
        // Using put directly will add a new user if ID doesn't exist,
        // or update an existing user if ID already exists.
        users.put(user.getId(), user);
        // Ponder Point: For user management, especially in multi-user systems,
        // checks for duplicate usernames or emails (if they are unique constraints)
        // would typically happen in the Service layer (UserService) before calling save,
        // or here if the repository is responsible for enforcing such constraints at the storage level.
        return user;
    }

    /**
     * Retrieves a User entity by their unique ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return An Optional containing the User if found, or an empty Optional if not found.
     * @throws IllegalArgumentException if the userId is null or empty.
     */
    @Override
    public Optional<User> findById(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty.");
        }
        // HashMap's get method is efficient for O(1) lookup.
        return Optional.ofNullable(users.get(userId));
    }

    /**
     * Retrieves a User entity by their username. This is useful for lookup
     * and ensuring unique usernames if enforced at the persistence level.
     *
     * @param username The username of the user to retrieve.
     * @return An Optional containing the User if found, or an empty Optional if not found.
     * @throws IllegalArgumentException if the username is null or empty.
     * Ponder Point: This method requires iterating through the map's values,
     * which is O(N) where N is the number of users. If username lookups are frequent
     * and performance-critical, a secondary map (e.g., `Map<String, User> usernameIndex = new HashMap<>();`)
     * could be maintained to map usernames to User objects, offering O(1) lookup.
     * This adds complexity for updates but improves read performance.
     */
    @Override
    public Optional<User> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        // Iterate through values to find user by username
        return users.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst(); // findFirst() because usernames are expected to be unique
    }

    /**
     * Retrieves all User entities from the persistence layer.
     *
     * @return A list of all users. Returns an empty list if no users exist.
     */
    @Override
    public List<User> findAll() {
        // Return a new ArrayList to prevent external modifications to the internal map's values.
        // Ponder Point: As discussed before, returning a defensive copy is crucial.
        // It prevents external code from directly altering the internal state of this repository.
        return new ArrayList<>(users.values());
    }

    /**
     * Deletes a User entity from the persistence layer by their ID.
     *
     * @param userId The ID of the user to delete.
     * @return true if the user was successfully deleted, false otherwise (e.g., user not found).
     * @throws IllegalArgumentException if the userId is null or empty.
     */
    @Override
    public boolean deleteById(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty.");
        }
        // remove returns the value associated with the key, or null if not found.
        // We check if the removed value was non-null to confirm deletion.
        return users.remove(userId) != null;
    }

    // --- Methods for managing the in-memory data for file persistence ---
    // These methods are typically used by a DataStorageManager for loading/saving.

    /**
     * Sets the entire map of users. This is typically used by a data loading
     * mechanism (like DataStorageManager) to populate the repository from storage.
     *
     * @param loadedUsers A map of users to set.
     */
    public void setUsers(Map<String, User> loadedUsers) {
        // Clear existing data and then put all loaded users
        this.users.clear();
        if (loadedUsers != null) {
            this.users.putAll(loadedUsers);
        }
    }

    /**
     * Retrieves the current in-memory map of users. This is typically used by a data saving
     * mechanism (like DataStorageManager) to get all data to persist.
     *
     * @return An unmodifiable map of users to prevent external direct modification.
     * Ponder Point: Returning an unmodifiable map here is crucial for encapsulation and
     * defensive programming. It ensures that the DataStorageManager can read the current state
     * for serialization but cannot inadvertently alter the live, in-memory collection.
     * This separation of concerns prevents data integrity issues.
     */
    public Map<String, User> getUsersMap() {
        // Return an unmodifiable map to protect the internal state
        return Collections.unmodifiableMap(users);
    }
}

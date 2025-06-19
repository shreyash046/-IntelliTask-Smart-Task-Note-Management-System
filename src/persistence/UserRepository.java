package persistence;

import model.User;

import java.util.List;
import java.util.Optional; // For methods that might not return a result

/**
 * Defines the contract for data access operations related to User entities.
 * This interface represents the Repository pattern, abstracting the underlying
 * data storage mechanism (e.g., file system, database).
 * It adheres to the Dependency Inversion Principle (DIP) by providing an
 * abstraction that high-level services (like UserService) can depend on,
 * rather than depending on concrete data access implementations.
 *
 * All methods here reflect persistent storage operations (CRUD - Create, Read, Update, Delete).
 * Designed with future multi-user capabilities in mind.
 */
public interface UserRepository {

    /**
     * Saves a User entity to the persistence layer.
     * If the user already exists (based on their ID), they should be updated.
     * Otherwise, they should be created.
     *
     * @param user The User object to save.
     * @return The saved or updated User object.
     * @throws IllegalArgumentException if the user or their ID is null.
     */
    User save(User user);

    /**
     * Retrieves a User entity by their unique ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return An Optional containing the User if found, or an empty Optional if not found.
     * @throws IllegalArgumentException if the userId is null or empty.
     */
    Optional<User> findById(String userId);

    /**
     * Retrieves a User entity by their username. This is useful for lookup
     * and ensuring unique usernames if enforced at the persistence level.
     *
     * @param username The username of the user to retrieve.
     * @return An Optional containing the User if found, or an empty Optional if not found.
     * @throws IllegalArgumentException if the username is null or empty.
     */
    Optional<User> findByUsername(String username);


    /**
     * Retrieves all User entities from the persistence layer.
     *
     * @return A list of all users. Returns an empty list if no users exist.
     */
    List<User> findAll();

    /**
     * Deletes a User entity from the persistence layer by their ID.
     *
     * @param userId The ID of the user to delete.
     * @return true if the user was successfully deleted, false otherwise (e.g., user not found).
     * @throws IllegalArgumentException if the userId is null or empty.
     */
    boolean deleteById(String userId);

    // Future potential methods for more specific queries, e.g.,
    // Optional<User> findByEmail(String email);
    // boolean existsByUsername(String username);
}

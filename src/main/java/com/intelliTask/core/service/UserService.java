package service;

import model.User;

import java.util.List;
import java.util.Optional;

/**
 * Defines the contract for managing users within the intelliTask system.
 * While intelliTask is initially a single-user CLI application, this interface
 * lays the groundwork for future multi-user capabilities, adhering to DIP.
 *
 * It encapsulates the core business logic related to user creation, retrieval,
 * and updates.
 */
public interface UserService {

    /**
     * Creates and registers a new user in the system.
     * The user ID will be generated internally.
     *
     * @param username The desired username for the new user.
     * @param email The email address for the new user.
     * @return The newly created User object.
     * @throws IllegalArgumentException if the username or email is invalid (e.g., null, empty, or duplicate username/email).
     */
    User createUser(String username, String email);

    /**
     * Retrieves a specific user by their unique ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return An Optional containing the User if found, or an empty Optional if not found.
     */
    Optional<User> getUserById(String userId);

    /**
     * Retrieves a user by their username. Useful for login or unique identification.
     *
     * @param username The username of the user to retrieve.
     * @return An Optional containing the User if found, or an empty Optional if not found.
     * @throws IllegalArgumentException if the username is null or empty.
     */
    Optional<User> getUserByUsername(String username);

    /**
     * Retrieves all users currently in the system.
     * In a single-user context, this will typically return a list with one user.
     *
     * @return A list of all users. Returns an empty list if no users exist.
     */
    List<User> getAllUsers();

    /**
     * Updates the username of an existing user.
     *
     * @param userId The ID of the user to update.
     * @param newUsername The new username for the user.
     * @return The updated User object.
     * @throws IllegalArgumentException if userId or newUsername is invalid.
     * @throws RuntimeException if the user with the given ID is not found. (Placeholder for custom exception)
     */
    User updateUsername(String userId, String newUsername);

    /**
     * Updates the email address of an existing user.
     *
     * @param userId The ID of the user to update.
     * @param newEmail The new email address for the user.
     * @return The updated User object.
     * @throws IllegalArgumentException if userId or newEmail is invalid.
     * @throws RuntimeException if the user with the given ID is not found. (Placeholder for custom exception)
     */
    User updateEmail(String userId, String newEmail);

    /**
     * Deletes a user from the system.
     * Note: In a real multi-user system, this operation would have significant
     * implications for associated tasks, notes, etc., and would require careful
     * consideration of cascading deletions or data re-assignment.
     * For this initial phase, it simply removes the user entry.
     *
     * @param userId The ID of the user to delete.
     * @return true if the user was successfully deleted, false otherwise (e.g., user not found).
     * @throws IllegalArgumentException if userId is invalid.
     */
    boolean deleteUser(String userId);

    // Future methods could include:
    // User changePassword(String userId, String oldPassword, String newPassword); (Requires actual password hashing)
    // List<Task> getUserTasks(String userId); (Or this could be in TaskService, depending on domain boundaries)
}
// src/main/java/com/intelliTask/core/service/impl/UserServiceImpl.java

package service.impl;

import model.User;
import service.UserService;
import persistence.UserRepository;
import util.IdGenerator; // For generating user IDs

import java.util.List;
import java.util.Optional;

/**
 * Concrete implementation of the UserService interface.
 * This class encapsulates the core business logic for managing users.
 * It depends on the UserRepository interface to perform data access operations,
 * adhering to the Dependency Inversion Principle (DIP).
 *
 * Designed with future multi-user capabilities in mind.
 */
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository; // Dependency on the UserRepository interface

    /**
     * Constructor for UserServiceImpl.
     * This demonstrates Manual Dependency Injection: the UserRepository is
     * passed in as a dependency, promoting loose coupling and testability.
     *
     * @param userRepository The repository to use for user data access.
     * @throws IllegalArgumentException if the provided userRepository is null.
     */
    public UserServiceImpl(UserRepository userRepository) {
        if (userRepository == null) {
            throw new IllegalArgumentException("UserRepository cannot be null.");
        }
        this.userRepository = userRepository;
    }

    /**
     * Creates and registers a new user in the system.
     * The user ID will be generated internally.
     * Business logic includes checking for duplicate usernames/emails.
     *
     * @param username The desired username for the new user.
     * @param email The email address for the new user.
     * @return The newly created User object.
     * @throws IllegalArgumentException if the username or email is invalid (e.g., null, empty)
     * or if the username or email already exists.
     * Ponder Point: For production, specific custom exceptions like `DuplicateUsernameException`
     * or `DuplicateEmailException` would provide clearer error handling.
     */
    @Override
    public User createUser(String username, String email) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }

        // Ponder Point: Business logic for uniqueness constraint enforcement.
        // The service layer is the appropriate place to check for business rule violations
        // like duplicate usernames before attempting to save to the repository.
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username '" + username + "' already exists. Please choose a different username.");
        }
        // Optional: Check for duplicate email as well if email is also considered unique.
        // if (userRepository.findByEmail(email).isPresent()) { /* throw exception */ }

        String id = IdGenerator.generateUniqueId();
        User newUser = new User(id, username, email);
        return userRepository.save(newUser); // Delegate persistence
    }

    /**
     * Retrieves a specific user by their unique ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return An Optional containing the User if found, or an empty Optional if not found.
     * @throws IllegalArgumentException if the userId is invalid.
     */
    @Override
    public Optional<User> getUserById(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty.");
        }
        return userRepository.findById(userId); // Delegate retrieval
    }

    /**
     * Retrieves a user by their username. Useful for login or unique identification.
     *
     * @param username The username of the user to retrieve.
     * @return An Optional containing the User if found, or an empty Optional if not found.
     * @throws IllegalArgumentException if the username is null or empty.
     */
    @Override
    public Optional<User> getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        return userRepository.findByUsername(username); // Delegate retrieval
    }

    /**
     * Retrieves all users currently in the system.
     * In a single-user context, this will typically return a list with one user.
     *
     * @return A list of all users. Returns an empty list if no users exist.
     */
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll(); // Delegate retrieval
    }

    /**
     * Updates the username of an existing user.
     *
     * @param userId The ID of the user to update.
     * @param newUsername The new username for the user.
     * @return The updated User object.
     * @throws IllegalArgumentException if userId or newUsername is invalid, or newUsername is a duplicate.
     * @throws RuntimeException if the user with the given ID is not found.
     */
    @Override
    public User updateUsername(String userId, String newUsername) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty.");
        }
        if (newUsername == null || newUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("New username cannot be null or empty.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Ponder Point: Duplicate username check for update.
        // It's crucial to check if the new username is already taken by *another* user.
        // We find by username, and if present AND it's not the current user's ID, then it's a duplicate.
        Optional<User> existingUserWithNewUsername = userRepository.findByUsername(newUsername);
        if (existingUserWithNewUsername.isPresent() && !existingUserWithNewUsername.get().getId().equals(userId)) {
            throw new IllegalArgumentException("Username '" + newUsername + "' is already taken by another user.");
        }

        user.setUsername(newUsername);
        return userRepository.save(user); // Save the updated user
    }

    /**
     * Updates the email address of an existing user.
     *
     * @param userId The ID of the user to update.
     * @param newEmail The new email address for the user.
     * @return The updated User object.
     * @throws IllegalArgumentException if userId or newEmail is invalid, or newEmail is a duplicate.
     * @throws RuntimeException if the user with the given ID is not found.
     */
    @Override
    public User updateEmail(String userId, String newEmail) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty.");
        }
        if (newEmail == null || newEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("New email cannot be null or empty.");
        }
        // Basic email format validation could be added here (regex etc.)

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Ponder Point: Similar duplicate check for email.
        // This requires a `findByEmail` method in `UserRepository` which we didn't add but could.
        // For now, we'll assume email uniqueness is less strictly enforced or handled elsewhere.
        // if (userRepository.findByEmail(newEmail).isPresent() && !existingUserWithNewEmail.get().getId().equals(userId)) {
        //     throw new IllegalArgumentException("Email '" + newEmail + "' is already taken by another user.");
        // }

        user.setEmail(newEmail);
        return userRepository.save(user); // Save the updated user
    }

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
     * Ponder Point: Cascading deletion for users is complex business logic.
     * If a user is deleted, what happens to their tasks, notes, projects, reminders, labels?
     * Options:
     * 1. Delete all associated data (cascading delete).
     * 2. Reassign data to a "default" user.
     * 3. Soft delete (mark user as inactive).
     * This logic would coordinate with other services (e.g., `taskService.deleteAllTasksForUser(userId)`).
     * For now, we only delete the user entity itself.
     */
    @Override
    public boolean deleteUser(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty.");
        }
        // Ponder Point: Add cascading deletion logic here for associated data
        // For example:
        // List<Task> userTasks = taskService.getTasksForUser(userId); // Need to add this method to TaskService
        // userTasks.forEach(task -> taskService.deleteTask(task.getId()));
        // Similar for notes, projects, reminders, labels created by this user.
        return userRepository.deleteById(userId); // Delegate deletion of the user itself
    }
}

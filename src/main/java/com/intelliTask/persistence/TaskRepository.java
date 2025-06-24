package persistence;

import model.Task;

import java.util.List;
import java.util.Optional; // For methods that might not return a result

/**
 * Defines the contract for data access operations related to Task entities.
 * This interface represents the Repository pattern, abstracting the underlying
 * data storage mechanism (e.g., file system, database).
 * It adheres to the Dependency Inversion Principle (DIP) by providing an
 * abstraction that high-level services (like TaskService) can depend on,
 * rather than depending on concrete data access implementations.
 *
 * All methods here reflect persistent storage operations (CRUD - Create, Read, Update, Delete).
 */
public interface TaskRepository {

    /**
     * Saves a Task entity to the persistence layer.
     * If the task already exists (based on its ID), it should be updated.
     * Otherwise, it should be created.
     *
     * @param task The Task object to save.
     * @return The saved or updated Task object.
     * @throws IllegalArgumentException if the task or its ID is null.
     */
    Task save(Task task);

    /**
     * Retrieves a Task entity by its unique ID.
     *
     * @param taskId The ID of the task to retrieve.
     * @return An Optional containing the Task if found, or an empty Optional if not found.
     * @throws IllegalArgumentException if the taskId is null or empty.
     */
    Optional<Task> findById(String taskId);

    /**
     * Retrieves all Task entities from the persistence layer.
     *
     * @return A list of all tasks. Returns an empty list if no tasks exist.
     */
    List<Task> findAll();

    /**
     * Deletes a Task entity from the persistence layer by its ID.
     *
     * @param taskId The ID of the task to delete.
     * @return true if the task was successfully deleted, false otherwise (e.g., task not found).
     * @throws IllegalArgumentException if the taskId is null or empty.
     */
    boolean deleteById(String taskId);

    // Future potential methods for more specific queries
    // List<Task> findByPriority(Priority priority);
    // List<Task> findByStatus(Status status);
    // List<Task> findByProjectId(String projectId);
}


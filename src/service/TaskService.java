package service;

import model.Task;
import model.enums.Priority;
import model.enums.Status;

import java.util.List;
import java.util.Optional; // For methods that might not return a result

/**
 * Defines the contract for managing tasks within the intelliTask system.
 * This interface represents the core business logic related to tasks.
 * It adheres to the Dependency Inversion Principle (DIP), allowing the
 * high-level TaskService to depend on abstractions, not concrete implementations
 * of data access.
 *
 * All methods here reflect business operations, not low-level data storage details.
 */
public interface TaskService {

    /**
     * Creates and adds a new task to the system.
     * The task ID will be generated internally.
     *
     * @param description The description of the new task.
     * @param priority The priority level of the task.
     * @return The newly created Task object.
     * @throws IllegalArgumentException if the description is invalid or essential parameters are null.
     */
    Task createTask(String description, Priority priority);

    /**
     * Creates and adds a new task to the system with default priority (NONE) and status (PENDING).
     *
     * @param description The description of the new task.
     * @return The newly created Task object.
     * @throws IllegalArgumentException if the description is invalid.
     */
    Task createTask(String description);

    /**
     * Retrieves a specific task by its unique ID.
     *
     * @param taskId The ID of the task to retrieve.
     * @return An Optional containing the Task if found, or an empty Optional if not found.
     */
    Optional<Task> getTaskById(String taskId);

    /**
     * Retrieves all tasks currently in the system.
     *
     * @return A list of all tasks. Returns an empty list if no tasks exist.
     */
    List<Task> getAllTasks();

    /**
     * Retrieves tasks filtered by their priority.
     *
     * @param priority The priority level to filter by.
     * @return A list of tasks matching the specified priority. Returns an empty list if none found.
     * @throws IllegalArgumentException if the priority is null.
     */
    List<Task> getTasksByPriority(Priority priority);

    /**
     * Retrieves tasks filtered by their current status.
     *
     * @param status The status to filter by.
     * @return A list of tasks matching the specified status. Returns an empty list if none found.
     * @throws IllegalArgumentException if the status is null.
     */
    List<Task> getTasksByStatus(Status status);

    /**
     * Updates the description of an existing task.
     *
     * @param taskId The ID of the task to update.
     * @param newDescription The new description for the task.
     * @return The updated Task object.
     * @throws IllegalArgumentException if taskId or newDescription is invalid.
     * @throws RuntimeException if the task with the given ID is not found. (Placeholder for custom exception)
     */
    Task updateTaskDescription(String taskId, String newDescription);

    /**
     * Updates the priority of an existing task.
     *
     * @param taskId The ID of the task to update.
     * @param newPriority The new priority for the task.
     * @return The updated Task object.
     * @throws IllegalArgumentException if taskId or newPriority is invalid.
     * @throws RuntimeException if the task with the given ID is not found. (Placeholder for custom exception)
     */
    Task updateTaskPriority(String taskId, Priority newPriority);

    /**
     * Updates the status of an existing task. This method also handles updating
     * the 'isCompleted' flag within the Task model for consistency.
     *
     * @param taskId The ID of the task to update.
     * @param newStatus The new status for the task.
     * @return The updated Task object.
     * @throws IllegalArgumentException if taskId or newStatus is invalid.
     * @throws RuntimeException if the task with the given ID is not found. (Placeholder for custom exception)
     */
    Task updateTaskStatus(String taskId, Status newStatus);

    /**
     * Marks an existing task as completed or uncompleted.
     * This method also handles updating the 'status' enum within the Task model for consistency.
     *
     * @param taskId The ID of the task to mark.
     * @param completed true to mark as completed, false to mark as uncompleted.
     * @return The updated Task object.
     * @throws IllegalArgumentException if taskId is invalid.
     * @throws RuntimeException if the task with the given ID is not found. (Placeholder for custom exception)
     */
    Task markTaskCompleted(String taskId, boolean completed);


    /**
     * Deletes a task from the system.
     *
     * @param taskId The ID of the task to delete.
     * @return true if the task was successfully deleted, false otherwise (e.g., task not found).
     * @throws IllegalArgumentException if taskId is invalid.
     */
    boolean deleteTask(String taskId);
}

package service;

import model.Project;
import model.Task; // For methods involving tasks within a project
import model.enums.Status;

import java.util.List;
import java.util.Optional;

/**
 * Defines the contract for managing projects within the intelliTask system.
 * This interface encapsulates the core business logic related to projects.
 * It adheres to the Dependency Inversion Principle (DIP), allowing the
 * high-level ProjectService to depend on abstractions, not concrete implementations
 * of data access.
 *
 * All methods here reflect business operations, not low-level data storage details.
 */
public interface ProjectService {

    /**
     * Creates and adds a new project to the system with a default PENDING status.
     * The project ID will be generated internally, and timestamps set.
     *
     * @param name The name of the new project.
     * @param description A description of the new project.
     * @return The newly created Project object.
     * @throws IllegalArgumentException if the name is invalid or essential parameters are null.
     */
    Project createProject(String name, String description);

    /**
     * Retrieves a specific project by its unique ID.
     *
     * @param projectId The ID of the project to retrieve.
     * @return An Optional containing the Project if found, or an empty Optional if not found.
     */
    Optional<Project> getProjectById(String projectId);

    /**
     * Retrieves all projects currently in the system.
     *
     * @return A list of all projects. Returns an empty list if no projects exist.
     */
    List<Project> getAllProjects();

    /**
     * Updates the name of an existing project.
     *
     * @param projectId The ID of the project to update.
     * @param newName The new name for the project.
     * @return The updated Project object.
     * @throws IllegalArgumentException if projectId or newName is invalid.
     * @throws RuntimeException if the project with the given ID is not found. (Placeholder for custom exception)
     */
    Project updateProjectName(String projectId, String newName);

    /**
     * Updates the description of an existing project.
     *
     * @param projectId The ID of the project to update.
     * @param newDescription The new description for the project.
     * @return The updated Project object.
     * @throws IllegalArgumentException if projectId is invalid.
     * @throws RuntimeException if the project with the given ID is not found. (Placeholder for custom exception)
     */
    Project updateProjectDescription(String projectId, String newDescription);

    /**
     * Updates the status of an existing project.
     *
     * @param projectId The ID of the project to update.
     * @param newStatus The new status for the project.
     * @return The updated Project object.
     * @throws IllegalArgumentException if projectId or newStatus is invalid.
     * @throws RuntimeException if the project with the given ID is not found. (Placeholder for custom exception)
     */
    Project updateProjectStatus(String projectId, Status newStatus);

    /**
     * Adds an existing task to a project.
     * This operation updates the project's internal list of associated task IDs.
     * Note: The Task itself needs to exist independently and might be managed by TaskService.
     *
     * @param projectId The ID of the project to add the task to.
     * @param taskId The ID of the task to associate with the project.
     * @return The updated Project object.
     * @throws IllegalArgumentException if projectId or taskId is invalid.
     * @throws RuntimeException if the project or task with the given ID is not found. (Placeholder for custom exception)
     */
    Project addTaskToProject(String projectId, String taskId);

    /**
     * Removes a task from a project.
     * This operation updates the project's internal list of associated task IDs.
     *
     * @param projectId The ID of the project to remove the task from.
     * @param taskId The ID of the task to disassociate from the project.
     * @return The updated Project object.
     * @throws IllegalArgumentException if projectId or taskId is invalid.
     * @throws RuntimeException if the project with the given ID is not found. (Placeholder for custom exception)
     */
    Project removeTaskFromProject(String projectId, String taskId);

    /**
     * Retrieves all tasks associated with a specific project.
     * This method would typically involve fetching tasks using the TaskService/Repository
     * based on the task IDs stored in the Project model.
     *
     * @param projectId The ID of the project whose tasks are to be retrieved.
     * @return A list of Task objects associated with the project. Returns an empty list if none found.
     * @throws IllegalArgumentException if projectId is invalid.
     * @throws RuntimeException if the project with the given ID is not found. (Placeholder for custom exception)
     */
    List<Task> getTasksInProject(String projectId);


    /**
     * Deletes a project from the system.
     * Note: This operation typically does not delete associated tasks, only the project itself.
     * Task deletion would be handled by TaskService.
     *
     * @param projectId The ID of the project to delete.
     * @return true if the project was successfully deleted, false otherwise (e.g., project not found).
     * @throws IllegalArgumentException if projectId is invalid.
     */
    boolean deleteProject(String projectId);
}

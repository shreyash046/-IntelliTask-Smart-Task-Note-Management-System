// src/main/java/com/intelliTask/core/service/impl/ProjectServiceImpl.java

package service.impl;

import model.Project;
import model.Task; // Will be used when fetching tasks within a project
import model.enums.Status;
import service.ProjectService;
import service.TaskService; // Dependency for managing tasks within projects
import persistence.ProjectRepository;
import util.IdGenerator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Concrete implementation of the ProjectService interface.
 * This class encapsulates the core business logic for managing projects.
 * It depends on the ProjectRepository interface for data access and
 * the TaskService interface for managing tasks associated with projects,
 * adhering to the Dependency Inversion Principle (DIP).
 */
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository; // Dependency for project data
    private final TaskService taskService;             // Dependency for task-related operations

    /**
     * Constructor for ProjectServiceImpl.
     * This demonstrates Manual Dependency Injection: both ProjectRepository and TaskService
     * are passed in as dependencies, promoting loose coupling and testability.
     *
     * @param projectRepository The repository to use for project data access.
     * @param taskService The service to use for task-related operations.
     * @throws IllegalArgumentException if either provided repository or service is null.
     */
    public ProjectServiceImpl(ProjectRepository projectRepository, TaskService taskService) {
        if (projectRepository == null) {
            throw new IllegalArgumentException("ProjectRepository cannot be null.");
        }
        if (taskService == null) {
            throw new IllegalArgumentException("TaskService cannot be null.");
        }
        this.projectRepository = projectRepository;
        this.taskService = taskService;
    }

    /**
     * Creates and adds a new project to the system with a default PENDING status.
     * The project ID will be generated internally.
     *
     * @param name The name of the new project.
     * @param description A description of the new project.
     * @return The newly created Project object.
     * @throws IllegalArgumentException if the name is invalid or essential parameters are null.
     */
    @Override
    public Project createProject(String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty.");
        }
        // Description can be empty, so no specific check beyond null in model.

        String id = IdGenerator.generateUniqueId();
        // Ponder Point: The service decides the default status (PENDING) for a new project.
        // This is a business rule handled at the service layer.
        Project newProject = new Project(id, name, description, Status.PENDING);
        return projectRepository.save(newProject); // Delegate persistence
    }

    /**
     * Retrieves a specific project by its unique ID.
     *
     * @param projectId The ID of the project to retrieve.
     * @return An Optional containing the Project if found, or an empty Optional if not found.
     * @throws IllegalArgumentException if the projectId is invalid.
     */
    @Override
    public Optional<Project> getProjectById(String projectId) {
        if (projectId == null || projectId.trim().isEmpty()) {
            throw new IllegalArgumentException("Project ID cannot be null or empty.");
        }
        return projectRepository.findById(projectId); // Delegate retrieval
    }

    /**
     * Retrieves all projects currently in the system.
     *
     * @return A list of all projects. Returns an empty list if no projects exist.
     */
    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll(); // Delegate retrieval
    }

    /**
     * Updates the name of an existing project.
     *
     * @param projectId The ID of the project to update.
     * @param newName The new name for the project.
     * @return The updated Project object.
     * @throws IllegalArgumentException if projectId or newName is invalid.
     * @throws RuntimeException if the project with the given ID is not found.
     * Ponder Point: For production, consider a custom exception like ProjectNotFoundException.
     */
    @Override
    public Project updateProjectName(String projectId, String newName) {
        if (projectId == null || projectId.trim().isEmpty()) {
            throw new IllegalArgumentException("Project ID cannot be null or empty.");
        }
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("New project name cannot be null or empty.");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        project.setName(newName); // Model's setter updates lastModifiedAt
        return projectRepository.save(project);
    }

    /**
     * Updates the description of an existing project.
     *
     * @param projectId The ID of the project to update.
     * @param newDescription The new description for the project.
     * @return The updated Project object.
     * @throws IllegalArgumentException if projectId is invalid.
     * @throws RuntimeException if the project with the given ID is not found.
     */
    @Override
    public Project updateProjectDescription(String projectId, String newDescription) {
        if (projectId == null || projectId.trim().isEmpty()) {
            throw new IllegalArgumentException("Project ID cannot be null or empty.");
        }
        // newDescription can be empty, but not null, model handles null by defaulting.

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        project.setDescription(newDescription); // Model's setter updates lastModifiedAt
        return projectRepository.save(project);
    }

    /**
     * Updates the status of an existing project.
     *
     * @param projectId The ID of the project to update.
     * @param newStatus The new status for the project.
     * @return The updated Project object.
     * @throws IllegalArgumentException if projectId or newStatus is invalid.
     * @throws RuntimeException if the project with the given ID is not found.
     */
    @Override
    public Project updateProjectStatus(String projectId, Status newStatus) {
        if (projectId == null || projectId.trim().isEmpty()) {
            throw new IllegalArgumentException("Project ID cannot be null or empty.");
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("New status cannot be null.");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        // Ponder Point: More complex business rules regarding status transitions
        // (e.g., cannot go from COMPLETED to IN_PROGRESS) would be implemented here.
        // For now, we allow any valid status enum value.
        project.setStatus(newStatus); // Model's setter updates lastModifiedAt
        return projectRepository.save(project);
    }

    /**
     * Adds an existing task to a project.
     * This operation updates the project's internal list of associated task IDs.
     * It also validates if the task itself exists using TaskService.
     *
     * @param projectId The ID of the project to add the task to.
     * @param taskId The ID of the task to associate with the project.
     * @return The updated Project object.
     * @throws IllegalArgumentException if any ID is invalid.
     * @throws RuntimeException if the project or task with the given ID is not found.
     * Ponder Point: Consider specific exceptions like ProjectNotFoundException, TaskNotFoundException.
     */
    @Override
    public Project addTaskToProject(String projectId, String taskId) {
        if (projectId == null || projectId.trim().isEmpty()) {
            throw new IllegalArgumentException("Project ID cannot be null or empty.");
        }
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("Task ID cannot be null or empty.");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        // Ponder Point: Service orchestration in action. ProjectService depends on TaskService
        // to ensure the task actually exists before linking it. This maintains data integrity.
        Optional<Task> taskOptional = taskService.getTaskById(taskId);
        if (taskOptional.isEmpty()) {
            throw new RuntimeException("Task not found with ID: " + taskId + ". Cannot add to project.");
        }

        project.addTask(taskId); // Add task ID to project model's list
        return projectRepository.save(project); // Save the updated project
    }

    /**
     * Removes a task from a project.
     * This operation updates the project's internal list of associated task IDs.
     *
     * @param projectId The ID of the project to remove the task from.
     * @param taskId The ID of the task to disassociate from the project.
     * @return The updated Project object.
     * @throws IllegalArgumentException if any ID is invalid.
     * @throws RuntimeException if the project with the given ID is not found.
     */
    @Override
    public Project removeTaskFromProject(String projectId, String taskId) {
        if (projectId == null || projectId.trim().isEmpty()) {
            throw new IllegalArgumentException("Project ID cannot be null or empty.");
        }
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("Task ID cannot be null or empty.");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        // Ponder Point: We might check if the task actually exists within the project's list
        // before attempting to remove, although project.removeTask() already handles this by returning false if not found.
        if (!project.removeTask(taskId)) {
            // Optional: throw a specific exception if the task was not associated
            throw new RuntimeException("Task with ID: " + taskId + " was not found in project: " + projectId);
        }

        return projectRepository.save(project);
    }

    /**
     * Retrieves all tasks associated with a specific project.
     * This method orchestrates fetching tasks using the TaskService/Repository
     * based on the task IDs stored in the Project model.
     *
     * @param projectId The ID of the project whose tasks are to be retrieved.
     * @return A list of Task objects associated with the project. Returns an empty list if none found.
     * @throws IllegalArgumentException if projectId is invalid.
     * @throws RuntimeException if the project with the given ID is not found.
     */
    @Override
    public List<Task> getTasksInProject(String projectId) {
        if (projectId == null || projectId.trim().isEmpty()) {
            throw new IllegalArgumentException("Project ID cannot be null or empty.");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        // Ponder Point: This is a classic example of "Lazy Loading" in a simple context.
        // We only fetch the actual Task objects when they are explicitly requested,
        // rather than loading them all when the Project itself is loaded.
        // We iterate through the list of Task IDs in the project and use the TaskService
        // to retrieve each Task object.
        return project.getTaskIds().stream()
                .map(taskService::getTaskById) // Map each ID to an Optional<Task>
                .filter(Optional::isPresent)   // Filter out any tasks that might not be found (e.g., deleted tasks)
                .map(Optional::get)            // Get the Task object from the Optional
                .collect(Collectors.toList()); // Collect into a List
    }

    /**
     * Deletes a project from the system.
     * Note: This operation typically does not delete associated tasks, only the project itself.
     * Task deletion would be handled by TaskService.
     *
     * @param projectId The ID of the project to delete.
     * @return true if the project was successfully deleted, false otherwise (e.g., project not found).
     * @throws IllegalArgumentException if projectId is invalid.
     */
    @Override
    public boolean deleteProject(String projectId) {
        if (projectId == null || projectId.trim().isEmpty()) {
            throw new IllegalArgumentException("Project ID cannot be null or empty.");
        }
        // Ponder Point: Cascading delete decisions are critical for SDE 2.
        // If deleting a project should also delete its tasks, notes, etc.,
        // that orchestration would happen here, coordinating with other services.
        // For example:
        // Project projectToDelete = projectRepository.findById(projectId)
        //     .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));
        // // Delete associated tasks first
        // projectToDelete.getTaskIds().forEach(taskService::deleteTask);
        // Then delete the project itself.
        return projectRepository.deleteById(projectId); // Delegate deletion
    }
}

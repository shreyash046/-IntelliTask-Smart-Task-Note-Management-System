
package service.impl;

import model.Task;
import model.enums.Priority;
import model.enums.Status;
import service.TaskService;
import persistence.TaskRepository;
import util.IdGenerator; // We'll use this for ID generation

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // For filtering operations

/**
 * Concrete implementation of the TaskService interface.
 * This class encapsulates the core business logic for managing tasks.
 * It depends on the TaskRepository interface to perform data access operations,
 * adhering to the Dependency Inversion Principle (DIP).
 */
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository; // Dependency on the TaskRepository interface

    /**
     * Constructor for TaskServiceImpl.
     * This demonstrates Manual Dependency Injection: the TaskRepository is
     * passed in as a dependency, rather than being created internally.
     *
     * @param taskRepository The repository to use for task data access.
     * @throws IllegalArgumentException if the provided taskRepository is null.
     */
    public TaskServiceImpl(TaskRepository taskRepository) {
        if (taskRepository == null) {
            throw new IllegalArgumentException("TaskRepository cannot be null.");
        }
        this.taskRepository = taskRepository;
    }

    /**
     * Creates and adds a new task to the system.
     * The task ID will be generated internally using IdGenerator.
     * This method acts as a Factory Method for Task creation within the service layer.
     *
     * @param description The description of the new task.
     * @param priority The priority level of the task.
     * @return The newly created Task object.
     * @throws IllegalArgumentException if the description is invalid or essential parameters are null.
     */
    @Override
    public Task createTask(String description, Priority priority) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Task description cannot be empty.");
        }
        if (priority == null) {
            throw new IllegalArgumentException("Task priority cannot be null.");
        }

        String id = IdGenerator.generateUniqueId(); // Using our centralized ID generator
        // Ponder Point: The service decides default status (PENDING) and completion (false).
        // This is business logic, so it resides here, not just in the model constructor.
        Task newTask = new Task(id, description, false, priority, Status.PENDING);
        return taskRepository.save(newTask); // Delegate persistence to the repository
    }

    /**
     * Creates and adds a new task to the system with default priority (NONE) and status (PENDING).
     *
     * @param description The description of the new task.
     * @return The newly created Task object.
     * @throws IllegalArgumentException if the description is invalid.
     */
    @Override
    public Task createTask(String description) {
        return createTask(description, Priority.NONE); // Delegate to the more specific createTask method
    }

    /**
     * Retrieves a specific task by its unique ID.
     *
     * @param taskId The ID of the task to retrieve.
     * @return An Optional containing the Task if found, or an empty Optional if not found.
     * @throws IllegalArgumentException if the taskId is invalid.
     */
    @Override
    public Optional<Task> getTaskById(String taskId) {
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("Task ID cannot be null or empty.");
        }
        return taskRepository.findById(taskId); // Delegate retrieval to the repository
    }

    /**
     * Retrieves all tasks currently in the system.
     *
     * @return A list of all tasks. Returns an empty list if no tasks exist.
     */
    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll(); // Delegate to the repository
    }

    /**
     * Retrieves tasks filtered by their priority.
     *
     * @param priority The priority level to filter by.
     * @return A list of tasks matching the specified priority. Returns an empty list if none found.
     * @throws IllegalArgumentException if the priority is null.
     */
    @Override
    public List<Task> getTasksByPriority(Priority priority) {
        if (priority == null) {
            throw new IllegalArgumentException("Priority cannot be null.");
        }
        // Ponder Point: Filtering logic resides in the service. For simple filtering,
        // using streams is efficient. For complex, performance-critical filters on large datasets,
        // this logic might be pushed down to the repository (e.g., findByPriority in DB).
        return taskRepository.findAll().stream()
                .filter(task -> task.getPriority() == priority)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves tasks filtered by their current status.
     *
     * @param status The status to filter by.
     * @return A list of tasks matching the specified status. Returns an empty list if none found.
     * @throws IllegalArgumentException if the status is null.
     */
    @Override
    public List<Task> getTasksByStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null.");
        }
        // Ponder Point: Similar to priority, filtering by status is business logic here.
        return taskRepository.findAll().stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Updates the description of an existing task.
     *
     * @param taskId The ID of the task to update.
     * @param newDescription The new description for the task.
     * @return The updated Task object.
     * @throws IllegalArgumentException if taskId or newDescription is invalid.
     * @throws RuntimeException if the task with the given ID is not found.
     * Ponder Point: For production, consider a custom exception like TaskNotFoundException.
     */
    @Override
    public Task updateTaskDescription(String taskId, String newDescription) {
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("Task ID cannot be null or empty.");
        }
        if (newDescription == null || newDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("New description cannot be null or empty.");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId)); // Safe retrieval with Optional

        // Apply the update directly to the retrieved Task object
        task.setDescription(newDescription); // Model's setter updates its lastModifiedAt implicitly

        return taskRepository.save(task); // Save the updated task
    }

    /**
     * Updates the priority of an existing task.
     *
     * @param taskId The ID of the task to update.
     * @param newPriority The new priority for the task.
     * @return The updated Task object.
     * @throws IllegalArgumentException if taskId or newPriority is invalid.
     * @throws RuntimeException if the task with the given ID is not found.
     */
    @Override
    public Task updateTaskPriority(String taskId, Priority newPriority) {
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("Task ID cannot be null or empty.");
        }
        if (newPriority == null) {
            throw new IllegalArgumentException("New priority cannot be null.");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        task.setPriority(newPriority); // Model's setter handles its update
        return taskRepository.save(task);
    }

    /**
     * Updates the status of an existing task. This method also handles updating
     * the 'isCompleted' flag within the Task model for consistency.
     *
     * @param taskId The ID of the task to update.
     * @param newStatus The new status for the task.
     * @return The updated Task object.
     * @throws IllegalArgumentException if taskId or newStatus is invalid.
     * @throws RuntimeException if the task with the given ID is not found.
     */
    @Override
    public Task updateTaskStatus(String taskId, Status newStatus) {
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("Task ID cannot be null or empty.");
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("New status cannot be null.");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        // Ponder Point: Business logic for status transitions could be complex.
        // For example, preventing status from going directly from COMPLETED to PENDING.
        // This is the ideal place to implement such rules.
        // For now, the Task model handles simple consistency between status and isCompleted.
        task.setStatus(newStatus); // Model's setter handles its update and isCompleted flag
        return taskRepository.save(task);
    }

    /**
     * Marks an existing task as completed or uncompleted.
     * This method also handles updating the 'status' enum within the Task model for consistency.
     *
     * @param taskId The ID of the task to mark.
     * @param completed true to mark as completed, false to mark as uncompleted.
     * @return The updated Task object.
     * @throws IllegalArgumentException if taskId is invalid.
     * @throws RuntimeException if the task with the given ID is not found.
     */
    @Override
    public Task markTaskCompleted(String taskId, boolean completed) {
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("Task ID cannot be null or empty.");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        // Ponder Point: This is where you might also trigger side effects or events.
        // E.g., if a task is completed, maybe all its sub-tasks are marked complete,
        // or a notification is sent to a project manager.
        task.setCompleted(completed); // Model's setter handles consistency with status enum
        return taskRepository.save(task);
    }

    /**
     * Updates the list of label IDs associated with an existing task.
     * This method is primarily used by the LabelService to manage associations.
     *
     * @param taskId The ID of the task to update.
     * @param newLabelIds The new list of label IDs to associate with the task.
     * @return The updated Task object.
     * @throws IllegalArgumentException if taskId or newLabelIds is invalid.
     * @throws RuntimeException if the task with the given ID is not found.
     */
    @Override
    public Task updateTaskLabels(String taskId, List<String> newLabelIds) {
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("Task ID cannot be null or empty.");
        }
        // newLabelIds can be null/empty if clearing all labels, but we'll check individual elements later if needed.
        if (newLabelIds == null) {
            newLabelIds = new ArrayList<>(); // Treat null as an empty list for setting
        }


        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        // Ponder Point: We use the Task model's `setLabelIds` directly.
        // This method clears existing labels and adds all new ones.
        // If granular add/remove methods were needed, we could call task.addLabel() / task.removeLabel()
        // for each item in the new list compared to the old list.
        task.setLabelIds(newLabelIds);

        return taskRepository.save(task);
    }
    /**
     * Deletes a task from the system.
     *
     * @param taskId The ID of the task to delete.
     * @return true if the task was successfully deleted, false otherwise (e.g., task not found).
     * @throws IllegalArgumentException if taskId is invalid.
     */
    @Override
    public boolean deleteTask(String taskId) {
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("Task ID cannot be null or empty.");
        }
        // Ponder Point: In a real system, deleting a task might have cascading effects:
        // - Remove task from any associated projects.
        // - Delete any reminders associated with this task.
        // This orchestration logic would reside here or be delegated to other services.
        return taskRepository.deleteById(taskId); // Delegate deletion to the repository
    }
}
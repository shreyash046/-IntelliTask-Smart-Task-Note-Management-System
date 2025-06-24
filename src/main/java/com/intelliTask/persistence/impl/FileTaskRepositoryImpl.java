package persistence.impl;

import model.Task;
import persistence.TaskRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * File-based implementation of the TaskRepository interface.
 * This class stores Task objects in an in-memory HashMap for quick access,
 * and will eventually handle saving/loading these tasks to/from a file.
 *
 * It adheres to the Repository pattern and the Dependency Inversion Principle (DIP)
 * by implementing the TaskRepository interface.
 */
public class FileTaskRepositoryImpl implements TaskRepository {

    // A HashMap to store tasks in memory. Key: taskId, Value: Task object.
    // This acts as our "in-memory database" for this phase.
    private final Map<String, Task> tasks = new HashMap<>();

    /**
     * Constructor for FileTaskRepositoryImpl.
     * In a real application, this might load initial data from a file.
     */
    public FileTaskRepositoryImpl() {
        // For now, it's empty. Later, we might add logic here to
        // load existing tasks from a file upon instantiation.
        // For simplicity, we assume tasks are loaded via a central DataStorageManager
        // which will then populate these repositories.
    }

    /**
     * Saves a Task entity to the persistence layer.
     * If the task already exists (based on its ID), it will be updated.
     * Otherwise, it will be added as a new task.
     *
     * @param task The Task object to save.
     * @return The saved or updated Task object.
     * @throws IllegalArgumentException if the task or its ID is null.
     */
    @Override
    public Task save(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }
        if (task.getId() == null || task.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Task ID cannot be null or empty.");
        }
        // Using put directly will add a new task if ID doesn't exist,
        // or update an existing task if ID already exists.
        tasks.put(task.getId(), task);
        // In a file-based system, you would typically trigger a save-to-file operation here
        // or rely on a batch save mechanism by a DataStorageManager.
        return task;
    }

    /**
     * Retrieves a Task entity by its unique ID.
     *
     * @param taskId The ID of the task to retrieve.
     * @return An Optional containing the Task if found, or an empty Optional if not found.
     * @throws IllegalArgumentException if the taskId is null or empty.
     */
    @Override
    public Optional<Task> findById(String taskId) {
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("Task ID cannot be null or empty.");
        }
        // HashMap's get method is efficient for O(1) lookup.
        return Optional.ofNullable(tasks.get(taskId));
    }

    /**
     * Retrieves all Task entities from the persistence layer.
     *
     * @return A list of all tasks. Returns an empty list if no tasks exist.
     */
    @Override
    public List<Task> findAll() {
        // Return a new ArrayList to prevent external modifications to the internal map's values.
        return new ArrayList<>(tasks.values());
    }

    /**
     * Deletes a Task entity from the persistence layer by its ID.
     *
     * @param taskId The ID of the task to delete.
     * @return true if the task was successfully deleted, false otherwise (e.g., task not found).
     * @throws IllegalArgumentException if the taskId is null or empty.
     */
    @Override
    public boolean deleteById(String taskId) {
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("Task ID cannot be null or empty.");
        }
        // remove returns the value associated with the key, or null if not found.
        // We check if the removed value was non-null to confirm deletion.
        return tasks.remove(taskId) != null;
    }

    // --- Methods for managing the in-memory data for file persistence ---
    // These methods are typically used by a DataStorageManager for loading/saving.

    /**
     * Sets the entire map of tasks. This is typically used by a data loading
     * mechanism (like DataStorageManager) to populate the repository from storage.
     *
     * @param loadedTasks A map of tasks to set.
     */
    public void setTasks(Map<String, Task> loadedTasks) {
        // Clear existing data and then put all loaded tasks
        this.tasks.clear();
        if (loadedTasks != null) {
            this.tasks.putAll(loadedTasks);
        }
    }

    /**
     * Retrieves the current in-memory map of tasks. This is typically used by a data saving
     * mechanism (like DataStorageManager) to get all data to persist.
     *
     * @return An unmodifiable map of tasks to prevent external direct modification.
     */
    public Map<String, Task> getTasksMap() {
        // Return an unmodifiable map to protect the internal state
        return java.util.Collections.unmodifiableMap(tasks);
    }
}
package model;

import model.enums.Status; // Import the Status enum
import java.time.LocalDateTime; // For tracking creation and modification timestamps
import java.util.ArrayList;     // For storing associated tasks
import java.util.List;          // For storing associated tasks
import java.util.Objects;       // For Objects.equals and Objects.hash

/**
 * Represents a project in the intelliTask system.
 * A project groups related tasks and can have its own status and description.
 * This class is a Plain Old Java Object (POJO) and serves as a core domain model.
 */
public class Project {
    private String id;           // Unique identifier for the project
    private String name;         // The name of the project
    private String description;  // A detailed description of the project
    private Status status;       // The current status of the project (e.g., PENDING, IN_PROGRESS, COMPLETED)
    private LocalDateTime createdAt; // Timestamp when the project was created
    private LocalDateTime lastModifiedAt; // Timestamp when the project was last modified
    // This list stores the IDs of tasks associated with this project.
    // The actual Task objects will be managed by a TaskService/Repository.
    private List<String> taskIds; // List of task IDs belonging to this project

    /**
     * Constructor to create a new Project instance.
     * The 'createdAt' field is automatically set to the current time upon creation.
     * The 'lastModifiedAt' field is also initially set to 'createdAt'.
     *
     * @param id The unique ID for the project.
     * @param name The name of the project.
     * @param description A description of the project.
     * @param status The initial status of the project.
     */
    public Project(String id, String name, String description, Status status) {
        // Basic validation: ensure ID and name are not null or empty
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Project ID cannot be empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty.");
        }
        // Description can be empty, so no check for that.
        if (status == null) {
            throw new IllegalArgumentException("Project status cannot be null.");
        }

        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.lastModifiedAt = this.createdAt;
        this.taskIds = new ArrayList<>(); // Initialize an empty list for task IDs
    }

    // Overloaded constructor for simpler project creation, defaulting status
    public Project(String id, String name, String description) {
        this(id, name, description, Status.PENDING); // Default to PENDING status
    }


    /**
     * Retrieves the unique identifier of the project.
     * @return The project's ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the name of the project.
     * @return The project's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the project.
     * Updates the 'lastModifiedAt' timestamp.
     * @param name The new name.
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty.");
        }
        this.name = name;
        this.lastModifiedAt = LocalDateTime.now(); // Update modification time
    }

    /**
     * Retrieves the description of the project.
     * @return The project's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the project.
     * Updates the 'lastModifiedAt' timestamp.
     * @param description The new description.
     */
    public void setDescription(String description) {
        if (description == null) {
            this.description = ""; // Default to empty string if null
        } else {
            this.description = description;
        }
        this.lastModifiedAt = LocalDateTime.now(); // Update modification time
    }

    /**
     * Retrieves the status of the project.
     * @return The project's status.
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets the status of the project.
     * Updates the 'lastModifiedAt' timestamp.
     * @param status The new status.
     */
    public void setStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Project status cannot be null.");
        }
        this.status = status;
        this.lastModifiedAt = LocalDateTime.now(); // Update modification time
    }

    /**
     * Retrieves the timestamp when the project was created.
     * @return The creation timestamp.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Retrieves the timestamp when the project was last modified.
     * @return The last modification timestamp.
     */
    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }

    /**
     * Retrieves a list of IDs of tasks associated with this project.
     * @return A copy of the list of task IDs.
     */
    public List<String> getTaskIds() {
        // Return a new ArrayList to prevent external modification of the internal list
        return new ArrayList<>(taskIds);
    }

    /**
     * Adds a task ID to the project's list of associated tasks.
     * Updates the 'lastModifiedAt' timestamp.
     * @param taskId The ID of the task to add.
     */
    public void addTask(String taskId) {
        if (taskId != null && !taskId.trim().isEmpty() && !taskIds.contains(taskId)) {
            this.taskIds.add(taskId);
            this.lastModifiedAt = LocalDateTime.now(); // Update modification time
        }
    }

    /**
     * Removes a task ID from the project's list of associated tasks.
     * Updates the 'lastModifiedAt' timestamp.
     * @param taskId The ID of the task to remove.
     */
    public void removeTask(String taskId) {
        if (taskId != null && taskIds.remove(taskId)) {
            this.lastModifiedAt = LocalDateTime.now(); // Update modification time
        }
    }

    /**
     * Generates a string representation of the Project object.
     * @return A string showing the project's ID, name, status, and the number of associated tasks.
     */
    @Override
    public String toString() {
        return "Project{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", taskCount=" + taskIds.size() +
                '}';
    }

    /**
     * Compares this Project object to another object for equality.
     * Two projects are considered equal if their IDs are the same.
     * @param o The object to compare with.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id);
    }

    /**
     * Returns a hash code value for the object based on the project's ID.
     * @return A hash code based on the project's ID.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

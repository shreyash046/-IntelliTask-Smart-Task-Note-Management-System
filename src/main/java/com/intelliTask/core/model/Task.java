package com.intelliTask.core.model;

import model.enums.Priority; // Import the Priority enum
import model.enums.Status;   // Import the Status enum
import java.util.Objects; // Used for Objects.equals and Objects.hash for good practice
import java.util.List;    // For associated labels
import java.util.ArrayList; // For initializing the list
/**
 * Represents a single task in the intelliTask system.
 * This class is a Plain Old Java Object (POJO) and serves as a core domain model.
 * It primarily holds data related to a task and should not contain complex business logic.
 *
 * UPDATED: Added support for associating labels.
 */
public class Task {
    private String id;          // Unique identifier for the task
    private String description; // A brief description of the task
    private boolean isCompleted; // Status of the task (completed or not)
    private Priority priority;  // The priority level of the task
    private Status status;      // The current status of the task
    private List<String> labelIds; // List of IDs of labels associated with this task

    /**
     * Constructor to create a new Task instance.
     *
     * @param id The unique ID for the task.
     * @param description The textual description of the task.
     * @param isCompleted The initial completion status of the task.
     * @param priority The initial priority level of the task.
     * @param status The initial status of the task.
     */
    public Task(String id, String description, boolean isCompleted, Priority priority, Status status) {
        // Basic validation: ensure description is not null or empty
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Task description cannot be empty.");
        }
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Task ID cannot be empty.");
        }
        // Ensure priority and status are not null
        if (priority == null) {
            throw new IllegalArgumentException("Task priority cannot be null.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Task status cannot be null.");
        }

        this.id = id;
        this.description = description;
        this.isCompleted = isCompleted;
        this.priority = priority;
        this.status = status;
        this.labelIds = new ArrayList<>(); // Initialize an empty list for labels
    }

    // Overloaded constructor for simpler task creation, defaulting priority and status
    public Task(String id, String description, boolean isCompleted) {
        this(id, description, isCompleted, Priority.NONE, Status.PENDING);
    }

    // New constructor to also initialize with labels if provided (e.g., during deserialization)
    public Task(String id, String description, boolean isCompleted, Priority priority, Status status, List<String> labelIds) {
        this(id, description, isCompleted, priority, status); // Call existing constructor
        if (labelIds != null) {
            this.labelIds.addAll(labelIds); // Add all provided labels
        }
    }


    /**
     * Retrieves the unique identifier of the task.
     * @return The task's ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the description of the task.
     * @return The task's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the task.
     * @param description The new description.
     */
    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Task description cannot be empty.");
        }
        this.description = description;
    }

    /**
     * Checks if the task is completed.
     * @return true if the task is completed, false otherwise.
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Sets the completion status of the task.
     * @param completed The new completion status.
     */
    public void setCompleted(boolean completed) {
        isCompleted = completed;
        // Optionally, update status to COMPLETED if isCompleted is true,
        // or PENDING/IN_PROGRESS if false. This logic might better reside in a service.
        if (completed) {
            this.status = Status.COMPLETED;
        } else if (this.status == Status.COMPLETED) {
            // If setting to not completed and was completed, revert to pending
            this.status = Status.PENDING;
        }
    }

    /**
     * Retrieves the priority of the task.
     * @return The task's priority.
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Sets the priority of the task.
     * @param priority The new priority.
     */
    public void setPriority(Priority priority) {
        if (priority == null) {
            throw new IllegalArgumentException("Task priority cannot be null.");
        }
        this.priority = priority;
    }

    /**
     * Retrieves the status of the task.
     * @return The task's status.
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets the status of the task.
     * @param status The new status.
     */
    public void setStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Task status cannot be null.");
        }
        this.status = status;
        // Keep isCompleted consistent with status if desired.
        // For simplicity, we assume setCompleted also updates status,
        // but this setter allows direct status changes.
        if (status == Status.COMPLETED) {
            this.isCompleted = true;
        } else if (status == Status.PENDING || status == Status.IN_PROGRESS || status == Status.CANCELLED) {
            this.isCompleted = false;
        }
    }

    /**
     * Retrieves a copy of the list of label IDs associated with this task.
     * @return A new List containing the label IDs.
     */
    public List<String> getLabelIds() {
        return new ArrayList<>(labelIds); // Return a defensive copy
    }

    /**
     * Sets the list of label IDs for this task.
     * This method is typically used by services or during deserialization.
     * @param labelIds The new list of label IDs.
     */
    public void setLabelIds(List<String> labelIds) {
        this.labelIds.clear();
        if (labelIds != null) {
            // Ensure unique labels if adding directly, or rely on calling service.
            // For now, just add all, and service will manage uniqueness.
            this.labelIds.addAll(labelIds);
        }
    }

    /**
     * Adds a label ID to the task's list of associated labels.
     * @param labelId The ID of the label to add.
     * @return true if the label was added, false if it was already present.
     */
    public boolean addLabel(String labelId) {
        if (labelId == null || labelId.trim().isEmpty()) {
            throw new IllegalArgumentException("Label ID cannot be null or empty.");
        }
        if (!labelIds.contains(labelId)) {
            return labelIds.add(labelId);
        }
        return false;
    }

    /**
     * Removes a label ID from the task's list of associated labels.
     * @param labelId The ID of the label to remove.
     * @return true if the label was removed, false if it was not found.
     */
    public boolean removeLabel(String labelId) {
        if (labelId == null || labelId.trim().isEmpty()) {
            throw new IllegalArgumentException("Label ID cannot be null or empty.");
        }
        return labelIds.remove(labelId);
    }

    /**
     * Generates a string representation of the Task object.
     * This is useful for debugging and logging.
     * @return A string showing the task's ID, description, completion status, priority, and current status.
     */
    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", isCompleted=" + isCompleted +
                ", priority=" + priority +
                ", status=" + status +
                ", labelIds=" + labelIds +
                '}';
    }

    /**
     * Compares this Task object to another object for equality.
     * Two tasks are considered equal if their IDs are the same.
     * This is crucial for uniquely identifying tasks, especially when using collections like Sets.
     * @param o The object to compare with.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id); // Tasks are equal if their IDs are equal
    }

    /**
     * Returns a hash code value for the object.
     * This method is supported for the benefit of hash tables such as those provided by HashMap and HashSet.
     * It's crucial to override hashCode() whenever equals() is overridden to maintain the contract
     * that equal objects must have equal hash codes.
     * @return A hash code based on the task's ID.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id); // Hash code based on the unique ID
    }
}

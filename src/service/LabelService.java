// src/main/java/com/intelliTask/core/service/LabelService.java

package service;

import model.Label;
import model.Task; // For potential methods to get tasks by label
import model.Note; // For potential methods to get notes by label

import java.util.List;
import java.util.Optional;

/**
 * Defines the contract for managing labels within the intelliTask system.
 * This interface encapsulates the core business logic related to creating,
 * retrieving, updating, and deleting labels, as well as associating them
 * with tasks and notes.
 * It adheres to the Dependency Inversion Principle (DIP), allowing the
 * high-level LabelService to depend on abstractions, not concrete implementations
 * of data access.
 *
 * All methods here reflect business operations.
 */
public interface LabelService {

    /**
     * Creates and adds a new label to the system.
     * The label ID will be generated internally.
     *
     * @param name The name of the new label.
     * @return The newly created Label object.
     * @throws IllegalArgumentException if the name is invalid (e.g., null or empty).
     */
    Label createLabel(String name);

    /**
     * Retrieves a specific label by its unique ID.
     *
     * @param labelId The ID of the label to retrieve.
     * @return An Optional containing the Label if found, or an empty Optional if not found.
     */
    Optional<Label> getLabelById(String labelId);

    /**
     * Retrieves a label by its name. This might be useful for quick lookup.
     *
     * @param name The name of the label to retrieve.
     * @return An Optional containing the Label if found, or an empty Optional if not found.
     * @throws IllegalArgumentException if the name is null or empty.
     */
    Optional<Label> getLabelByName(String name);

    /**
     * Retrieves all labels currently in the system.
     *
     * @return A list of all labels. Returns an empty list if no labels exist.
     */
    List<Label> getAllLabels();

    /**
     * Updates the name of an existing label.
     *
     * @param labelId The ID of the label to update.
     * @param newName The new name for the label.
     * @return The updated Label object.
     * @throws IllegalArgumentException if labelId or newName is invalid.
     * @throws RuntimeException if the label with the given ID is not found. (Placeholder for custom exception)
     */
    Label updateLabelName(String labelId, String newName);

    /**
     * Deletes a label from the system.
     * This method would typically also handle disassociating the label from
     * any tasks or notes it was applied to.
     *
     * @param labelId The ID of the label to delete.
     * @return true if the label was successfully deleted, false otherwise (e.g., label not found).
     * @throws IllegalArgumentException if labelId is invalid.
     */
    boolean deleteLabel(String labelId);

    /**
     * Associates a label with a specific task.
     * This implies updating the Task model to include the label's ID.
     *
     * @param labelId The ID of the label to associate.
     * @param taskId The ID of the task to apply the label to.
     * @return The updated Task object.
     * @throws IllegalArgumentException if any ID is invalid.
     * @throws RuntimeException if the label or task is not found. (Placeholder for custom exception)
     */
    Task associateLabelWithTask(String labelId, String taskId);

    /**
     * Removes the association of a label from a specific task.
     *
     * @param labelId The ID of the label to remove.
     * @param taskId The ID of the task to remove the label from.
     * @return The updated Task object.
     * @throws IllegalArgumentException if any ID is invalid.
     * @throws RuntimeException if the label or task is not found. (Placeholder for custom exception)
     */
    Task disassociateLabelFromTask(String labelId, String taskId);

    /**
     * Associates a label with a specific note.
     * This implies updating the Note model to include the label's ID.
     *
     * @param labelId The ID of the label to associate.
     * @param noteId The ID of the note to apply the label to.
     * @return The updated Note object.
     * @throws IllegalArgumentException if any ID is invalid.
     * @throws RuntimeException if the label or note is not found. (Placeholder for custom exception)
     */
    Note associateLabelWithNote(String labelId, String noteId);

    /**
     * Removes the association of a label from a specific note.
     *
     * @param labelId The ID of the label to remove.
     * @param noteId The ID of the note to remove the label from.
     * @return The updated Note object.
     * @throws IllegalArgumentException if any ID is invalid.
     * @throws RuntimeException if the label or note is not found. (Placeholder for custom exception)
     */
    Note disassociateLabelFromNote(String labelId, String noteId);

    /**
     * Retrieves all tasks that have a specific label associated with them.
     *
     * @param labelId The ID of the label to filter by.
     * @return A list of tasks associated with the specified label. Returns an empty list if none found.
     * @throws IllegalArgumentException if labelId is invalid.
     * @throws RuntimeException if the label is not found. (Placeholder for custom exception)
     */
    List<Task> getTasksByLabel(String labelId);

    /**
     * Retrieves all notes that have a specific label associated with them.
     *
     * @param labelId The ID of the label to filter by.
     * @return A list of notes associated with the specified label. Returns an empty list if none found.
     * @throws IllegalArgumentException if labelId is invalid.
     * @throws RuntimeException if the label is not found. (Placeholder for custom exception)
     */
    List<Note> getNotesByLabel(String labelId);
}

// src/main/java/com/intelliTask/core/service/impl/LabelServiceImpl.java

package service.impl;

import model.Label;
import model.Task; // To update Task when associating labels
import model.Note; // To update Note when associating labels
import service.LabelService;
import service.TaskService; // Dependency for task management/retrieval
import service.NoteService; // Dependency for note management/retrieval
import persistence.LabelRepository;
import util.IdGenerator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList; // For defensive copies of lists

/**
 * Concrete implementation of the LabelService interface.
 * This class encapsulates the core business logic for managing labels and
 * their associations with tasks and notes.
 * It depends on the LabelRepository for data access and coordinates with
 * TaskService and NoteService for managing associated entities,
 * adhering to the Dependency Inversion Principle (DIP).
 */
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository; // Dependency for label data
    private final TaskService taskService;         // Dependency for task operations
    private final NoteService noteService;         // Dependency for note operations

    /**
     * Constructor for LabelServiceImpl.
     * This demonstrates Manual Dependency Injection: LabelRepository, TaskService,
     * and NoteService are passed in as dependencies, promoting loose coupling and testability.
     *
     * @param labelRepository The repository to use for label data access.
     * @param taskService The service to use for task-related operations (e.g., getting/updating tasks).
     * @param noteService The service to use for note-related operations (e.g., getting/updating notes).
     * @throws IllegalArgumentException if any provided dependency is null.
     */
    public LabelServiceImpl(LabelRepository labelRepository,
                            TaskService taskService,
                            NoteService noteService) {
        if (labelRepository == null) {
            throw new IllegalArgumentException("LabelRepository cannot be null.");
        }
        if (taskService == null) {
            throw new IllegalArgumentException("TaskService cannot be null.");
        }
        if (noteService == null) {
            throw new IllegalArgumentException("NoteService cannot be null.");
        }
        this.labelRepository = labelRepository;
        this.taskService = taskService;
        this.noteService = noteService;
    }

    /**
     * Creates and adds a new label to the system.
     * The label ID will be generated internally.
     *
     * @param name The name of the new label.
     * @return The newly created Label object.
     * @throws IllegalArgumentException if the name is invalid (e.g., null or empty).
     * Ponder Point: For production, you might add business logic here to ensure
     * label names are unique (case-insensitive) or enforce naming conventions,
     * possibly by calling `labelRepository.findByName(name)`.
     */
    @Override
    public Label createLabel(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Label name cannot be empty.");
        }

        String id = IdGenerator.generateUniqueId();
        Label newLabel = new Label(id, name);
        return labelRepository.save(newLabel); // Delegate persistence
    }

    /**
     * Retrieves a specific label by its unique ID.
     *
     * @param labelId The ID of the label to retrieve.
     * @return An Optional containing the Label if found, or an empty Optional if not found.
     * @throws IllegalArgumentException if the labelId is invalid.
     */
    @Override
    public Optional<Label> getLabelById(String labelId) {
        if (labelId == null || labelId.trim().isEmpty()) {
            throw new IllegalArgumentException("Label ID cannot be null or empty.");
        }
        return labelRepository.findById(labelId); // Delegate retrieval
    }

    /**
     * Retrieves a label by its name.
     *
     * @param name The name of the label to retrieve.
     * @return An Optional containing the Label if found, or an empty Optional if not found.
     * Ponder Point: This method's efficiency currently relies on `findAll()` and then streaming.
     * For large datasets, a `findByName` method in the `LabelRepository` (which a database could optimize)
     * or a secondary in-memory index within the repository would be more performant.
     */
    @Override
    public Optional<Label> getLabelByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Label name cannot be null or empty.");
        }
        return labelRepository.findAll().stream()
                .filter(label -> label.getName().equalsIgnoreCase(name)) // Case-insensitive comparison
                .findFirst(); // Labels are typically unique by name
    }

    /**
     * Retrieves all labels currently in the system.
     *
     * @return A list of all labels. Returns an empty list if no labels exist.
     */
    @Override
    public List<Label> getAllLabels() {
        return labelRepository.findAll(); // Delegate retrieval
    }

    /**
     * Updates the name of an existing label.
     *
     * @param labelId The ID of the label to update.
     * @param newName The new name for the label.
     * @return The updated Label object.
     * @throws IllegalArgumentException if labelId or newName is invalid.
     * @throws RuntimeException if the label with the given ID is not found.
     * Ponder Point: If label names must be unique, check for duplicates here
     * before updating, or ensure the repository handles this constraint.
     */
    @Override
    public Label updateLabelName(String labelId, String newName) {
        if (labelId == null || labelId.trim().isEmpty()) {
            throw new IllegalArgumentException("Label ID cannot be null or empty.");
        }
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("New label name cannot be null or empty.");
        }

        Label label = labelRepository.findById(labelId)
                .orElseThrow(() -> new RuntimeException("Label not found with ID: " + labelId));

        label.setName(newName);
        return labelRepository.save(label); // Save the updated label
    }

    /**
     * Deletes a label from the system.
     * This method would typically also handle disassociating the label from
     * any tasks or notes it was applied to.
     *
     * @param labelId The ID of the label to delete.
     * @return true if the label was successfully deleted, false otherwise (e.g., label not found).
     * @throws IllegalArgumentException if labelId is invalid.
     * Ponder Point: Cascading deletion logic for labels.
     * When a label is deleted, it should be removed from all tasks and notes that use it.
     * This involves coordination with TaskService and NoteService. For our file-based system,
     * this requires explicit iteration and update. In a database, this might be handled by
     * cascading foreign key constraints or dedicated SQL queries.
     */
    @Override
    public boolean deleteLabel(String labelId) {
        if (labelId == null || labelId.trim().isEmpty()) {
            throw new IllegalArgumentException("Label ID cannot be null or empty.");
        }

        // --- Ponder Point: Cascading disassociation logic (Manual for file-based) ---
        // Retrieve all tasks and notes and remove this label from them.
        // This is business logic that ensures data consistency across entities.
        taskService.getAllTasks().stream()
                .filter(task -> task.getLabelIds().contains(labelId)) // Find tasks with this label
                .forEach(task -> {
                    // Create a mutable copy of the labels list for modification
                    List<String> updatedLabels = new ArrayList<>(task.getLabelIds());
                    updatedLabels.remove(labelId); // Remove the specific label ID
                    // Update the task through its service
                    taskService.updateTaskLabels(task.getId(), updatedLabels);
                });

        noteService.getAllNotes().stream()
                .filter(note -> note.getLabelIds().contains(labelId)) // Find notes with this label
                .forEach(note -> {
                    // Create a mutable copy of the labels list for modification
                    List<String> updatedLabels = new ArrayList<>(note.getLabelIds());
                    updatedLabels.remove(labelId); // Remove the specific label ID
                    // Update the note through its service
                    noteService.updateNoteLabels(note.getId(), updatedLabels);
                });
        // --- End Ponder Point ---

        return labelRepository.deleteById(labelId); // Delegate deletion of the label itself
    }

    /**
     * Associates a label with a specific task.
     * This implies updating the Task model to include the label's ID.
     *
     * @param labelId The ID of the label to associate.
     * @param taskId The ID of the task to apply the label to.
     * @return The updated Task object.
     * @throws IllegalArgumentException if any ID is invalid.
     * @throws RuntimeException if the label or task is not found.
     */
    @Override
    public Task associateLabelWithTask(String labelId, String taskId) {
        if (labelId == null || labelId.trim().isEmpty()) {
            throw new IllegalArgumentException("Label ID cannot be null or empty.");
        }
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("Task ID cannot be null or empty.");
        }

        // Ponder Point: Service orchestration for association.
        // We need to ensure both the label and the task exist before linking them.
        labelRepository.findById(labelId) // Just check for existence, no need for the object itself here
                .orElseThrow(() -> new RuntimeException("Label not found with ID: " + labelId));

        Task task = taskService.getTaskById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        // Add the label ID to the task's list using the Task model's method
        // Ponder Point: The `addLabel` method in `Task.java` handles duplicates.
        if (!task.addLabel(labelId)) {
            // If addLabel returns false, it means the label was already associated.
            // We could optionally throw an exception here if "already associated" is an error.
            // For now, it's a silent no-op if already present.
        }

        // After modifying the task object, save it via the TaskService.
        // Ponder Point: Calling `updateTaskLabels` is the most direct way to commit
        // this change via the service's defined API. Alternatively, if TaskService had
        // a generic `save(Task task)` or `update(Task task)` method that directly saved
        // the Task object, we could use that as well. Sticking to `updateTaskLabels` for now.
        return taskService.updateTaskLabels(taskId, task.getLabelIds());
    }

    /**
     * Removes the association of a label from a specific task.
     *
     * @param labelId The ID of the label to remove.
     * @param taskId The ID of the task to remove the label from.
     * @return The updated Task object.
     * @throws IllegalArgumentException if any ID is invalid.
     * @throws RuntimeException if the task is not found, or label not associated.
     * Ponder Point: For production, consider specific exceptions like `LabelNotAssociatedWithTaskException`.
     */
    @Override
    public Task disassociateLabelFromTask(String labelId, String taskId) {
        if (labelId == null || labelId.trim().isEmpty()) {
            throw new IllegalArgumentException("Label ID cannot be null or empty.");
        }
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("Task ID cannot be null or empty.");
        }

        // We need to ensure the task exists. Label existence is less critical for disassociation.
        Task task = taskService.getTaskById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        // Remove the label ID from the task's list using the Task model's method
        boolean removed = task.removeLabel(labelId); // This method returns true if successfully removed

        if (!removed) {
            throw new RuntimeException("Label with ID: " + labelId + " was not associated with Task: " + taskId + ". Cannot disassociate.");
        }

        // After modifying the task object, save it via the TaskService.
        return taskService.updateTaskLabels(taskId, task.getLabelIds());
    }

    /**
     * Associates a label with a specific note.
     * This implies updating the Note model to include the label's ID.
     *
     * @param labelId The ID of the label to associate.
     * @param noteId The ID of the note to apply the label to.
     * @return The updated Note object.
     * @throws IllegalArgumentException if any ID is invalid.
     * @throws RuntimeException if the label or note is not found.
     */
    @Override
    public Note associateLabelWithNote(String labelId, String noteId) {
        if (labelId == null || labelId.trim().isEmpty()) {
            throw new IllegalArgumentException("Label ID cannot be null or empty.");
        }
        if (noteId == null || noteId.trim().isEmpty()) {
            throw new IllegalArgumentException("Note ID cannot be null or empty.");
        }

        // Ensure both label and note exist.
        labelRepository.findById(labelId)
                .orElseThrow(() -> new RuntimeException("Label not found with ID: " + labelId));

        Note note = noteService.getNoteById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found with ID: " + noteId));

        // Add the label ID to the note's list using the Note model's method
        // The `addLabel` method in `Note.java` handles duplicates.
        if (!note.addLabel(labelId)) {
            // Optional: throw exception if already associated.
        }

        // Save the updated note via the NoteService.
        return noteService.updateNoteLabels(noteId, note.getLabelIds());
    }

    /**
     * Removes the association of a label from a specific note.
     *
     * @param labelId The ID of the label to remove.
     * @param noteId The ID of the note to remove the label from.
     * @return The updated Note object.
     * @throws IllegalArgumentException if any ID is invalid.
     * @throws RuntimeException if the note is not found, or label not associated.
     * Ponder Point: For production, consider specific exceptions like `LabelNotAssociatedWithNoteException`.
     */
    @Override
    public Note disassociateLabelFromNote(String labelId, String noteId) {
        if (labelId == null || labelId.trim().isEmpty()) {
            throw new IllegalArgumentException("Label ID cannot be null or empty.");
        }
        if (noteId == null || noteId.trim().isEmpty()) {
            throw new IllegalArgumentException("Note ID cannot be null or empty.");
        }

        Note note = noteService.getNoteById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found with ID: " + noteId));

        // Remove the label ID from the note's list using the Note model's method
        boolean removed = note.removeLabel(labelId);

        if (!removed) {
            throw new RuntimeException("Label with ID: " + labelId + " was not associated with Note: " + noteId + ". Cannot disassociate.");
        }

        // Save the updated note via the NoteService.
        return noteService.updateNoteLabels(noteId, note.getLabelIds());
    }

    /**
     * Retrieves all tasks that have a specific label associated with them.
     *
     * @param labelId The ID of the label to filter by.
     * @return A list of tasks associated with the specified label. Returns an empty list if none found.
     * @throws IllegalArgumentException if labelId is invalid.
     * @throws RuntimeException if the label is not found.
     * Ponder Point: In a real database, this would typically be a direct query to the TaskRepository
     * (e.g., `taskRepository.findByLabelId(labelId)`). For our file-based system, it involves
     * iterating through all tasks and checking their label IDs. For performance, this
     * might hint at needing a dedicated query method in the `TaskRepository` for database-backed systems.
     */
    @Override
    public List<Task> getTasksByLabel(String labelId) {
        if (labelId == null || labelId.trim().isEmpty()) {
            throw new IllegalArgumentException("Label ID cannot be null or empty.");
        }
        // Validate if the label itself exists (optional, but good for data integrity)
        labelRepository.findById(labelId)
                .orElseThrow(() -> new RuntimeException("Label not found with ID: " + labelId));

        // Get all tasks and filter them by their associated labels
        return taskService.getAllTasks().stream()
                .filter(task -> task.getLabelIds().contains(labelId))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all notes that have a specific label associated with them.
     *
     * @param labelId The ID of the label to filter by.
     * @return A list of notes associated with the specified label. Returns an empty list if none found.
     * @throws IllegalArgumentException if labelId is invalid.
     * @throws RuntimeException if the label is not found.
     */
    @Override
    public List<Note> getNotesByLabel(String labelId) {
        if (labelId == null || labelId.trim().isEmpty()) {
            throw new IllegalArgumentException("Label ID cannot be null or empty.");
        }
        // Validate if the label itself exists
        labelRepository.findById(labelId)
                .orElseThrow(() -> new RuntimeException("Label not found with ID: " + labelId));

        // Get all notes and filter them by their associated labels
        return noteService.getAllNotes().stream()
                .filter(note -> note.getLabelIds().contains(labelId))
                .collect(Collectors.toList());
    }
}

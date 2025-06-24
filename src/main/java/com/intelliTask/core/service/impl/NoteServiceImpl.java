// src/main/java/com/intelliTask/core/service/impl/NoteServiceImpl.java

package service.impl;

import model.Note;
import service.NoteService;
import persistence.NoteRepository;
import util.IdGenerator; // We'll use this for ID generation

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList; // For defensive copy when setting labels
import java.util.stream.Collectors; // For potential future filtering operations

/**
 * Concrete implementation of the NoteService interface.
 * This class encapsulates the core business logic for managing notes.
 * It depends on the NoteRepository interface to perform data access operations,
 * adhering to the Dependency Inversion Principle (DIP).
 *
 * UPDATED: Implemented the logic for managing note labels.
 */
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository; // Dependency on the NoteRepository interface

    /**
     * Constructor for NoteServiceImpl.
     * This demonstrates Manual Dependency Injection: the NoteRepository is
     * passed in as a dependency, rather than being created internally.
     *
     * @param noteRepository The repository to use for note data access.
     * @throws IllegalArgumentException if the provided noteRepository is null.
     */
    public NoteServiceImpl(NoteRepository noteRepository) {
        if (noteRepository == null) {
            throw new IllegalArgumentException("NoteRepository cannot be null.");
        }
        this.noteRepository = noteRepository;
    }

    /**
     * Creates and adds a new note to the system.
     * The note ID will be generated internally using IdGenerator.
     * This method acts as a Factory Method for Note creation within the service layer.
     *
     * @param title The title of the new note.
     * @param content The main content of the new note.
     * @return The newly created Note object.
     * @throws IllegalArgumentException if the title is invalid or essential parameters are null.
     */
    @Override
    public Note createNote(String title, String content) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Note title cannot be empty.");
        }
        // Content can be empty, so no check for that here.

        String id = IdGenerator.generateUniqueId(); // Using our centralized ID generator
        // Ponder Point: Note's creation and last modified timestamps are set within the Note model's constructor.
        // The service orchestrates the creation and delegates persistence.
        // The Note constructor now accepts labelIds, initializing as empty.
        Note newNote = new Note(id, title, content, new ArrayList<>());
        return noteRepository.save(newNote); // Delegate persistence to the repository
    }

    /**
     * Retrieves a specific note by its unique ID.
     *
     * @param noteId The ID of the note to retrieve.
     * @return An Optional containing the Note if found, or an empty Optional if not found.
     * @throws IllegalArgumentException if the noteId is invalid.
     */
    @Override
    public Optional<Note> getNoteById(String noteId) {
        if (noteId == null || noteId.trim().isEmpty()) {
            throw new IllegalArgumentException("Note ID cannot be null or empty.");
        }
        return noteRepository.findById(noteId); // Delegate retrieval to the repository
    }

    /**
     * Retrieves all notes currently in the system.
     *
     * @return A list of all notes. Returns an empty list if no notes exist.
     */
    @Override
    public List<Note> getAllNotes() {
        return noteRepository.findAll(); // Delegate to the repository
    }

    /**
     * Updates the title of an existing note.
     *
     * @param noteId The ID of the note to update.
     * @param newTitle The new title for the note.
     * @return The updated Note object.
     * @throws IllegalArgumentException if noteId or newTitle is invalid.
     * @throws RuntimeException if the note with the given ID is not found.
     * Ponder Point: For production, consider a custom exception like NoteNotFoundException
     * to provide more specific error messages and handling.
     */
    @Override
    public Note updateNoteTitle(String noteId, String newTitle) {
        if (noteId == null || noteId.trim().isEmpty()) {
            throw new IllegalArgumentException("Note ID cannot be null or empty.");
        }
        if (newTitle == null || newTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("New title cannot be null or empty.");
        }

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found with ID: " + noteId)); // Safe retrieval with Optional

        // Apply the update directly to the retrieved Note object
        // Ponder Point: The Note model's setTitle() method implicitly updates its lastModifiedAt.
        // This is an example of behavior residing in the model, which is acceptable for simple
        // attribute changes that affect the model's internal state consistency.
        note.setTitle(newTitle);

        return noteRepository.save(note); // Save the updated note
    }

    /**
     * Updates the content of an existing note.
     *
     * @param noteId The ID of the note to update.
     * @param newContent The new content for the note.
     * @return The updated Note object.
     * @throws IllegalArgumentException if noteId is invalid.
     * @throws RuntimeException if the note with the given ID is not found.
     */
    @Override
    public Note updateNoteContent(String noteId, String newContent) {
        if (noteId == null || noteId.trim().isEmpty()) {
            throw new IllegalArgumentException("Note ID cannot be null or empty.");
        }
        // newContent can be an empty string, but not null.
        // The Note model's setContent handles null by defaulting to empty.

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found with ID: " + noteId));

        note.setContent(newContent); // Model's setter updates its lastModifiedAt implicitly
        return noteRepository.save(note);
    }

    /**
     * Updates the list of label IDs associated with an existing note.
     * This method is primarily used by the LabelService to manage associations.
     *
     * @param noteId The ID of the note to update.
     * @param newLabelIds The new list of label IDs to associate with the note.
     * @return The updated Note object.
     * @throws IllegalArgumentException if noteId or newLabelIds is invalid.
     * @throws RuntimeException if the note with the given ID is not found.
     */
    @Override
    public Note updateNoteLabels(String noteId, List<String> newLabelIds) {
        if (noteId == null || noteId.trim().isEmpty()) {
            throw new IllegalArgumentException("Note ID cannot be null or empty.");
        }
        // newLabelIds can be null/empty if clearing all labels, but we'll treat null as an empty list.
        if (newLabelIds == null) {
            newLabelIds = new ArrayList<>(); // Defensive handling for null input
        }

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found with ID: " + noteId));

        // Ponder Point: We use the Note model's `setLabelIds` directly.
        // This method clears existing labels and adds all new ones.
        // This approach simplifies the service logic for updates where the entire list
        // is being replaced, common in many-to-many relationships.
        note.setLabelIds(newLabelIds);

        return noteRepository.save(note);
    }

    /**
     * Deletes a note from the system.
     *
     * @param noteId The ID of the note to delete.
     * @return true if the note was successfully deleted, false otherwise (e.g., note not found).
     * @throws IllegalArgumentException if noteId is invalid.
     * Ponder Point: In a real system, deleting a note might have cascading effects:
     * - Delete any reminders associated with this note.
     * This orchestration logic would reside here or be delegated to other services (e.g., ReminderService).
     * For now, we only delegate to the repository.
     */
    @Override
    public boolean deleteNote(String noteId) {
        if (noteId == null || noteId.trim().isEmpty()) {
            throw new IllegalArgumentException("Note ID cannot be null or empty.");
        }
        // Here, we would ideally coordinate with ReminderService to delete associated reminders
        // For example: reminderService.deleteRemindersForEntity(noteId);
        return noteRepository.deleteById(noteId); // Delegate deletion to the repository
    }
}

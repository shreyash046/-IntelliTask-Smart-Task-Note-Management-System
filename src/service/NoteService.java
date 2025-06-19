package service;

import model.Note;

import java.util.List;
import java.util.Optional; // For methods that might not return a result
import java.time.LocalDateTime; // For potentially setting reminder times or filtering by date

/**
 * Defines the contract for managing notes within the intelliTask system.
 * This interface encapsulates the core business logic related to notes.
 * It adheres to the Dependency Inversion Principle (DIP), allowing the
 * high-level NoteService to depend on abstractions, not concrete implementations
 * of data access.
 *
 * All methods here reflect business operations, not low-level data storage details.
 */
public interface NoteService {

    /**
     * Creates and adds a new note to the system.
     * The note ID will be generated internally, and creation/modification timestamps set.
     *
     * @param title The title of the new note.
     * @param content The main content of the new note.
     * @return The newly created Note object.
     * @throws IllegalArgumentException if the title is invalid or essential parameters are null.
     */
    Note createNote(String title, String content);

    /**
     * Retrieves a specific note by its unique ID.
     *
     * @param noteId The ID of the note to retrieve.
     * @return An Optional containing the Note if found, or an empty Optional if not found.
     */
    Optional<Note> getNoteById(String noteId);

    /**
     * Retrieves all notes currently in the system.
     *
     * @return A list of all notes. Returns an empty list if no notes exist.
     */
    List<Note> getAllNotes();

    /**
     * Updates the title of an existing note.
     *
     * @param noteId The ID of the note to update.
     * @param newTitle The new title for the note.
     * @return The updated Note object.
     * @throws IllegalArgumentException if noteId or newTitle is invalid.
     * @throws RuntimeException if the note with the given ID is not found. (Placeholder for custom exception)
     */
    Note updateNoteTitle(String noteId, String newTitle);

    /**
     * Updates the content of an existing note.
     *
     * @param noteId The ID of the note to update.
     * @param newContent The new content for the note.
     * @return The updated Note object.
     * @throws IllegalArgumentException if noteId is invalid.
     * @throws RuntimeException if the note with the given ID is not found. (Placeholder for custom exception)
     */
    Note updateNoteContent(String noteId, String newContent);

    /**
     * Deletes a note from the system.
     *
     * @param noteId The ID of the note to delete.
     * @return true if the note was successfully deleted, false otherwise (e.g., note not found).
     * @throws IllegalArgumentException if noteId is invalid.
     */
    boolean deleteNote(String noteId);

    // Future potential methods:
    // List<Note> searchNotesByKeyword(String keyword);
    // List<Note> getNotesCreatedBetween(LocalDateTime start, LocalDateTime end);
}

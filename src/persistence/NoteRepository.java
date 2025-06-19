package persistence;
import model.Note;

import java.util.List;
import java.util.Optional; // For methods that might not return a result

/**
 * Defines the contract for data access operations related to Note entities.
 * This interface represents the Repository pattern, abstracting the underlying
 * data storage mechanism (e.g., file system, database).
 * It adheres to the Dependency Inversion Principle (DIP) by providing an
 * abstraction that high-level services (like NoteService) can depend on,
 * rather than depending on concrete data access implementations.
 *
 * All methods here reflect persistent storage operations (CRUD - Create, Read, Update, Delete).
 */
public interface NoteRepository {

    /**
     * Saves a Note entity to the persistence layer.
     * If the note already exists (based on its ID), it should be updated.
     * Otherwise, it should be created.
     *
     * @param note The Note object to save.
     * @return The saved or updated Note object.
     * @throws IllegalArgumentException if the note or its ID is null.
     */
    Note save(Note note);

    /**
     * Retrieves a Note entity by its unique ID.
     *
     * @param noteId The ID of the note to retrieve.
     * @return An Optional containing the Note if found, or an empty Optional if not found.
     * @throws IllegalArgumentException if the noteId is null or empty.
     */
    Optional<Note> findById(String noteId);

    /**
     * Retrieves all Note entities from the persistence layer.
     *
     * @return A list of all notes. Returns an empty list if no notes exist.
     */
    List<Note> findAll();

    /**
     * Deletes a Note entity from the persistence layer by its ID.
     *
     * @param noteId The ID of the note to delete.
     * @return true if the note was successfully deleted, false otherwise (e.g., note not found).
     * @throws IllegalArgumentException if the noteId is null or empty.
     */
    boolean deleteById(String noteId);

    // Future potential methods for more specific queries, e.g., by title keyword, date range
    // List<Note> findByTitleContaining(String keyword);
    // List<Note> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}

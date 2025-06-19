package persistence.impl;

import model.Note;
import persistence.NoteRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Collections; // For unmodifiableMap

/**
 * File-based implementation of the NoteRepository interface.
 * This class stores Note objects in an in-memory HashMap for quick access,
 * and will eventually handle saving/loading these notes to/from a file.
 *
 * It adheres to the Repository pattern and the Dependency Inversion Principle (DIP)
 * by implementing the NoteRepository interface.
 */
public class FileNoteRepositoryImpl implements NoteRepository {

    // A HashMap to store notes in memory. Key: noteId, Value: Note object.
    // This acts as our "in-memory database" for this phase.
    private final Map<String, Note> notes = new HashMap<>();

    /**
     * Constructor for FileNoteRepositoryImpl.
     * In a real application, this might load initial data from a file.
     */
    public FileNoteRepositoryImpl() {
        // For now, it's empty. Later, we might add logic here to
        // load existing notes from a file upon instantiation.
        // For simplicity, we assume notes are loaded via a central DataStorageManager
        // which will then populate these repositories.
    }

    /**
     * Saves a Note entity to the persistence layer.
     * If the note already exists (based on its ID), it will be updated.
     * Otherwise, it will be added as a new note.
     *
     * @param note The Note object to save.
     * @return The saved or updated Note object.
     * @throws IllegalArgumentException if the note or its ID is null.
     */
    @Override
    public Note save(Note note) {
        if (note == null) {
            throw new IllegalArgumentException("Note cannot be null.");
        }
        if (note.getId() == null || note.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Note ID cannot be null or empty.");
        }
        // Using put directly will add a new note if ID doesn't exist,
        // or update an existing note if ID already exists.
        notes.put(note.getId(), note);
        // In a file-based system, you would typically trigger a save-to-file operation here
        // or rely on a batch save mechanism by a DataStorageManager.
        return note;
    }

    /**
     * Retrieves a Note entity by its unique ID.
     *
     * @param noteId The ID of the note to retrieve.
     * @return An Optional containing the Note if found, or an empty Optional if not found.
     * @throws IllegalArgumentException if the noteId is null or empty.
     */
    @Override
    public Optional<Note> findById(String noteId) {
        if (noteId == null || noteId.trim().isEmpty()) {
            throw new IllegalArgumentException("Note ID cannot be null or empty.");
        }
        // HashMap's get method is efficient for O(1) lookup.
        return Optional.ofNullable(notes.get(noteId));
    }

    /**
     * Retrieves all Note entities from the persistence layer.
     *
     * @return A list of all notes. Returns an empty list if no notes exist.
     */
    @Override
    public List<Note> findAll() {
        // Return a new ArrayList to prevent external modifications to the internal map's values.
        return new ArrayList<>(notes.values());
    }

    /**
     * Deletes a Note entity from the persistence layer by its ID.
     *
     * @param noteId The ID of the note to delete.
     * @return true if the note was successfully deleted, false otherwise (e.g., note not found).
     * @throws IllegalArgumentException if the noteId is null or empty.
     */
    @Override
    public boolean deleteById(String noteId) {
        if (noteId == null || noteId.trim().isEmpty()) {
            throw new IllegalArgumentException("Note ID cannot be null or empty.");
        }
        // remove returns the value associated with the key, or null if not found.
        // We check if the removed value was non-null to confirm deletion.
        return notes.remove(noteId) != null;
    }

    // --- Methods for managing the in-memory data for file persistence ---
    // These methods are typically used by a DataStorageManager for loading/saving.

    /**
     * Sets the entire map of notes. This is typically used by a data loading
     * mechanism (like DataStorageManager) to populate the repository from storage.
     *
     * @param loadedNotes A map of notes to set.
     */
    public void setNotes(Map<String, Note> loadedNotes) {
        // Clear existing data and then put all loaded notes
        this.notes.clear();
        if (loadedNotes != null) {
            this.notes.putAll(loadedNotes);
        }
    }

    /**
     * Retrieves the current in-memory map of notes. This is typically used by a data saving
     * mechanism (like DataStorageManager) to get all data to persist.
     *
     * @return An unmodifiable map of notes to prevent external direct modification.
     */
    public Map<String, Note> getNotesMap() {
        // Return an unmodifiable map to protect the internal state
        return Collections.unmodifiableMap(notes);
    }
}
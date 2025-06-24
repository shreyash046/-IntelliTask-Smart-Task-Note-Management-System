package persistence;

import model.Label;

import java.util.List;
import java.util.Optional; // For methods that might not return a result

/**
 * Defines the contract for data access operations related to Label entities.
 * This interface represents the Repository pattern, abstracting the underlying
 * data storage mechanism (e.g., file system, database).
 * It adheres to the Dependency Inversion Principle (DIP) by providing an
 * abstraction that high-level services (like LabelService) can depend on,
 * rather than depending on concrete data access implementations.
 *
 * All methods here reflect persistent storage operations (CRUD - Create, Read, Update, Delete).
 */
public interface LabelRepository {

    /**
     * Saves a Label entity to the persistence layer.
     * If the label already exists (based on its ID), it should be updated.
     * Otherwise, it should be created.
     *
     * @param label The Label object to save.
     * @return The saved or updated Label object.
     * @throws IllegalArgumentException if the label or its ID is null.
     */
    Label save(Label label);

    /**
     * Retrieves a Label entity by its unique ID.
     *
     * @param labelId The ID of the label to retrieve.
     * @return An Optional containing the Label if found, or an empty Optional if not found.
     * @throws IllegalArgumentException if the labelId is null or empty.
     */
    Optional<Label> findById(String labelId);

    /**
     * Retrieves all Label entities from the persistence layer.
     *
     * @return A list of all labels. Returns an empty list if no labels exist.
     */
    List<Label> findAll();

    /**
     * Deletes a Label entity from the persistence layer by its ID.
     *
     * @param labelId The ID of the label to delete.
     * @return true if the label was successfully deleted, false otherwise (e.g., label not found).
     * @throws IllegalArgumentException if the labelId is null or empty.
     */
    boolean deleteById(String labelId);

    // Future potential methods for more specific queries
    // Optional<Label> findByName(String name); // Useful for looking up labels by their text
    // List<Label> findByAssociatedTaskId(String taskId);
    // List<Label> findByAssociatedNoteId(String noteId);
}

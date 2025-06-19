// src/main/java/com/intelliTask/persistence/impl/FileLabelRepositoryImpl.java

package persistence.impl;

import model.Label;
import persistence.LabelRepository;

import java.util.ArrayList;
import java.util.Collections; // For unmodifiableMap
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors; // Will be useful for future queries like findByName

/**
 * File-based implementation of the LabelRepository interface.
 * This class stores Label objects in an in-memory HashMap for quick access,
 * and will eventually handle saving/loading these labels to/from a file.
 *
 * It adheres to the Repository pattern and the Dependency Inversion Principle (DIP)
 * by implementing the LabelRepository interface.
 */
public class FileLabelRepositoryImpl implements LabelRepository {

    // A HashMap to store labels in memory. Key: labelId, Value: Label object.
    // This acts as our "in-memory database" for this phase.
    private final Map<String, Label> labels = new HashMap<>();

    /**
     * Constructor for FileLabelRepositoryImpl.
     * In a real application, this might load initial data from a file.
     */
    public FileLabelRepositoryImpl() {
        // For now, it's empty. Later, we might add logic here to
        // load existing labels from a file upon instantiation.
        // For simplicity, we assume labels are loaded via a central DataStorageManager
        // which will then populate these repositories.
    }

    /**
     * Saves a Label entity to the persistence layer.
     * If the label already exists (based on its ID), it will be updated.
     * Otherwise, it will be added as a new label.
     *
     * @param label The Label object to save.
     * @return The saved or updated Label object.
     * @throws IllegalArgumentException if the label or its ID is null.
     */
    @Override
    public Label save(Label label) {
        if (label == null) {
            throw new IllegalArgumentException("Label cannot be null.");
        }
        if (label.getId() == null || label.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Label ID cannot be null or empty.");
        }
        // Using put directly will add a new label if ID doesn't exist,
        // or update an existing label if ID already exists.
        labels.put(label.getId(), label);
        // Ponder Point: In a transactional system, a 'save' might not immediately write
        // to disk but rather batch changes. For this simple file-based approach,
        // we assume DataStorageManager handles the actual file I/O for all repositories.
        return label;
    }

    /**
     * Retrieves a Label entity by its unique ID.
     *
     * @param labelId The ID of the label to retrieve.
     * @return An Optional containing the Label if found, or an empty Optional if not found.
     * @throws IllegalArgumentException if the labelId is null or empty.
     */
    @Override
    public Optional<Label> findById(String labelId) {
        if (labelId == null || labelId.trim().isEmpty()) {
            throw new IllegalArgumentException("Label ID cannot be null or empty.");
        }
        // HashMap's get method is efficient for O(1) lookup.
        return Optional.ofNullable(labels.get(labelId));
    }

    /**
     * Retrieves all Label entities from the persistence layer.
     *
     * @return A list of all labels. Returns an empty list if no labels exist.
     */
    @Override
    public List<Label> findAll() {
        // Return a new ArrayList to prevent external modifications to the internal map's values.
        // Ponder Point: Returning a copy (defensive copy) is crucial. If we returned
        // labels.values() directly, and an external piece of code modified that collection,
        // it could break the internal state of this repository. This defends against
        // unexpected side effects and maintains encapsulation.
        return new ArrayList<>(labels.values());
    }

    /**
     * Deletes a Label entity from the persistence layer by its ID.
     *
     * @param labelId The ID of the label to delete.
     * @return true if the label was successfully deleted, false otherwise (e.g., label not found).
     * @throws IllegalArgumentException if the labelId is null or empty.
     */
    @Override
    public boolean deleteById(String labelId) {
        if (labelId == null || labelId.trim().isEmpty()) {
            throw new IllegalArgumentException("Label ID cannot be null or empty.");
        }
        // remove returns the value associated with the key, or null if not found.
        // We check if the removed value was non-null to confirm deletion.
        return labels.remove(labelId) != null;
    }

    // --- Methods for managing the in-memory data for file persistence ---
    // These methods are typically used by a DataStorageManager for loading/saving.

    /**
     * Sets the entire map of labels. This is typically used by a data loading
     * mechanism (like DataStorageManager) to populate the repository from storage.
     *
     * @param loadedLabels A map of labels to set.
     */
    public void setLabels(Map<String, Label> loadedLabels) {
        // Clear existing data and then put all loaded labels
        this.labels.clear();
        if (loadedLabels != null) {
            this.labels.putAll(loadedLabels);
        }
    }

    /**
     * Retrieves the current in-memory map of labels. This is typically used by a data saving
     * mechanism (like DataStorageManager) to get all data to persist.
     *
     * @return An unmodifiable map of labels to prevent external direct modification.
     * Ponder Point: Like `findAll()`, returning an unmodifiable map here is crucial for
     * encapsulation and defensive programming. It ensures that the DataStorageManager
     * can read the current state for serialization but cannot inadvertently alter
     * the live, in-memory collection. This separation of concerns prevents data integrity issues.
     */
    public Map<String, Label> getLabelsMap() {
        // Return an unmodifiable map to protect the internal state
        return Collections.unmodifiableMap(labels);
    }
}

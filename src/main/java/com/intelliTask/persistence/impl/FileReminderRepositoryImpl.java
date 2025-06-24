// src/main/java/com/intelliTask/persistence/impl/FileReminderRepositoryImpl.java

package persistence.impl;

import model.Reminder;
import persistence.ReminderRepository;

import java.util.ArrayList;
import java.util.Collections; // For unmodifiableMap
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * File-based implementation of the ReminderRepository interface.
 * This class stores Reminder objects in an in-memory HashMap for quick access,
 * and will eventually handle saving/loading these reminders to/from a file.
 *
 * It adheres to the Repository pattern and the Dependency Inversion Principle (DIP)
 * by implementing the ReminderRepository interface.
 */
public class FileReminderRepositoryImpl implements ReminderRepository {

    // A HashMap to store reminders in memory. Key: reminderId, Value: Reminder object.
    // This acts as our "in-memory database" for this phase.
    private final Map<String, Reminder> reminders = new HashMap<>();

    /**
     * Constructor for FileReminderRepositoryImpl.
     * In a real application, this might load initial data from a file.
     */
    public FileReminderRepositoryImpl() {
        // For now, it's empty. Later, we might add logic here to
        // load existing reminders from a file upon instantiation.
        // For simplicity, we assume reminders are loaded via a central DataStorageManager
        // which will then populate these repositories.
    }

    /**
     * Saves a Reminder entity to the persistence layer.
     * If the reminder already exists (based on its ID), it will be updated.
     * Otherwise, it will be added as a new reminder.
     *
     * @param reminder The Reminder object to save.
     * @return The saved or updated Reminder object.
     * @throws IllegalArgumentException if the reminder or its ID is null.
     */
    @Override
    public Reminder save(Reminder reminder) {
        if (reminder == null) {
            throw new IllegalArgumentException("Reminder cannot be null.");
        }
        if (reminder.getId() == null || reminder.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Reminder ID cannot be null or empty.");
        }
        // Using put directly will add a new reminder if ID doesn't exist,
        // or update an existing reminder if ID already exists.
        reminders.put(reminder.getId(), reminder);
        // In a file-based system, you would typically trigger a save-to-file operation here
        // or rely on a batch save mechanism by a DataStorageManager.
        return reminder;
    }

    /**
     * Retrieves a Reminder entity by its unique ID.
     *
     * @param reminderId The ID of the reminder to retrieve.
     * @return An Optional containing the Reminder if found, or an empty Optional if not found.
     * @throws IllegalArgumentException if the reminderId is null or empty.
     */
    @Override
    public Optional<Reminder> findById(String reminderId) {
        if (reminderId == null || reminderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Reminder ID cannot be null or empty.");
        }
        // HashMap's get method is efficient for O(1) lookup.
        return Optional.ofNullable(reminders.get(reminderId));
    }

    /**
     * Retrieves all Reminder entities from the persistence layer.
     *
     * @return A list of all reminders. Returns an empty list if no reminders exist.
     */
    @Override
    public List<Reminder> findAll() {
        // Return a new ArrayList to prevent external modifications to the internal map's values.
        return new ArrayList<>(reminders.values());
    }

    /**
     * Deletes a Reminder entity from the persistence layer by its ID.
     *
     * @param reminderId The ID of the reminder to delete.
     * @return true if the reminder was successfully deleted, false otherwise (e.g., reminder not found).
     * @throws IllegalArgumentException if the reminderId is null or empty.
     */
    @Override
    public boolean deleteById(String reminderId) {
        if (reminderId == null || reminderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Reminder ID cannot be null or empty.");
        }
        // remove returns the value associated with the key, or null if not found.
        // We check if the removed value was non-null to confirm deletion.
        return reminders.remove(reminderId) != null;
    }

    // --- Methods for managing the in-memory data for file persistence ---
    // These methods are typically used by a DataStorageManager for loading/saving.

    /**
     * Sets the entire map of reminders. This is typically used by a data loading
     * mechanism (like DataStorageManager) to populate the repository from storage.
     *
     * @param loadedReminders A map of reminders to set.
     */
    public void setReminders(Map<String, Reminder> loadedReminders) {
        // Clear existing data and then put all loaded reminders
        this.reminders.clear();
        if (loadedReminders != null) {
            this.reminders.putAll(loadedReminders);
        }
    }

    /**
     * Retrieves the current in-memory map of reminders. This is typically used by a data saving
     * mechanism (like DataStorageManager) to get all data to persist.
     *
     * @return An unmodifiable map of reminders to prevent external direct modification.
     */
    public Map<String, Reminder> getRemindersMap() {
        // Return an unmodifiable map to protect the internal state
        return Collections.unmodifiableMap(reminders);
    }
}

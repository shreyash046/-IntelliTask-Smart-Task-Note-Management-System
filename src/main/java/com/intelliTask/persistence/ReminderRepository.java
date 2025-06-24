package persistence;

import model.Reminder;

import java.util.List;
import java.util.Optional; // For methods that might not return a result

/**
 * Defines the contract for data access operations related to Reminder entities.
 * This interface represents the Repository pattern, abstracting the underlying
 * data storage mechanism (e.g., file system, database).
 * It adheres to the Dependency Inversion Principle (DIP) by providing an
 * abstraction that high-level services (like ReminderService) can depend on,
 * rather than depending on concrete data access implementations.
 *
 * All methods here reflect persistent storage operations (CRUD - Create, Read, Update, Delete).
 */
public interface ReminderRepository {

    /**
     * Saves a Reminder entity to the persistence layer.
     * If the reminder already exists (based on its ID), it should be updated.
     * Otherwise, it should be created.
     *
     * @param reminder The Reminder object to save.
     * @return The saved or updated Reminder object.
     * @throws IllegalArgumentException if the reminder or its ID is null.
     */
    Reminder save(Reminder reminder);

    /**
     * Retrieves a Reminder entity by its unique ID.
     *
     * @param reminderId The ID of the reminder to retrieve.
     * @return An Optional containing the Reminder if found, or an empty Optional if not found.
     * @throws IllegalArgumentException if the reminderId is null or empty.
     */
    Optional<Reminder> findById(String reminderId);

    /**
     * Retrieves all Reminder entities from the persistence layer.
     *
     * @return A list of all reminders. Returns an empty list if no reminders exist.
     */
    List<Reminder> findAll();

    /**
     * Deletes a Reminder entity from the persistence layer by its ID.
     *
     * @param reminderId The ID of the reminder to delete.
     * @return true if the reminder was successfully deleted, false otherwise (e.g., reminder not found).
     * @throws IllegalArgumentException if the reminderId is null or empty.
     */
    boolean deleteById(String reminderId);

    // Future potential methods for more specific queries
    // List<Reminder> findByAssociatedEntityId(String entityId);
    // List<Reminder> findByReminderTimeBefore(LocalDateTime time);
    // List<Reminder> findByDismissedStatus(boolean dismissed);
}

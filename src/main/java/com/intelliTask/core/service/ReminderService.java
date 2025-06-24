package service;

import model.Reminder;

import java.time.LocalDateTime; // For reminder time parameters
import java.util.List;
import java.util.Optional;

/**
 * Defines the contract for managing reminders within the intelliTask system.
 * This interface encapsulates the core business logic related to reminders.
 * It adheres to the Dependency Inversion Principle (DIP), allowing the
 * high-level ReminderService to depend on abstractions, not concrete implementations
 * of data access.
 *
 * All methods here reflect business operations, not low-level data storage details.
 */
public interface ReminderService {

    /**
     * Creates and adds a new reminder associated with a task or note.
     * The reminder ID will be generated internally.
     *
     * @param message The message for the reminder.
     * @param reminderTime The scheduled date and time for the reminder.
     * @param associatedEntityId The ID of the Task or Note this reminder is linked to.
     * @param associatedEntityType The type of entity ("Task" or "Note") this reminder is for.
     * @return The newly created Reminder object.
     * @throws IllegalArgumentException if any essential parameter is null or invalid
     * (e.g., empty message, invalid reminder time,
     * unsupported associated entity type).
     * @throws RuntimeException if the associated entity (Task or Note) does not exist. (Placeholder for custom exception)
     */
    Reminder createReminder(String message, LocalDateTime reminderTime,
                            String associatedEntityId, String associatedEntityType);

    /**
     * Retrieves a specific reminder by its unique ID.
     *
     * @param reminderId The ID of the reminder to retrieve.
     * @return An Optional containing the Reminder if found, or an empty Optional if not found.
     */
    Optional<Reminder> getReminderById(String reminderId);

    /**
     * Retrieves all reminders currently in the system.
     *
     * @return A list of all reminders. Returns an empty list if no reminders exist.
     */
    List<Reminder> getAllReminders();

    /**
     * Retrieves all reminders associated with a specific entity (Task or Note).
     *
     * @param entityId The ID of the associated entity.
     * @return A list of reminders linked to the specified entity. Returns an empty list if none found.
     * @throws IllegalArgumentException if entityId is invalid.
     */
    List<Reminder> getRemindersForEntity(String entityId);

    /**
     * Retrieves all active (not dismissed) reminders that are due by a specific time.
     *
     * @param currentTime The current time to check against. Reminders due at or before this time are returned.
     * @return A list of due and active reminders. Returns an empty list if none found.
     * @throws IllegalArgumentException if currentTime is null.
     */
    List<Reminder> getDueReminders(LocalDateTime currentTime);

    /**
     * Updates the message of an existing reminder.
     *
     * @param reminderId The ID of the reminder to update.
     * @param newMessage The new message for the reminder.
     * @return The updated Reminder object.
     * @throws IllegalArgumentException if reminderId or newMessage is invalid.
     * @throws RuntimeException if the reminder with the given ID is not found. (Placeholder for custom exception)
     */
    Reminder updateReminderMessage(String reminderId, String newMessage);

    /**
     * Updates the scheduled time of an existing reminder.
     *
     * @param reminderId The ID of the reminder to update.
     * @param newReminderTime The new scheduled time for the reminder.
     * @return The updated Reminder object.
     * @throws IllegalArgumentException if reminderId or newReminderTime is invalid.
     * @throws RuntimeException if the reminder with the given ID is not found. (Placeholder for custom exception)
     */
    Reminder updateReminderTime(String reminderId, LocalDateTime newReminderTime);

    /**
     * Marks a reminder as dismissed. Dismissed reminders will no longer show as due.
     *
     * @param reminderId The ID of the reminder to dismiss.
     * @return The updated Reminder object.
     * @throws IllegalArgumentException if reminderId is invalid.
     * @throws RuntimeException if the reminder with the given ID is not found. (Placeholder for custom exception)
     */
    Reminder dismissReminder(String reminderId);

    /**
     * Deletes a reminder from the system.
     *
     * @param reminderId The ID of the reminder to delete.
     * @return true if the reminder was successfully deleted, false otherwise (e.g., reminder not found).
     * @throws IllegalArgumentException if reminderId is invalid.
     */
    boolean deleteReminder(String reminderId);
}

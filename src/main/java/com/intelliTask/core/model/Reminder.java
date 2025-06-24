package model;

import java.time.LocalDateTime; // For setting the reminder time
import java.util.Objects;       // For Objects.equals and Objects.hash

/**
 * Represents a reminder in the intelliTask system.
 * A reminder is associated with a specific task or note and has a scheduled time.
 * This class is a Plain Old Java Object (POJO) and serves as a core domain model.
 */
public class Reminder {
    private String id;               // Unique identifier for the reminder
    private String message;          // The reminder message
    private LocalDateTime reminderTime; // The scheduled time for the reminder
    private String associatedEntityId; // ID of the Task or Note this reminder is linked to
    private String associatedEntityType; // Type of the entity ("Task" or "Note")
    private boolean isDismissed;     // Indicates if the reminder has been dismissed

    /**
     * Constructor to create a new Reminder instance.
     *
     * @param id The unique ID for the reminder.
     * @param message The message to display for the reminder.
     * @param reminderTime The scheduled date and time for the reminder.
     * @param associatedEntityId The ID of the task or note this reminder is for.
     * @param associatedEntityType The type of entity ("Task" or "Note") this reminder is for.
     */
    public Reminder(String id, String message, LocalDateTime reminderTime,
                    String associatedEntityId, String associatedEntityType) {
        // Basic validation for essential fields
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Reminder ID cannot be empty.");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Reminder message cannot be empty.");
        }
        if (reminderTime == null) {
            throw new IllegalArgumentException("Reminder time cannot be null.");
        }
        if (associatedEntityId == null || associatedEntityId.trim().isEmpty()) {
            throw new IllegalArgumentException("Associated entity ID cannot be empty.");
        }
        if (associatedEntityType == null || associatedEntityType.trim().isEmpty() ||
                (!associatedEntityType.equals("Task") && !associatedEntityType.equals("Note"))) {
            throw new IllegalArgumentException("Associated entity type must be 'Task' or 'Note'.");
        }

        this.id = id;
        this.message = message;
        this.reminderTime = reminderTime;
        this.associatedEntityId = associatedEntityId;
        this.associatedEntityType = associatedEntityType;
        this.isDismissed = false; // Reminders are not dismissed by default upon creation
    }

    /**
     * Retrieves the unique identifier of the reminder.
     * @return The reminder's ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the message of the reminder.
     * @return The reminder's message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message of the reminder.
     * @param message The new message.
     */
    public void setMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Reminder message cannot be empty.");
        }
        this.message = message;
    }

    /**
     * Retrieves the scheduled time for the reminder.
     * @return The reminder's scheduled time.
     */
    public LocalDateTime getReminderTime() {
        return reminderTime;
    }

    /**
     * Sets the scheduled time for the reminder.
     * @param reminderTime The new scheduled time.
     */
    public void setReminderTime(LocalDateTime reminderTime) {
        if (reminderTime == null) {
            throw new IllegalArgumentException("Reminder time cannot be null.");
        }
        this.reminderTime = reminderTime;
    }

    /**
     * Retrieves the ID of the entity (Task or Note) this reminder is associated with.
     * @return The associated entity's ID.
     */
    public String getAssociatedEntityId() {
        return associatedEntityId;
    }

    // No setter for associatedEntityId as association should be immutable after creation.

    /**
     * Retrieves the type of the entity (e.g., "Task", "Note") this reminder is associated with.
     * @return The associated entity's type.
     */
    public String getAssociatedEntityType() {
        return associatedEntityType;
    }

    // No setter for associatedEntityType as association should be immutable after creation.

    /**
     * Checks if the reminder has been dismissed.
     * @return true if the reminder is dismissed, false otherwise.
     */
    public boolean isDismissed() {
        return isDismissed;
    }

    /**
     * Sets the dismissed status of the reminder.
     * @param dismissed The new dismissed status.
     */
    public void setDismissed(boolean dismissed) {
        isDismissed = dismissed;
    }

    /**
     * Generates a string representation of the Reminder object.
     * @return A string showing the reminder's ID, message, time, associated entity, and dismissed status.
     */
    @Override
    public String toString() {
        return "Reminder{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", reminderTime=" + reminderTime +
                ", associatedEntityId='" + associatedEntityId + '\'' +
                ", associatedEntityType='" + associatedEntityType + '\'' +
                ", isDismissed=" + isDismissed +
                '}';
    }

    /**
     * Compares this Reminder object to another object for equality.
     * Two reminders are considered equal if their IDs are the same.
     * @param o The object to compare with.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reminder reminder = (Reminder) o;
        return Objects.equals(id, reminder.id);
    }

    /**
     * Returns a hash code value for the object based on the reminder's ID.
     * @return A hash code based on the reminder's ID.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
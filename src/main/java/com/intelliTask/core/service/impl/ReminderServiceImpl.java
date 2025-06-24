// src/main/java/com/intelliTask/core/service/impl/ReminderServiceImpl.java

package service.impl;

import model.Reminder;
import service.NoteService; // For validating associated notes
import service.ReminderService;
import service.TaskService; // For validating associated tasks
import persistence.ReminderRepository;
import util.IdGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Concrete implementation of the ReminderService interface.
 * This class encapsulates the core business logic for managing reminders.
 * It depends on the ReminderRepository interface for data access and
 * coordinates with TaskService and NoteService to validate associations,
 * adhering to the Dependency Inversion Principle (DIP).
 */
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository reminderRepository; // Dependency for reminder data
    private final TaskService taskService;               // Dependency for task validation
    private final NoteService noteService;               // Dependency for note validation

    /**
     * Constructor for ReminderServiceImpl.
     * This demonstrates Manual Dependency Injection: ReminderRepository, TaskService,
     * and NoteService are passed in as dependencies, promoting loose coupling and testability.
     *
     * @param reminderRepository The repository to use for reminder data access.
     * @param taskService The service to use for validating associated tasks.
     * @param noteService The service to use for validating associated notes.
     * @throws IllegalArgumentException if any provided dependency is null.
     */
    public ReminderServiceImpl(ReminderRepository reminderRepository,
                               TaskService taskService,
                               NoteService noteService) {
        if (reminderRepository == null) {
            throw new IllegalArgumentException("ReminderRepository cannot be null.");
        }
        if (taskService == null) {
            throw new IllegalArgumentException("TaskService cannot be null.");
        }
        if (noteService == null) {
            throw new IllegalArgumentException("NoteService cannot be null.");
        }
        this.reminderRepository = reminderRepository;
        this.taskService = taskService;
        this.noteService = noteService;
    }

    /**
     * Creates and adds a new reminder associated with a task or note.
     * The reminder ID will be generated internally.
     * Business logic includes validating the existence of the associated entity.
     *
     * @param message The message for the reminder.
     * @param reminderTime The scheduled date and time for the reminder.
     * @param associatedEntityId The ID of the Task or Note this reminder is linked to.
     * @param associatedEntityType The type of entity ("Task" or "Note") this reminder is for.
     * @return The newly created Reminder object.
     * @throws IllegalArgumentException if any essential parameter is null or invalid.
     * @throws RuntimeException if the associated entity (Task or Note) does not exist.
     * Ponder Point: For production, consider specific exceptions like EntityNotFoundException
     * or InvalidEntityTypeException.
     */
    @Override
    public Reminder createReminder(String message, LocalDateTime reminderTime,
                                   String associatedEntityId, String associatedEntityType) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Reminder message cannot be empty.");
        }
        if (reminderTime == null) {
            throw new IllegalArgumentException("Reminder time cannot be null.");
        }
        if (associatedEntityId == null || associatedEntityId.trim().isEmpty()) {
            throw new IllegalArgumentException("Associated entity ID cannot be empty.");
        }
        if (associatedEntityType == null || associatedEntityType.trim().isEmpty()) {
            throw new IllegalArgumentException("Associated entity type cannot be empty.");
        }

        // Ponder Point: Service orchestration and validation.
        // The ReminderService needs to confirm the associated entity actually exists.
        switch (associatedEntityType) {
            case "Task":
                taskService.getTaskById(associatedEntityId)
                        .orElseThrow(() -> new RuntimeException("Associated Task not found with ID: " + associatedEntityId));
                break;
            case "Note":
                noteService.getNoteById(associatedEntityId)
                        .orElseThrow(() -> new RuntimeException("Associated Note not found with ID: " + associatedEntityId));
                break;
            default:
                throw new IllegalArgumentException("Unsupported associated entity type: " + associatedEntityType + ". Must be 'Task' or 'Note'.");
        }

        String id = IdGenerator.generateUniqueId();
        Reminder newReminder = new Reminder(id, message, reminderTime, associatedEntityId, associatedEntityType);
        return reminderRepository.save(newReminder); // Delegate persistence
    }

    /**
     * Retrieves a specific reminder by its unique ID.
     *
     * @param reminderId The ID of the reminder to retrieve.
     * @return An Optional containing the Reminder if found, or an empty Optional if not found.
     * @throws IllegalArgumentException if the reminderId is invalid.
     */
    @Override
    public Optional<Reminder> getReminderById(String reminderId) {
        if (reminderId == null || reminderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Reminder ID cannot be null or empty.");
        }
        return reminderRepository.findById(reminderId); // Delegate retrieval
    }

    /**
     * Retrieves all reminders currently in the system.
     *
     * @return A list of all reminders. Returns an empty list if no reminders exist.
     */
    @Override
    public List<Reminder> getAllReminders() {
        return reminderRepository.findAll(); // Delegate retrieval
    }

    /**
     * Retrieves all reminders associated with a specific entity (Task or Note).
     *
     * @param entityId The ID of the associated entity.
     * @return A list of reminders linked to the specified entity. Returns an empty list if none found.
     * @throws IllegalArgumentException if entityId is invalid.
     */
    @Override
    public List<Reminder> getRemindersForEntity(String entityId) {
        if (entityId == null || entityId.trim().isEmpty()) {
            throw new IllegalArgumentException("Entity ID cannot be null or empty.");
        }
        // Ponder Point: Filtering by association. This relies on the Reminder model storing the associatedEntityId.
        // For very large datasets, pushing this filter to the repository (if possible with the chosen persistence technology)
        // would be more efficient, e.g., reminderRepository.findByAssociatedEntityId(entityId).
        return reminderRepository.findAll().stream()
                .filter(reminder -> reminder.getAssociatedEntityId().equals(entityId))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all active (not dismissed) reminders that are due by a specific time.
     *
     * @param currentTime The current time to check against. Reminders due at or before this time are returned.
     * @return A list of due and active reminders. Returns an empty list if none found.
     * @throws IllegalArgumentException if currentTime is null.
     */
    @Override
    public List<Reminder> getDueReminders(LocalDateTime currentTime) {
        if (currentTime == null) {
            throw new IllegalArgumentException("Current time cannot be null.");
        }
        // Ponder Point: Business rule for "due" reminders.
        // A reminder is due if its reminderTime is before or at the currentTime
        // AND it has not been dismissed.
        return reminderRepository.findAll().stream()
                .filter(reminder -> !reminder.isDismissed())
                .filter(reminder -> !reminder.getReminderTime().isAfter(currentTime)) // reminderTime <= currentTime
                .collect(Collectors.toList());
    }

    /**
     * Updates the message of an existing reminder.
     *
     * @param reminderId The ID of the reminder to update.
     * @param newMessage The new message for the reminder.
     * @return The updated Reminder object.
     * @throws IllegalArgumentException if reminderId or newMessage is invalid.
     * @throws RuntimeException if the reminder with the given ID is not found.
     */
    @Override
    public Reminder updateReminderMessage(String reminderId, String newMessage) {
        if (reminderId == null || reminderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Reminder ID cannot be null or empty.");
        }
        if (newMessage == null || newMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("New reminder message cannot be null or empty.");
        }

        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new RuntimeException("Reminder not found with ID: " + reminderId));

        reminder.setMessage(newMessage);
        return reminderRepository.save(reminder); // Save the updated reminder
    }

    /**
     * Updates the scheduled time of an existing reminder.
     *
     * @param reminderId The ID of the reminder to update.
     * @param newReminderTime The new scheduled time for the reminder.
     * @return The updated Reminder object.
     * @throws IllegalArgumentException if reminderId or newReminderTime is invalid.
     * @throws RuntimeException if the reminder with the given ID is not found.
     */
    @Override
    public Reminder updateReminderTime(String reminderId, LocalDateTime newReminderTime) {
        if (reminderId == null || reminderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Reminder ID cannot be null or empty.");
        }
        if (newReminderTime == null) {
            throw new IllegalArgumentException("New reminder time cannot be null.");
        }

        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new RuntimeException("Reminder not found with ID: " + reminderId));

        // Ponder Point: Business rule: a reminder time shouldn't be in the past, unless explicitly allowed.
        // If (newReminderTime.isBefore(LocalDateTime.now())) { throw new IllegalArgumentException("Reminder time cannot be in the past."); }
        reminder.setReminderTime(newReminderTime);
        return reminderRepository.save(reminder);
    }

    /**
     * Marks a reminder as dismissed. Dismissed reminders will no longer show as due.
     *
     * @param reminderId The ID of the reminder to dismiss.
     * @return The updated Reminder object.
     * @throws IllegalArgumentException if reminderId is invalid.
     * @throws RuntimeException if the reminder with the given ID is not found.
     */
    @Override
    public Reminder dismissReminder(String reminderId) {
        if (reminderId == null || reminderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Reminder ID cannot be null or empty.");
        }

        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new RuntimeException("Reminder not found with ID: " + reminderId));

        reminder.setDismissed(true); // Set dismissed flag to true
        return reminderRepository.save(reminder);
    }

    /**
     * Deletes a reminder from the system.
     *
     * @param reminderId The ID of the reminder to delete.
     * @return true if the reminder was successfully deleted, false otherwise (e.g., reminder not found).
     * @throws IllegalArgumentException if reminderId is invalid.
     */
    @Override
    public boolean deleteReminder(String reminderId) {
        if (reminderId == null || reminderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Reminder ID cannot be null or empty.");
        }
        return reminderRepository.deleteById(reminderId); // Delegate deletion
    }
}

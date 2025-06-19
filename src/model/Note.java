// src/main/java/com/intelliTask/core/model/Note.java

package model;

import java.time.LocalDateTime; // For tracking creation and modification timestamps
import java.util.Objects;       // For Objects.equals and Objects.hash
import java.util.List;          // For associated labels
import java.util.ArrayList;     // For initializing the list

/**
 * Represents a single note in the intelliTask system.
 * This class is a Plain Old Java Object (POJO) and serves as a core domain model.
 * It primarily holds data related to a note, such as its title, content,
 * and timestamps for creation and last modification.
 *
 * UPDATED: Added support for associating labels.
 */
public class Note {
    private String id;               // Unique identifier for the note
    private String title;            // The title of the note
    private String content;          // The main content of the note
    private LocalDateTime createdAt;  // Timestamp when the note was created
    private LocalDateTime lastModifiedAt; // Timestamp when the note was last modified
    private List<String> labelIds;   // List of IDs of labels associated with this note

    /**
     * Constructor to create a new Note instance.
     * The 'createdAt' field is automatically set to the current time upon creation.
     * The 'lastModifiedAt' field is also initially set to 'createdAt'.
     *
     * @param id The unique ID for the note.
     * @param title The title of the note.
     * @param content The main content of the note.
     */
    public Note(String id, String title, String content) {
        // Basic validation: ensure ID and title are not null or empty
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Note ID cannot be empty.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Note title cannot be empty.");
        }
        // Content can be empty, so no check for that.

        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now(); // Set creation time automatically
        this.lastModifiedAt = this.createdAt; // Initially, last modified is same as created
        this.labelIds = new ArrayList<>(); // Initialize an empty list for labels
    }

    /**
     * Overloaded constructor to create a new Note instance, including initial labels.
     * This is useful for deserialization or when creating notes with pre-defined labels.
     *
     * @param id The unique ID for the note.
     * @param title The title of the note.
     * @param content The main content of the note.
     * @param labelIds An initial list of label IDs to associate with the note.
     */
    public Note(String id, String title, String content, List<String> labelIds) {
        this(id, title, content); // Call the primary constructor
        if (labelIds != null) {
            this.labelIds.addAll(labelIds); // Add all provided labels
        }
    }

    /**
     * Retrieves the unique identifier of the note.
     * @return The note's ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the title of the note.
     * @return The note's title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the note.
     * Updates the 'lastModifiedAt' timestamp.
     * @param title The new title.
     */
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Note title cannot be empty.");
        }
        this.title = title;
        this.lastModifiedAt = LocalDateTime.now(); // Update modification time
    }

    /**
     * Retrieves the content of the note.
     * @return The note's content.
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of the note.
     * Updates the 'lastModifiedAt' timestamp.
     * @param content The new content.
     */
    public void setContent(String content) {
        // Content can be empty, so we can check for null to prevent NullPointerExceptions
        if (content == null) {
            this.content = ""; // Default to empty string if null is provided
        } else {
            this.content = content;
        }
        this.lastModifiedAt = LocalDateTime.now(); // Update modification time
    }

    /**
     * Retrieves the timestamp when the note was created.
     * @return The creation timestamp.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Retrieves the timestamp when the note was last modified.
     * @return The last modification timestamp.
     */
    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }

    /**
     * Retrieves a copy of the list of label IDs associated with this note.
     * @return A new List containing the label IDs.
     */
    public List<String> getLabelIds() {
        return new ArrayList<>(labelIds); // Return a defensive copy
    }

    /**
     * Sets the list of label IDs for this note.
     * This method is typically used by services or during deserialization.
     * @param labelIds The new list of label IDs.
     */
    public void setLabelIds(List<String> labelIds) {
        this.labelIds.clear();
        if (labelIds != null) {
            // Ensure uniqueness if desired, though service might handle this.
            this.labelIds.addAll(labelIds);
        }
    }

    /**
     * Adds a label ID to the note's list of associated labels.
     * @param labelId The ID of the label to add.
     * @return true if the label was added, false if it was already present.
     */
    public boolean addLabel(String labelId) {
        if (labelId == null || labelId.trim().isEmpty()) {
            throw new IllegalArgumentException("Label ID cannot be null or empty.");
        }
        if (!labelIds.contains(labelId)) {
            return labelIds.add(labelId);
        }
        return false;
    }

    /**
     * Removes a label ID from the note's list of associated labels.
     * @param labelId The ID of the label to remove.
     * @return true if the label was removed, false if it was not found.
     */
    public boolean removeLabel(String labelId) {
        if (labelId == null || labelId.trim().isEmpty()) {
            throw new IllegalArgumentException("Label ID cannot be null or empty.");
        }
        return labelIds.remove(labelId);
    }

    /**
     * Generates a string representation of the Note object.
     * @return A string showing the note's ID, title, creation time, last modification time, and associated label IDs.
     */
    @Override
    public String toString() {
        return "Note{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", createdAt=" + createdAt +
                ", lastModifiedAt=" + lastModifiedAt +
                ", labelIds=" + labelIds +
                '}';
    }

    /**
     * Compares this Note object to another object for equality.
     * Two notes are considered equal if their IDs are the same.
     * @param o The object to compare with.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equals(id, note.id);
    }

    /**
     * Returns a hash code value for the object based on the note's ID.
     * @return A hash code based on the note's ID.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

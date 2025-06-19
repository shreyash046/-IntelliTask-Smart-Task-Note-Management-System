package model;

import java.time.LocalDateTime; // For tracking creation and modification timestamps
import java.util.Objects;       // For Objects.equals and Objects.hash

/**
 * Represents a single note in the intelliTask system.
 * This class is a Plain Old Java Object (POJO) and serves as a core domain model.
 * It primarily holds data related to a note, such as its title, content,
 * and timestamps for creation and last modification.
 */
public class Note {
    private String id;               // Unique identifier for the note
    private String title;            // The title of the note
    private String content;          // The main content of the note
    private LocalDateTime createdAt;  // Timestamp when the note was created
    private LocalDateTime lastModifiedAt; // Timestamp when the note was last modified

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
        // Content can be empty, but we can check for null to prevent NullPointerExceptions
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

    // Setter for lastModifiedAt is made protected/package-private or removed
    // if we want to strictly control when it's updated (e.g., only via setTitle/setContent)
    // For now, we'll keep it private to ensure it's updated internally.
    // However, if persistence layer needs to set it directly during loading,
    // a protected/package-private setter or constructor parameter might be added.
    // For simplicity of this POJO, we'll assume setters manage it.

    /**
     * Generates a string representation of the Note object.
     * @return A string showing the note's ID, title, creation time, and last modification time.
     */
    @Override
    public String toString() {
        return "Note{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", createdAt=" + createdAt +
                ", lastModifiedAt=" + lastModifiedAt +
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
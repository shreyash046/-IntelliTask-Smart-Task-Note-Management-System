package com.intelliTask.core.model;

import java.util.Objects; // For Objects.equals and Objects.hash

/**
 * Represents a label (tag) in the intelliTask system.
 * Labels can be used to categorize or group tasks and notes.
 * This class is a Plain Old Java Object (POJO) and serves as a core domain model.
 */
public class Label {
    private String id;   // Unique identifier for the label
    private String name; // The name of the label (e.g., "Work", "Personal", "Urgent")

    /**
     * Constructor to create a new Label instance.
     *
     * @param id The unique ID for the label.
     * @param name The name of the label.
     */
    public Label(String id, String name) {
        // Basic validation: ensure ID and name are not null or empty
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Label ID cannot be empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Label name cannot be empty.");
        }

        this.id = id;
        this.name = name;
    }

    /**
     * Retrieves the unique identifier of the label.
     * @return The label's ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the name of the label.
     * @return The label's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the label.
     * @param name The new name for the label.
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Label name cannot be empty.");
        }
        this.name = name;
    }

    /**
     * Generates a string representation of the Label object.
     * @return A string showing the label's ID and name.
     */
    @Override
    public String toString() {
        return "Label{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    /**
     * Compares this Label object to another object for equality.
     * Two labels are considered equal if their IDs are the same.
     * @param o The object to compare with.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Label label = (Label) o;
        return Objects.equals(id, label.id);
    }

    /**
     * Returns a hash code value for the object based on the label's ID.
     * @return A hash code based on the label's ID.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
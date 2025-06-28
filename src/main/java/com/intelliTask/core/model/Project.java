// src/main/java/com/intelliTask/core/model/Project.java

package com.intelliTask.core.model;

import com.intelliTask.core.model.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a project in the intelliTask system.
 * This class is a JPA Entity, mapped to a database table, and uses Lombok
 * for boilerplate code generation.
 *
 * A Project can contain a collection of Task IDs (String) that are associated with it.
 */
@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@ToString(exclude = "taskIds")
public class Project {

    /**
     * Unique identifier for the project.
     * Mapped as the primary key and generated as a UUID by JPA.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    /**
     * The name of the project.
     * Must be unique and non-null in the database.
     */
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    /**
     * A detailed description of the project.
     * This field is nullable in the database.
     */
    @Column(name = "description")
    private String description;

    /**
     * The current status of the project (e.g., PENDING, IN_PROGRESS, COMPLETED).
     * Stored as a String in the database.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    /**
     * Timestamp when the project was created.
     * Automatically set upon creation and not updatable.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Timestamp when the project was last modified.
     * Automatically updated on every modification.
     */
    @Column(name = "last_modified_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime lastModifiedAt;

    /**
     * A collection of IDs of tasks associated with this project.
     * Stored in a separate join table managed by JPA.
     * The collection is eagerly fetched.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "project_tasks", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "task_id")
    private List<String> taskIds = new ArrayList<>(); // Initialized to prevent NullPointerException

    /**
     * Custom constructor to create a new Project instance.
     * Used by application logic for new entity creation.
     *
     * @param name The name of the project.
     * @param description A description of the project.
     * @throws IllegalArgumentException if the name is null or empty.
     */
    public Project(String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty.");
        }
        this.name = name;
        this.description = description;
        this.status = Status.PENDING; // Projects start with PENDING status by default
    }
}

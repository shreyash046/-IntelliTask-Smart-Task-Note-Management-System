package persistence.impl;

// src/main/java/com/intelliTask/persistence/impl/FileProjectRepositoryImpl.java



import model.Project;
import persistence.ProjectRepository;

import java.util.ArrayList;
import java.util.Collections; // For unmodifiableMap
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * File-based implementation of the ProjectRepository interface.
 * This class stores Project objects in an in-memory HashMap for quick access,
 * and will eventually handle saving/loading these projects to/from a file.
 *
 * It adheres to the Repository pattern and the Dependency Inversion Principle (DIP)
 * by implementing the ProjectRepository interface.
 */
public class FileProjectRepositoryImpl implements ProjectRepository {

    // A HashMap to store projects in memory. Key: projectId, Value: Project object.
    // This acts as our "in-memory database" for this phase.
    private final Map<String, Project> projects = new HashMap<>();

    /**
     * Constructor for FileProjectRepositoryImpl.
     * In a real application, this might load initial data from a file.
     */
    public FileProjectRepositoryImpl() {
        // For now, it's empty. Later, we might add logic here to
        // load existing projects from a file upon instantiation.
        // For simplicity, we assume projects are loaded via a central DataStorageManager
        // which will then populate these repositories.
    }

    /**
     * Saves a Project entity to the persistence layer.
     * If the project already exists (based on its ID), it will be updated.
     * Otherwise, it will be added as a new project.
     *
     * @param project The Project object to save.
     * @return The saved or updated Project object.
     * @throws IllegalArgumentException if the project or its ID is null.
     */
    @Override
    public Project save(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null.");
        }
        if (project.getId() == null || project.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Project ID cannot be null or empty.");
        }
        // Using put directly will add a new project if ID doesn't exist,
        // or update an existing project if ID already exists.
        projects.put(project.getId(), project);
        // In a file-based system, you would typically trigger a save-to-file operation here
        // or rely on a batch save mechanism by a DataStorageManager.
        return project;
    }

    /**
     * Retrieves a Project entity by its unique ID.
     *
     * @param projectId The ID of the project to retrieve.
     * @return An Optional containing the Project if found, or an empty Optional if not found.
     * @throws IllegalArgumentException if the projectId is null or empty.
     */
    @Override
    public Optional<Project> findById(String projectId) {
        if (projectId == null || projectId.trim().isEmpty()) {
            throw new IllegalArgumentException("Project ID cannot be null or empty.");
        }
        // HashMap's get method is efficient for O(1) lookup.
        return Optional.ofNullable(projects.get(projectId));
    }

    /**
     * Retrieves all Project entities from the persistence layer.
     *
     * @return A list of all projects. Returns an empty list if no projects exist.
     */
    @Override
    public List<Project> findAll() {
        // Return a new ArrayList to prevent external modifications to the internal map's values.
        return new ArrayList<>(projects.values());
    }

    /**
     * Deletes a Project entity from the persistence layer by its ID.
     *
     * @param projectId The ID of the project to delete.
     * @return true if the project was successfully deleted, false otherwise (e.g., project not found).
     * @throws IllegalArgumentException if the projectId is null or empty.
     */
    @Override
    public boolean deleteById(String projectId) {
        if (projectId == null || projectId.trim().isEmpty()) {
            throw new IllegalArgumentException("Project ID cannot be null or empty.");
        }
        // remove returns the value associated with the key, or null if not found.
        // We check if the removed value was non-null to confirm deletion.
        return projects.remove(projectId) != null;
    }

    // --- Methods for managing the in-memory data for file persistence ---
    // These methods are typically used by a DataStorageManager for loading/saving.

    /**
     * Sets the entire map of projects. This is typically used by a data loading
     * mechanism (like DataStorageManager) to populate the repository from storage.
     *
     * @param loadedProjects A map of projects to set.
     */
    public void setProjects(Map<String, Project> loadedProjects) {
        // Clear existing data and then put all loaded projects
        this.projects.clear();
        if (loadedProjects != null) {
            this.projects.putAll(loadedProjects);
        }
    }

    /**
     * Retrieves the current in-memory map of projects. This is typically used by a data saving
     * mechanism (like DataStorageManager) to get all data to persist.
     *
     * @return An unmodifiable map of projects to prevent external direct modification.
     */
    public Map<String, Project> getProjectsMap() {
        // Return an unmodifiable map to protect the internal state
        return Collections.unmodifiableMap(projects);
    }
}


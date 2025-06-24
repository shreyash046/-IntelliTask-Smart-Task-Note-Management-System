package persistence;

import model.Project;

import java.util.List;
import java.util.Optional; // For methods that might not return a result

/**
 * Defines the contract for data access operations related to Project entities.
 * This interface represents the Repository pattern, abstracting the underlying
 * data storage mechanism (e.g., file system, database).
 * It adheres to the Dependency Inversion Principle (DIP) by providing an
 * abstraction that high-level services (like ProjectService) can depend on,
 * rather than depending on concrete data access implementations.
 *
 * All methods here reflect persistent storage operations (CRUD - Create, Read, Update, Delete).
 */
public interface ProjectRepository {

    /**
     * Saves a Project entity to the persistence layer.
     * If the project already exists (based on its ID), it should be updated.
     * Otherwise, it should be created.
     *
     * @param project The Project object to save.
     * @return The saved or updated Project object.
     * @throws IllegalArgumentException if the project or its ID is null.
     */
    Project save(Project project);

    /**
     * Retrieves a Project entity by its unique ID.
     *
     * @param projectId The ID of the project to retrieve.
     * @return An Optional containing the Project if found, or an empty Optional if not found.
     * @throws IllegalArgumentException if the projectId is null or empty.
     */
    Optional<Project> findById(String projectId);

    /**
     * Retrieves all Project entities from the persistence layer.
     *
     * @return A list of all projects. Returns an empty list if no projects exist.
     */
    List<Project> findAll();

    /**
     * Deletes a Project entity from the persistence layer by its ID.
     *
     * @param projectId The ID of the project to delete.
     * @return true if the project was successfully deleted, false otherwise (e.g., project not found).
     * @throws IllegalArgumentException if the projectId is null or empty.
     */
    boolean deleteById(String projectId);

    // Future potential methods for more specific queries
    // List<Project> findByStatus(Status status);
    // List<Project> findByNameContaining(String keyword);
}

// src/main/java/com/intelliTask/persistence/DataStorageManager.java

package persistence;

import com.google.gson.Gson;          // For JSON serialization/deserialization
import com.google.gson.GsonBuilder;  // For pretty printing JSON
import com.google.gson.reflect.TypeToken; // For handling generic types during deserialization

import model.*; // Import all models
import persistence.impl.*; // Import all file repository implementations

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the loading and saving of all application data to and from persistent storage (files).
 * This class acts as a central orchestrator for persistence, coordinating with
 * individual File-based repository implementations.
 *
 * It adheres to the Single Responsibility Principle (SRP) by centralizing
 * file I/O and serialization concerns, and works with the Dependency Inversion Principle (DIP)
 * by taking concrete FileRepository implementations as dependencies.
 */
public class DataStorageManager {

    // Define the file path for storing our data.
    // Ponder Point: In a production app, this path might be configurable
    // via a properties file or environment variable.
    private static final String DATA_FILE_NAME = "intelliTask_data.json";
    private final Path dataFilePath;

    // We depend on concrete FileRepository implementations here.
    // The DataStorageManager is a "low-level" component itself, specifically designed
    // to work with these file-based implementations.
    private final FileUserRepositoryImpl userRepository;
    private final FileTaskRepositoryImpl taskRepository;
    private final FileNoteRepositoryImpl noteRepository;
    private final FileProjectRepositoryImpl projectRepository;
    private final FileReminderRepositoryImpl reminderRepository;
    private final FileLabelRepositoryImpl labelRepository;

    // Gson instance for JSON serialization/deserialization.
    // `setPrettyPrinting()` makes the JSON output more human-readable.
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            // Ponder Point: If LocalDateTime serialization/deserialization
            // proves problematic with default Gson, a custom serializer/deserializer
            // for LocalDateTime might be needed here (e.g., using ISO 8601 format).
            .create();

    /**
     * Constructor for DataStorageManager.
     * It receives all the file-based repository implementations as dependencies.
     * This is a form of Manual Dependency Injection.
     *
     * @param userRepository The FileUserRepositoryImpl instance.
     * @param taskRepository The FileTaskRepositoryImpl instance.
     * @param noteRepository The FileNoteRepositoryImpl instance.
     * @param projectRepository The FileProjectRepositoryImpl instance.
     * @param reminderRepository The FileReminderRepositoryImpl instance.
     * @param labelRepository The FileLabelRepositoryImpl instance.
     * @throws IllegalArgumentException if any repository dependency is null.
     */
    public DataStorageManager(FileUserRepositoryImpl userRepository,
                              FileTaskRepositoryImpl taskRepository,
                              FileNoteRepositoryImpl noteRepository,
                              FileProjectRepositoryImpl projectRepository,
                              FileReminderRepositoryImpl reminderRepository,
                              FileLabelRepositoryImpl labelRepository) {
        // Essential validation for dependencies.
        if (userRepository == null || taskRepository == null || noteRepository == null ||
                projectRepository == null || reminderRepository == null || labelRepository == null) {
            throw new IllegalArgumentException("All repository implementations must be provided and cannot be null.");
        }

        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.noteRepository = noteRepository;
        this.projectRepository = projectRepository;
        this.reminderRepository = reminderRepository;
        this.labelRepository = labelRepository;

        // Ponder Point: Where should the data file be stored?
        // For a simple CLI, the current working directory or a dedicated 'data' folder
        // within the project is common. Using `Paths.get(DATA_FILE_NAME)` puts it in
        // the application's current working directory.
        this.dataFilePath = Paths.get(DATA_FILE_NAME);
    }

    /**
     * Loads all application data from the designated JSON file into the in-memory repositories.
     * This method orchestrates the deserialization process.
     *
     * Ponder Point (SDE 2): Error handling for file operations.
     * This method catches `IOException` and prints a message, which is fine for a CLI.
     * In a production system, you might:
     * - Log the error more formally.
     * - Throw a custom checked exception (e.g., `DataLoadException`) to force callers
     * to handle persistence errors.
     * - Implement retry mechanisms.
     */
    public void loadAllData() {
        if (!Files.exists(dataFilePath)) {
            System.out.println("Data file not found. Starting with empty data.");
            return; // No file to load, start fresh.
        }

        try (FileReader reader = new FileReader(dataFilePath.toFile())) {
            // Ponder Point (SDE 2): TypeToken for generic types during deserialization.
            // Gson needs a TypeToken to correctly deserialize generic types like Map<String, Task>.
            // Without it, Gson might deserialize them as Map<String, LinkedTreeMap> and lead to ClassCastExceptions.
            Type userMapType = new TypeToken<Map<String, User>>() {}.getType();
            Type taskMapType = new TypeToken<Map<String, Task>>() {}.getType();
            Type noteMapType = new TypeToken<Map<String, Note>>() {}.getType();
            Type projectMapType = new TypeToken<Map<String, Project>>() {}.getType();
            Type reminderMapType = new TypeToken<Map<String, Reminder>>() {}.getType();
            Type labelMapType = new TypeToken<Map<String, Label>>() {}.getType();


            // Read the entire JSON file content into a Map that holds maps for each entity type.
            // This structure aligns with how we'll store all data in the file.
            Type overallDataType = new TypeToken<Map<String, Map<String, ?>>>() {}.getType();
            Map<String, Map<String, ?>> allData = gson.fromJson(reader, overallDataType);

            if (allData != null) {
                // Safely cast and set the data for each repository.
                // We use gson.fromJson again for individual maps to ensure correct type mapping.
                // This is crucial because the outer deserialization might give LinkedTreeMaps.
                Map<String, User> loadedUsers = gson.fromJson(gson.toJson(allData.get("users")), userMapType);
                if (loadedUsers != null) userRepository.setUsers(loadedUsers);

                Map<String, Task> loadedTasks = gson.fromJson(gson.toJson(allData.get("tasks")), taskMapType);
                if (loadedTasks != null) taskRepository.setTasks(loadedTasks);

                Map<String, Note> loadedNotes = gson.fromJson(gson.toJson(allData.get("notes")), noteMapType);
                if (loadedNotes != null) noteRepository.setNotes(loadedNotes);

                Map<String, Project> loadedProjects = gson.fromJson(gson.toJson(allData.get("projects")), projectMapType);
                if (loadedProjects != null) projectRepository.setProjects(loadedProjects);

                Map<String, Reminder> loadedReminders = gson.fromJson(gson.toJson(allData.get("reminders")), reminderMapType);
                if (loadedReminders != null) reminderRepository.setReminders(loadedReminders);

                Map<String, Label> loadedLabels = gson.fromJson(gson.toJson(allData.get("labels")), labelMapType);
                if (loadedLabels != null) labelRepository.setLabels(loadedLabels);

                System.out.println("Data loaded successfully from " + DATA_FILE_NAME);
            } else {
                System.out.println("Data file is empty or corrupted. Starting with empty data.");
            }

        } catch (IOException e) {
            System.err.println("Error loading data from file: " + e.getMessage());
            // Ponder Point: In a production app, logging the full stack trace would be better.
            // e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error processing loaded data: " + e.getMessage());
            // e.printStackTrace();
        }
    }

    /**
     * Saves all application data from the in-memory repositories to the JSON file.
     * This method orchestrates the serialization process.
     *
     * Ponder Point (SDE 2): Atomicity and Data Consistency.
     * For critical data, you might implement atomic file writes:
     * 1. Write to a temporary file.
     * 2. If successful, rename the temporary file to the final file, overwriting it.
     * This prevents data loss or corruption if the application crashes during write.
     */
    public void saveAllData() {
        // Collect all data from each repository's unmodifiable map.
        // Ponder Point: We are using the `get*Map()` methods that return `Collections.unmodifiableMap()`.
        // This is a defensive programming measure. It ensures we can read the data for serialization
        // but cannot accidentally modify the live in-memory collections of the repositories.
        Map<String, Map<String, ?>> allData = new HashMap<>();
        allData.put("users", userRepository.getUsersMap());
        allData.put("tasks", taskRepository.getTasksMap());
        allData.put("notes", noteRepository.getNotesMap());
        allData.put("projects", projectRepository.getProjectsMap());
        allData.put("reminders", reminderRepository.getRemindersMap());
        allData.put("labels", labelRepository.getLabelsMap());

        try (FileWriter writer = new FileWriter(dataFilePath.toFile())) {
            // Serialize the entire data structure to JSON and write to file.
            gson.toJson(allData, writer);
            System.out.println("Data saved successfully to " + DATA_FILE_NAME);
        } catch (IOException e) {
            System.err.println("Error saving data to file: " + e.getMessage());
            // e.printStackTrace();
        }
    }
}

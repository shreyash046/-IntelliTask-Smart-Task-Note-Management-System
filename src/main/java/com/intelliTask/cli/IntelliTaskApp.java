// src/main/java/com/intelliTask/cli/IntelliTaskApp.java

package cli;

import model.*; // Import all models
import model.enums.Priority;
import model.enums.Status;
import service.*; // Import all service interfaces
import service.impl.*; // Import all service implementations
import persistence.DataStorageManager;
import persistence.impl.*; // Import all file repository implementations
import util.IdGenerator; // Not directly used here, but good for context if creating on the fly

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors; // For various filtering/mapping operations

/**
 * The main entry point for the intelliTask Command-Line Interface (CLI) application.
 * This class handles user input, command parsing, orchestrates calls to the service layer,
 * and manages data persistence via the DataStorageManager.
 *
 * It demonstrates Manual Dependency Injection for all service and repository components.
 *
 * Ponder Point (SDE 2): For a more complex CLI, consider using a dedicated CLI framework
 * like Picocli or JCommander to simplify command parsing, validation, and help generation.
 * For this phase, `Scanner` and manual parsing suffice.
 */
public class IntelliTaskApp {

    // Declare instances of all our Service interfaces.
    // These will be manually injected in the main method.
    private final UserService userService;
    private final TaskService taskService;
    private final NoteService noteService;
    private final ProjectService projectService;
    private final ReminderService reminderService;
    private final LabelService labelService;
    private final DataStorageManager dataStorageManager;

    // Use a Scanner for reading user input from the console.
    private final Scanner scanner;

    // Current user context (for future multi-user, currently single-user focus)
    private User currentUser; // In this phase, we'll create a default user if none exists.

    /**
     * Constructor for IntelliTaskApp.
     * Performs Manual Dependency Injection of all required service and data management components.
     *
     * @param userService The UserService implementation.
     * @param taskService The TaskService implementation.
     * @param noteService The NoteService implementation.
     * @param projectService The ProjectService implementation.
     * @param reminderService The ReminderService implementation.
     * @param labelService The LabelService implementation.
     * @param dataStorageManager The DataStorageManager instance.
     */
    public IntelliTaskApp(UserService userService, TaskService taskService,
                          NoteService noteService, ProjectService projectService,
                          ReminderService reminderService, LabelService labelService,
                          DataStorageManager dataStorageManager) {
        // Validate all injected dependencies are not null.
        if (userService == null || taskService == null || noteService == null ||
                projectService == null || reminderService == null || labelService == null ||
                dataStorageManager == null) {
            throw new IllegalArgumentException("All service and data manager dependencies must be provided and cannot be null.");
        }

        this.userService = userService;
        this.taskService = taskService;
        this.noteService = noteService;
        this.projectService = projectService;
        this.reminderService = reminderService;
        this.labelService = labelService;
        this.dataStorageManager = dataStorageManager;
        this.scanner = new Scanner(System.in);
    }

    /**
     * The main method and entry point of the intelliTask CLI application.
     * This is where all components are initialized and dependency injection is performed manually.
     *
     * @param args Command-line arguments (not used in this CLI).
     */
    public static void main(String[] args) {
        // --- Manual Dependency Injection Setup ---
        // Ponder Point (SDE 2): This section clearly shows the value of Spring Boot later.
        // Spring's IoC container would automate this entire setup with annotations.
        FileUserRepositoryImpl fileUserRepository = new FileUserRepositoryImpl();
        FileTaskRepositoryImpl fileTaskRepository = new FileTaskRepositoryImpl();
        FileNoteRepositoryImpl fileNoteRepository = new FileNoteRepositoryImpl();
        FileProjectRepositoryImpl fileProjectRepository = new FileProjectRepositoryImpl();
        FileReminderRepositoryImpl fileReminderRepository = new FileReminderRepositoryImpl();
        FileLabelRepositoryImpl fileLabelRepository = new FileLabelRepositoryImpl();

        // DataStorageManager takes concrete repository implementations
        DataStorageManager dataManager = new DataStorageManager(
                fileUserRepository, fileTaskRepository, fileNoteRepository,
                fileProjectRepository, fileReminderRepository, fileLabelRepository
        );

        // Service implementations take their respective repository interfaces
        // and other service interfaces as dependencies.
        UserService userService = new UserServiceImpl(fileUserRepository);
        TaskService taskService = new TaskServiceImpl(fileTaskRepository);
        NoteService noteService = new NoteServiceImpl(fileNoteRepository);
        ProjectService projectService = new ProjectServiceImpl(fileProjectRepository, taskService); // ProjectService depends on TaskService
        ReminderService reminderService = new ReminderServiceImpl(fileReminderRepository, taskService, noteService); // ReminderService depends on Task/Note Services
        LabelService labelService = new LabelServiceImpl(fileLabelRepository, taskService, noteService); // LabelService depends on Task/Note Services

        // Instantiate the main application class with all services and the data manager.
        IntelliTaskApp app = new IntelliTaskApp(
                userService, taskService, noteService, projectService,
                reminderService, labelService, dataManager
        );

        app.run(); // Start the application loop
    }

    /**
     * Runs the main application loop, handling user commands.
     * This method loads data at startup and saves data at shutdown.
     */
    public void run() {
        System.out.println("Welcome to intelliTask CLI!");

        // Load data at application startup
        dataStorageManager.loadAllData();

        // Ensure a user exists (for single-user CLI)
        initializeUser();

        displayHelp(); // Show available commands initially

        // Main command loop
        while (true) {
            System.out.print("\nintelliTask> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting intelliTask. Goodbye!");
                break; // Exit loop
            }

            // Ponder Point (SDE 2): Command Parsing.
            // For complex commands, a dedicated parser (e.g., regex, CLI library) is better.
            // For now, simple split based on space.
            String[] parts = input.split(" ", 2); // Split into command and argument part
            String command = parts[0].toLowerCase();
            String args = parts.length > 1 ? parts[1] : "";

            try {
                // Delegate to command-specific handler methods.
                // This keeps the switch statement clean and logic modular.
                switch (command) {
                    case "help":
                        displayHelp();
                        break;
                    case "addtask":
                        handleAddUpdateTask(args, null); // Null for create
                        break;
                    case "listtasks":
                        handleListTasks(args);
                        break;
                    case "updatetask":
                        handleAddUpdateTask(args, "update"); // Signal update operation
                        break;
                    case "deletetask":
                        handleDeleteTask(args);
                        break;
                    case "addnote":
                        handleAddUpdateNote(args, null);
                        break;
                    case "listnotes":
                        handleListNotes();
                        break;
                    case "updatenote":
                        handleAddUpdateNote(args, "update");
                        break;
                    case "deletenote":
                        handleDeleteNote(args);
                        break;
                    case "addproject":
                        handleAddUpdateProject(args, null);
                        break;
                    case "listprojects":
                        handleListProjects();
                        break;
                    case "updateproject":
                        handleAddUpdateProject(args, "update");
                        break;
                    case "deleteproject":
                        handleDeleteProject(args);
                        break;
                    case "addreminder":
                        handleAddReminder(args);
                        break;
                    case "listreminders":
                        handleListReminders(args);
                        break;
                    case "dismissreminder":
                        handleDismissReminder(args);
                        break;
                    case "deletereminder":
                        handleDeleteReminder(args);
                        break;
                    case "addlabel":
                        handleAddUpdateLabel(args, null);
                        break;
                    case "listlabels":
                        handleListLabels();
                        break;
                    case "updatelabel":
                        handleAddUpdateLabel(args, "update");
                        break;
                    case "deletelabel":
                        handleDeleteLabel(args);
                        break;
                    case "associate":
                        handleAssociate(args);
                        break;
                    case "disassociate":
                        handleDisassociate(args);
                        break;
                    case "gettasksbylabel":
                        handleGetTasksByLabel(args);
                        break;
                    case "getnotesbylabel":
                        handleGetNotesByLabel(args);
                        break;
                    case "updateuser":
                        handleUpdateUser(args);
                        break;
                    case "whoami":
                        System.out.println("You are: " + currentUser.getUsername() + " (ID: " + currentUser.getId() + ")");
                        break;
                    default:
                        System.out.println("Unknown command: '" + command + "'. Type 'help' for available commands.");
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Input Error: " + e.getMessage());
            } catch (RuntimeException e) { // Catching our custom (placeholder) service exceptions
                System.err.println("Application Error: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("An unexpected error occurred: " + e.getMessage());
                // Ponder Point (SDE 2): Log full stack trace for debugging in production.
                // e.printStackTrace();
            }
        }
        scanner.close(); // Close scanner resource
        // Save data at application shutdown
        dataStorageManager.saveAllData();
    }

    /**
     * Initializes a default user if no users exist in the system.
     * This is for the single-user CLI context.
     * Ponder Point: In a multi-user app, this would be a login/registration flow.
     */
    private void initializeUser() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No user found. Creating a default user 'cli_user'.");
            currentUser = userService.createUser("cli_user", "cli_user@intelliTask.com");
            System.out.println("Default user created: " + currentUser.getUsername());
        } else {
            currentUser = users.get(0); // Take the first user as the current user
            System.out.println("Logged in as: " + currentUser.getUsername());
        }
    }


    // --- COMMAND HANDLER METHODS ---
    // These methods encapsulate the logic for each CLI command.

    private void displayHelp() {
        System.out.println("\n--- intelliTask Commands ---");
        System.out.println("General:");
        System.out.println("  help                               - Display this help message.");
        System.out.println("  exit                               - Exit the application.");
        System.out.println("  whoami                             - Show current user info.");
        System.out.println("  updateuser <new_username> <new_email> - Update current user's details.");
        System.out.println("Tasks:");
        System.out.println("  addtask <description> [priority]   - Add a new task (priority: HIGH, MEDIUM, LOW, NONE).");
        System.out.println("  listtasks [priority|status]        - List all tasks, optionally filtered by priority or status.");
        System.out.println("  updatetask <id> <new_description>  - Update task description.");
        System.out.println("  updatetask <id> priority <new_priority> - Update task priority.");
        System.out.println("  updatetask <id> status <new_status> - Update task status (PENDING, IN_PROGRESS, COMPLETED, CANCELLED).");
        System.out.println("  updatetask <id> completed <true|false> - Mark task as completed/uncompleted.");
        System.out.println("  deletetask <id>                    - Delete a task.");
        System.out.println("Notes:");
        System.out.println("  addnote <title> <content>          - Add a new note.");
        System.out.println("  listnotes                          - List all notes.");
        System.out.println("  updatenote <id> title <new_title>  - Update note title.");
        System.out.println("  updatenote <id> content <new_content> - Update note content.");
        System.out.println("  deletenote <id>                    - Delete a note.");
        System.out.println("Projects:");
        System.out.println("  addproject <name> <description>    - Add a new project.");
        System.out.println("  listprojects                       - List all projects.");
        System.out.println("  updateproject <id> name <new_name> - Update project name.");
        System.out.println("  updateproject <id> description <new_description> - Update project description.");
        System.out.println("  updateproject <id> status <new_status> - Update project status.");
        System.out.println("  deleteproject <id>                 - Delete a project.");
        System.out.println("Reminders:");
        System.out.println("  addreminder <message> <yyyy-MM-dd HH:mm> <entity_type> <entity_id> - Add reminder (entity_type: Task/Note).");
        System.out.println("  listreminders [due]                - List all reminders, optionally 'due' (past/present & not dismissed).");
        System.out.println("  dismissreminder <id>               - Mark a reminder as dismissed.");
        System.out.println("  deletereminder <id>                - Delete a reminder.");
        System.out.println("Labels:");
        System.out.println("  addlabel <name>                    - Add a new label.");
        System.out.println("  listlabels                         - List all labels.");
        System.out.println("  updatelabel <id> <new_name>        - Update label name.");
        System.out.println("  deletelabel <id>                   - Delete a label.");
        System.out.println("Associations:");
        System.out.println("  associate task <task_id> label <label_id> - Associate label with task.");
        System.out.println("  associate note <note_id> label <label_id> - Associate label with note.");
        System.out.println("  associate project <project_id> task <task_id> - Associate task with project.");
        System.out.println("  disassociate task <task_id> label <label_id> - Disassociate label from task.");
        System.out.println("  disassociate note <note_id> label <label_id> - Disassociate label from note.");
        System.out.println("  disassociate project <project_id> task <task_id> - Disassociate task from project.");
        System.out.println("  gettasksbylabel <label_id>         - List tasks by label.");
        System.out.println("  getnotesbylabel <label_id>         - List notes by label.");
    }

    /**
     * Handles adding or updating a task based on provided arguments.
     * Uses regex for robust argument parsing.
     * Ponder Point (SDE 2): This method is getting complex. A dedicated command parsing
     * library would simplify this significantly by automatically mapping arguments.
     */
    private void handleAddUpdateTask(String args, String operationType) {
        if (operationType == null) { // Add Task
            // addtask <description> [priority]
            String[] parts = args.split(" ", 2);
            String description = parts[0]; // First part is always description (or start of it)
            String priorityStr = (parts.length > 1) ? parts[1].toUpperCase() : Priority.NONE.name();

            // Handle multi-word descriptions
            if (parts.length > 1) { // If there are more parts, the last part might be priority
                try {
                    Priority priorityCheck = Priority.valueOf(priorityStr);
                    description = parts[0]; // If it's a valid priority, description is just the first part
                } catch (IllegalArgumentException e) {
                    // If the last part is NOT a valid priority, it's part of the description
                    description = args; // The entire arg string is the description
                    priorityStr = Priority.NONE.name(); // Default priority
                }
            } else {
                description = args; // Only one part, it's the description
            }

            try {
                Priority priority = Priority.valueOf(priorityStr);
                Task newTask = taskService.createTask(description, priority);
                System.out.println("Task added: " + newTask);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid priority specified. Valid options: HIGH, MEDIUM, LOW, NONE.");
            }
        } else if ("update".equals(operationType)) { // Update Task
            // updatetask <id> <field> <value>
            String[] updateParts = args.split(" ", 3); // id, field, value
            if (updateParts.length < 3) {
                System.out.println("Usage: updatetask <id> <field> <value>");
                System.out.println("Fields: description, priority, status, completed");
                return;
            }

            String id = updateParts[0];
            String field = updateParts[1].toLowerCase();
            String value = updateParts[2];

            try {
                switch (field) {
                    case "description":
                        Task updatedDescTask = taskService.updateTaskDescription(id, value);
                        System.out.println("Task description updated: " + updatedDescTask);
                        break;
                    case "priority":
                        Priority newPriority = Priority.valueOf(value.toUpperCase());
                        Task updatedPriorityTask = taskService.updateTaskPriority(id, newPriority);
                        System.out.println("Task priority updated: " + updatedPriorityTask);
                        break;
                    case "status":
                        Status newStatus = Status.valueOf(value.toUpperCase());
                        Task updatedStatusTask = taskService.updateTaskStatus(id, newStatus);
                        System.out.println("Task status updated: " + updatedStatusTask);
                        break;
                    case "completed":
                        boolean completed = Boolean.parseBoolean(value);
                        Task updatedCompletedTask = taskService.markTaskCompleted(id, completed);
                        System.out.println("Task completed status updated: " + updatedCompletedTask);
                        break;
                    default:
                        System.out.println("Invalid task field for update: " + field);
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Error updating task: " + e.getMessage());
            }
        }
    }

    /**
     * Handles listing tasks, optionally filtered by priority or status.
     * @param args "priority <level>" or "status <type>" or empty.
     */
    private void handleListTasks(String args) {
        List<Task> tasks;
        if (args.isEmpty()) {
            tasks = taskService.getAllTasks();
            System.out.println("\n--- All Tasks ---");
        } else {
            String[] filterParts = args.split(" ", 2);
            if (filterParts.length < 2) {
                System.out.println("Usage: listtasks [priority <level>|status <type>]");
                return;
            }
            String filterType = filterParts[0].toLowerCase();
            String filterValue = filterParts[1];

            try {
                if ("priority".equals(filterType)) {
                    Priority priority = Priority.valueOf(filterValue.toUpperCase());
                    tasks = taskService.getTasksByPriority(priority);
                    System.out.println("\n--- Tasks by Priority: " + priority + " ---");
                } else if ("status".equals(filterType)) {
                    Status status = Status.valueOf(filterValue.toUpperCase());
                    tasks = taskService.getTasksByStatus(status);
                    System.out.println("\n--- Tasks by Status: " + status + " ---");
                } else {
                    System.out.println("Invalid filter type. Use 'priority' or 'status'.");
                    return;
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid filter value: " + e.getMessage());
                return;
            }
        }

        if (tasks.isEmpty()) {
            System.out.println("No tasks found matching criteria.");
        } else {
            tasks.forEach(System.out::println);
        }
    }

    /**
     * Handles deleting a task.
     * @param args The task ID to delete.
     */
    private void handleDeleteTask(String args) {
        String taskId = args.trim();
        if (taskId.isEmpty()) {
            System.out.println("Usage: deletetask <id>");
            return;
        }
        if (taskService.deleteTask(taskId)) {
            System.out.println("Task with ID " + taskId + " deleted successfully.");
        } else {
            System.out.println("Task with ID " + taskId + " not found or could not be deleted.");
        }
    }

    /**
     * Handles adding or updating a note.
     * Add: addnote <title> <content>
     * Update: updatenote <id> title <new_title>
     * Update: updatenote <id> content <new_content>
     */
    private void handleAddUpdateNote(String args, String operationType) {
        if (operationType == null) { // Add Note
            // addnote <title> <content>
            String[] parts = args.split(" ", 2); // title, content
            if (parts.length < 2) {
                System.out.println("Usage: addnote <title> <content>");
                return;
            }
            String title = parts[0];
            String content = parts[1];
            Note newNote = noteService.createNote(title, content);
            System.out.println("Note added: " + newNote);
        } else if ("update".equals(operationType)) { // Update Note
            // updatenote <id> <field> <value>
            String[] updateParts = args.split(" ", 3); // id, field, value
            if (updateParts.length < 3) {
                System.out.println("Usage: updatenote <id> <field> <value>");
                System.out.println("Fields: title, content");
                return;
            }
            String id = updateParts[0];
            String field = updateParts[1].toLowerCase();
            String value = updateParts[2];

            try {
                switch (field) {
                    case "title":
                        Note updatedTitleNote = noteService.updateNoteTitle(id, value);
                        System.out.println("Note title updated: " + updatedTitleNote);
                        break;
                    case "content":
                        Note updatedContentNote = noteService.updateNoteContent(id, value);
                        System.out.println("Note content updated: " + updatedContentNote);
                        break;
                    default:
                        System.out.println("Invalid note field for update: " + field);
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Error updating note: " + e.getMessage());
            }
        }
    }

    /**
     * Handles listing all notes.
     */
    private void handleListNotes() {
        List<Note> notes = noteService.getAllNotes();
        if (notes.isEmpty()) {
            System.out.println("No notes found.");
        } else {
            System.out.println("\n--- All Notes ---");
            notes.forEach(System.out::println);
        }
    }

    /**
     * Handles deleting a note.
     * @param args The note ID to delete.
     */
    private void handleDeleteNote(String args) {
        String noteId = args.trim();
        if (noteId.isEmpty()) {
            System.out.println("Usage: deletenote <id>");
            return;
        }
        if (noteService.deleteNote(noteId)) {
            System.out.println("Note with ID " + noteId + " deleted successfully.");
        } else {
            System.out.println("Note with ID " + noteId + " not found or could not be deleted.");
        }
    }

    /**
     * Handles adding or updating a project.
     * Add: addproject <name> <description>
     * Update: updateproject <id> name <new_name>
     * Update: updateproject <id> description <new_description>
     * Update: updateproject <id> status <new_status>
     */
    private void handleAddUpdateProject(String args, String operationType) {
        if (operationType == null) { // Add Project
            // addproject <name> <description>
            String[] parts = args.split(" ", 2); // name, description
            if (parts.length < 2) {
                System.out.println("Usage: addproject <name> <description>");
                return;
            }
            String name = parts[0];
            String description = parts[1];
            Project newProject = projectService.createProject(name, description);
            System.out.println("Project added: " + newProject);
        } else if ("update".equals(operationType)) { // Update Project
            // updateproject <id> <field> <value>
            String[] updateParts = args.split(" ", 3); // id, field, value
            if (updateParts.length < 3) {
                System.out.println("Usage: updateproject <id> <field> <value>");
                System.out.println("Fields: name, description, status");
                return;
            }
            String id = updateParts[0];
            String field = updateParts[1].toLowerCase();
            String value = updateParts[2];

            try {
                switch (field) {
                    case "name":
                        Project updatedNameProject = projectService.updateProjectName(id, value);
                        System.out.println("Project name updated: " + updatedNameProject);
                        break;
                    case "description":
                        Project updatedDescProject = projectService.updateProjectDescription(id, value);
                        System.out.println("Project description updated: " + updatedDescProject);
                        break;
                    case "status":
                        Status newStatus = Status.valueOf(value.toUpperCase());
                        Project updatedStatusProject = projectService.updateProjectStatus(id, newStatus);
                        System.out.println("Project status updated: " + updatedStatusProject);
                        break;
                    default:
                        System.out.println("Invalid project field for update: " + field);
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Error updating project: " + e.getMessage());
            }
        }
    }

    /**
     * Handles listing all projects.
     */
    private void handleListProjects() {
        List<Project> projects = projectService.getAllProjects();
        if (projects.isEmpty()) {
            System.out.println("No projects found.");
        } else {
            System.out.println("\n--- All Projects ---");
            projects.forEach(System.out::println);
        }
    }

    /**
     * Handles deleting a project.
     * @param args The project ID to delete.
     */
    private void handleDeleteProject(String args) {
        String projectId = args.trim();
        if (projectId.isEmpty()) {
            System.out.println("Usage: deleteproject <id>");
            return;
        }
        if (projectService.deleteProject(projectId)) {
            System.out.println("Project with ID " + projectId + " deleted successfully.");
        } else {
            System.out.println("Project with ID " + projectId + " not found or could not be deleted.");
        }
    }

    /**
     * Handles adding a reminder.
     * Usage: addreminder <message> <yyyy-MM-dd HH:mm> <entity_type> <entity_id>
     */
    private void handleAddReminder(String args) {
        String[] parts = args.split(" ", 4); // message, datetime, type, id
        if (parts.length < 4) {
            System.out.println("Usage: addreminder <message> <yyyy-MM-dd HH:mm> <entity_type> <entity_id>");
            System.out.println("Example: addreminder \"Meeting\" 2025-06-20 10:30 Task task123");
            return;
        }

        String message = parts[0];
        String dateTimeStr = parts[1] + " " + parts[2]; // Combine date and time
        String entityType = parts[3];
        String entityId = parts[4]; // This part will hold the entity ID

        try {
            // Need to parse LocalDateTime
            LocalDateTime reminderTime = LocalDateTime.parse(dateTimeStr, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            Reminder newReminder = reminderService.createReminder(message, reminderTime, entityId, entityType);
            System.out.println("Reminder added: " + newReminder);
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date/time format. Please use yyyy-MM-dd HH:mm (e.g., 2025-06-20 10:30).");
        } catch (RuntimeException e) {
            System.err.println("Error adding reminder: " + e.getMessage());
        }
    }

    /**
     * Handles listing reminders, optionally filtered by 'due'.
     * @param args "due" or empty.
     */
    private void handleListReminders(String args) {
        List<Reminder> reminders;
        if (args.equalsIgnoreCase("due")) {
            reminders = reminderService.getDueReminders(LocalDateTime.now());
            System.out.println("\n--- Due & Active Reminders ---");
        } else if (args.isEmpty()) {
            reminders = reminderService.getAllReminders();
            System.out.println("\n--- All Reminders ---");
        } else {
            System.out.println("Usage: listreminders [due]");
            return;
        }

        if (reminders.isEmpty()) {
            System.out.println("No reminders found matching criteria.");
        } else {
            reminders.forEach(System.out::println);
        }
    }

    /**
     * Handles dismissing a reminder.
     * @param args The reminder ID to dismiss.
     */
    private void handleDismissReminder(String args) {
        String reminderId = args.trim();
        if (reminderId.isEmpty()) {
            System.out.println("Usage: dismissreminder <id>");
            return;
        }
        try {
            Reminder dismissedReminder = reminderService.dismissReminder(reminderId);
            System.out.println("Reminder dismissed: " + dismissedReminder);
        } catch (RuntimeException e) {
            System.err.println("Error dismissing reminder: " + e.getMessage());
        }
    }

    /**
     * Handles deleting a reminder.
     * @param args The reminder ID to delete.
     */
    private void handleDeleteReminder(String args) {
        String reminderId = args.trim();
        if (reminderId.isEmpty()) {
            System.out.println("Usage: deletereminder <id>");
            return;
        }
        if (reminderService.deleteReminder(reminderId)) {
            System.out.println("Reminder with ID " + reminderId + " deleted successfully.");
        } else {
            System.out.println("Reminder with ID " + reminderId + " not found or could not be deleted.");
        }
    }

    /**
     * Handles adding or updating a label.
     * Add: addlabel <name>
     * Update: updatelabel <id> <new_name>
     */
    private void handleAddUpdateLabel(String args, String operationType) {
        if (operationType == null) { // Add Label
            // addlabel <name>
            if (args.isEmpty()) {
                System.out.println("Usage: addlabel <name>");
                return;
            }
            try {
                Label newLabel = labelService.createLabel(args);
                System.out.println("Label added: " + newLabel);
            } catch (IllegalArgumentException e) {
                System.err.println("Error adding label: " + e.getMessage());
            }
        } else if ("update".equals(operationType)) { // Update Label
            // updatelabel <id> <new_name>
            String[] updateParts = args.split(" ", 2); // id, new_name
            if (updateParts.length < 2) {
                System.out.println("Usage: updatelabel <id> <new_name>");
                return;
            }
            String id = updateParts[0];
            String newName = updateParts[1];
            try {
                Label updatedLabel = labelService.updateLabelName(id, newName);
                System.out.println("Label name updated: " + updatedLabel);
            } catch (RuntimeException e) {
                System.err.println("Error updating label: " + e.getMessage());
            }
        }
    }

    /**
     * Handles listing all labels.
     */
    private void handleListLabels() {
        List<Label> labels = labelService.getAllLabels();
        if (labels.isEmpty()) {
            System.out.println("No labels found.");
        } else {
            System.out.println("\n--- All Labels ---");
            labels.forEach(System.out::println);
        }
    }

    /**
     * Handles deleting a label.
     * @param args The label ID to delete.
     */
    private void handleDeleteLabel(String args) {
        String labelId = args.trim();
        if (labelId.isEmpty()) {
            System.out.println("Usage: deletelabel <id>");
            return;
        }
        if (labelService.deleteLabel(labelId)) {
            System.out.println("Label with ID " + labelId + " deleted successfully. Associated tasks/notes might still reference it.");
        } else {
            System.out.println("Label with ID " + labelId + " not found or could not be deleted.");
        }
    }

    /**
     * Handles associating entities (tasks with labels, notes with labels, tasks with projects).
     * Usage: associate <entity_type_1> <id_1> <entity_type_2> <id_2>
     * Examples:
     * associate task <task_id> label <label_id>
     * associate note <note_id> label <label_id>
     * associate project <project_id> task <task_id>
     */
    private void handleAssociate(String args) {
        String[] parts = args.split(" ");
        if (parts.length != 4) {
            System.out.println("Usage: associate <entity_type_1> <id_1> <entity_type_2> <id_2>");
            System.out.println("Examples:");
            System.out.println("  associate task <task_id> label <label_id>");
            System.out.println("  associate note <note_id> label <label_id>");
            System.out.println("  associate project <project_id> task <task_id>");
            return;
        }

        String type1 = parts[0].toLowerCase();
        String id1 = parts[1];
        String type2 = parts[2].toLowerCase();
        String id2 = parts[3];

        try {
            if ("task".equals(type1) && "label".equals(type2)) {
                Task updatedTask = labelService.associateLabelWithTask(id2, id1);
                System.out.println("Label " + id2 + " associated with Task " + updatedTask.getId());
            } else if ("note".equals(type1) && "label".equals(type2)) {
                Note updatedNote = labelService.associateLabelWithNote(id2, id1);
                System.out.println("Label " + id2 + " associated with Note " + updatedNote.getId());
            } else if ("project".equals(type1) && "task".equals(type2)) {
                Project updatedProject = projectService.addTaskToProject(id1, id2);
                System.out.println("Task " + id2 + " associated with Project " + updatedProject.getId());
            } else {
                System.out.println("Unsupported association type. See 'help' for usage.");
            }
        } catch (RuntimeException e) {
            System.err.println("Error associating: " + e.getMessage());
        }
    }

    /**
     * Handles disassociating entities.
     * Usage: disassociate <entity_type_1> <id_1> <entity_type_2> <id_2>
     * Examples:
     * disassociate task <task_id> label <label_id>
     * disassociate note <note_id> label <label_id>
     * disassociate project <project_id> task <task_id>
     */
    private void handleDisassociate(String args) {
        String[] parts = args.split(" ");
        if (parts.length != 4) {
            System.out.println("Usage: disassociate <entity_type_1> <id_1> <entity_type_2> <id_2>");
            System.out.println("Examples:");
            System.out.println("  disassociate task <task_id> label <label_id>");
            System.out.println("  disassociate note <note_id> label <label_id>");
            System.out.println("  disassociate project <project_id> task <task_id>");
            return;
        }

        String type1 = parts[0].toLowerCase();
        String id1 = parts[1];
        String type2 = parts[2].toLowerCase();
        String id2 = parts[3];

        try {
            if ("task".equals(type1) && "label".equals(type2)) {
                Task updatedTask = labelService.disassociateLabelFromTask(id2, id1);
                System.out.println("Label " + id2 + " disassociated from Task " + updatedTask.getId());
            } else if ("note".equals(type1) && "label".equals(type2)) {
                Note updatedNote = labelService.disassociateLabelFromNote(id2, id1);
                System.out.println("Label " + id2 + " disassociated from Note " + updatedNote.getId());
            } else if ("project".equals(type1) && "task".equals(type2)) {
                Project updatedProject = projectService.removeTaskFromProject(id1, id2);
                System.out.println("Task " + id2 + " disassociated from Project " + updatedProject.getId());
            } else {
                System.out.println("Unsupported disassociation type. See 'help' for usage.");
            }
        } catch (RuntimeException e) {
            System.err.println("Error disassociating: " + e.getMessage());
        }
    }

    /**
     * Handles listing tasks associated with a specific label.
     * Usage: gettasksbylabel <label_id>
     */
    private void handleGetTasksByLabel(String args) {
        String labelId = args.trim();
        if (labelId.isEmpty()) {
            System.out.println("Usage: gettasksbylabel <label_id>");
            return;
        }
        try {
            List<Task> tasks = labelService.getTasksByLabel(labelId);
            if (tasks.isEmpty()) {
                System.out.println("No tasks found with label ID: " + labelId);
            } else {
                System.out.println("\n--- Tasks with Label ID: " + labelId + " ---");
                tasks.forEach(System.out::println);
            }
        } catch (RuntimeException e) {
            System.err.println("Error getting tasks by label: " + e.getMessage());
        }
    }

    /**
     * Handles listing notes associated with a specific label.
     * Usage: getnotesbylabel <label_id>
     */
    private void handleGetNotesByLabel(String args) {
        String labelId = args.trim();
        if (labelId.isEmpty()) {
            System.out.println("Usage: getnotesbylabel <label_id>");
            return;
        }
        try {
            List<Note> notes = labelService.getNotesByLabel(labelId);
            if (notes.isEmpty()) {
                System.out.println("No notes found with label ID: " + labelId);
            } else {
                System.out.println("\n--- Notes with Label ID: " + labelId + " ---");
                notes.forEach(System.out::println);
            }
        } catch (RuntimeException e) {
            System.err.println("Error getting notes by label: " + e.getMessage());
        }
    }

    /**
     * Handles updating the current user's username and email.
     * Usage: updateuser <new_username> <new_email>
     */
    private void handleUpdateUser(String args) {
        String[] parts = args.split(" ", 2); // new_username, new_email
        if (parts.length < 2) {
            System.out.println("Usage: updateuser <new_username> <new_email>");
            return;
        }
        String newUsername = parts[0];
        String newEmail = parts[1];

        try {
            // Ponder Point: In a multi-user context, you'd update the specific user.
            // Here, we update the current single user.
            currentUser = userService.updateUsername(currentUser.getId(), newUsername);
            currentUser = userService.updateEmail(currentUser.getId(), newEmail); // Update email separately
            System.out.println("User updated to: " + currentUser);
        } catch (RuntimeException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
    }
}

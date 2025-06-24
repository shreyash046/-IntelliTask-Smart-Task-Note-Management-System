package util;

import java.util.UUID; // Import the UUID class for generating unique identifiers

/**
 * A utility class responsible for generating unique identifiers (IDs)
 * for various entities within the intelliTask system.
 *
 * This class adheres to the Single Responsibility Principle (SRP) by centralizing
 * the ID generation logic. This makes it easy to change the ID generation strategy
 * (e.g., from UUIDs to sequence numbers or a custom format) in the future
 * without affecting other parts of the application that consume these IDs.
 *
 * All methods in this class are static, as it provides a stateless utility function.
 */
public class IdGenerator {

    /**
     * Generates a universally unique identifier (UUID) as a String.
     * UUIDs are 128-bit values that are globally unique.
     *
     * @return A unique String ID.
     */
    public static String generateUniqueId() {
        // UUID.randomUUID() generates a Type 4 (randomly generated) UUID.
        // .toString() converts the UUID object into its standard string representation.
        return UUID.randomUUID().toString();
    }

    // Future potential methods:
    // public static String generateSequentialId() { ... }
    // public static String generateTimestampId() { ... }
}
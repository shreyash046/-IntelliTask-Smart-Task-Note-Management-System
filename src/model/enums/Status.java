package model.enums;

public enum Status {
    PENDING,        // Item has not yet started
    IN_PROGRESS,    // Item is currently being worked on
    COMPLETED,      // Item has been successfully finished
    CANCELLED       // Item has been cancelled and will not be completed
}

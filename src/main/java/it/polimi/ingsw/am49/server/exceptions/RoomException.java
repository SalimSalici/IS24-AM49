package it.polimi.ingsw.am49.server.exceptions;

/**
 * Exception thrown when there is an issue related to room operations in the game server.
 * This can include errors such as trying to join a full room, accessing a non-existent room, etc.
 */
public class RoomException extends Exception {
    /**
     * Constructs a new RoomException with the specified detail message.
     * 
     * @param message the detail message.
     */
    public RoomException(String message) {
        super(message);
    }
}


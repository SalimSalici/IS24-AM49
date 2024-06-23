package it.polimi.ingsw.am49.server.exceptions;

/**
 * Exception thrown when an error occurs during the process of joining a room.
 */
public class JoinRoomException extends Exception {
    /**
     * Constructs a new JoinRoomException with the specified detail message.
     * 
     * @param message the detail message.
     */
    public JoinRoomException(String message) {
        super(message);
    }
}

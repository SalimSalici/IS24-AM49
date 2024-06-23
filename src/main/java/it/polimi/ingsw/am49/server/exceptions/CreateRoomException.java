package it.polimi.ingsw.am49.server.exceptions;

/**
 * Exception thrown when there is an error creating a game room.
 */
public class CreateRoomException extends Exception {
    /**
     * Constructs a new CreateRoomException with the specified detail message.
     * 
     * @param message the detail message.
     */
    public CreateRoomException(String message) {
        super(message);
    }
}

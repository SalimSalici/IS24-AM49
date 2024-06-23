package it.polimi.ingsw.am49.server.exceptions;

/**
 * Exception thrown when an invalid action is attempted in the game.
 */
public class InvalidActionException extends Exception {
    /**
     * Constructs a new InvalidActionException with the specified detail message.
     * 
     * @param message the detail message.
     */
    public InvalidActionException(String message) {
        super(message);
    }
}
package it.polimi.ingsw.am49.common.exceptions;

/**
 * Exception thrown when a game action is attempted by a user who is not currently in a game.
 */
public class NotInGameException extends Exception {
    /**
     * Constructs a new NotInGameException with a default message.
     */
    public NotInGameException() {
        super("You cannot execute game actions while you're not in a game.");
    }
}

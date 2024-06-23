package it.polimi.ingsw.am49.server.exceptions;

/**
 * Exception thrown when an attempt is made to start a game that has already started.
 */
public class GameAlreadyStartedException extends Exception{

    /**
     * Constructs a new GameAlreadyStartedException with a default error message.
     */
    public GameAlreadyStartedException() {
        super("Game already started");
    }
}

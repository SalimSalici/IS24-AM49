package it.polimi.ingsw.am49.common.exceptions;


/**
 * This exception is thrown when a player tries to make an action while it is not their turn.
 */
public class NotYourTurnException extends Exception {
    public NotYourTurnException(String message) {
        super(message);
    }
}

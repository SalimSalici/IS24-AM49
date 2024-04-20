package it.polimi.ingsw.am49.server.exceptions;

/**
 * This exception is a client chooses an invalid username
 */
public class InvalidUsernameException extends Exception {

    /**
     * @param message the message of the exception
     */
    public InvalidUsernameException(String message) {
        super(message);
    }
}

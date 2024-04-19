package it.polimi.ingsw.am49.server.exceptions;

/**
 * This exception is thrown when a client trying to make a new login into the server chooses a username that
 * is already taken by another already logged in client.
 */
public class InvalidUsernameException extends Exception {

    /**
     * @param message the message of the exception
     */
    public InvalidUsernameException(String message) {
        super(message);
    }
}

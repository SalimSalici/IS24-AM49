package it.polimi.ingsw.am49.server.exceptions;

/**
 * This exception is thrown when a client using the socket communication method receives a return value
 * of an invalid type for the kind of request that was sent to the server.
 * For example, if the client sent a login request to the server, it expects to receive back a boolean return value
 * or an InvalidUsernameException exception. If the return value is not one of these two
 * things, an InvalidReturnTypeException is thrown.
 */
public class InvalidReturnTypeException extends RuntimeException {
    public InvalidReturnTypeException(String message) {
        super(message);
    }
}

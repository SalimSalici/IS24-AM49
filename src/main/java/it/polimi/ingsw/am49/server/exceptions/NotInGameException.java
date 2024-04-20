package it.polimi.ingsw.am49.server.exceptions;

public class NotInGameException extends Exception {
    public NotInGameException() {
        super("You cannot execute game actions while you're not in a game");
    }
}

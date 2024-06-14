package it.polimi.ingsw.am49.server.exceptions;

public class GameAlreadyStartedException extends Exception{

    public GameAlreadyStartedException() {
        super("Game already started");
    }
}

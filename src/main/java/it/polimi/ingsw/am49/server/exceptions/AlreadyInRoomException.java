package it.polimi.ingsw.am49.server.exceptions;

public class AlreadyInRoomException extends Exception {

    private final String gameName;

    public AlreadyInRoomException(String gameName) {
        super("You are already in a game named '" + gameName + "'");
        this.gameName = gameName;
    }

    public String getGameName() {
        return gameName;
    }
}

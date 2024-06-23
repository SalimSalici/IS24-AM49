package it.polimi.ingsw.am49.common.exceptions;

/**
 * Exception thrown when a user tries to join a game room they are already in.
 */
public class AlreadyInRoomException extends Exception {

    /**
     * The name of the game room.
     */
    private final String gameName;

    /**
     * Constructs a new AlreadyInRoomException with the specified game name.
     * 
     * @param gameName the name of the game room the user is already in
     */
    public AlreadyInRoomException(String gameName) {
        super("You are already in a game named '" + gameName + "'");
        this.gameName = gameName;
    }

    /**
     * Returns the name of the game room.
     * 
     * @return the name of the game room
     */
    public String getGameName() {
        return gameName;
    }
}

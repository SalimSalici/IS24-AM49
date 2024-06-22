package it.polimi.ingsw.am49.model.actions;

import java.io.Serializable;

/**
 * Abstract class representing a generic game action.
 * This class serves as a base for all actions that can be performed in the game.
 * It implements Serializable to allow actions to be serialized for network transmission.
 */
public abstract class GameAction implements Serializable {

    /**
     * The type of the action being performed.
     */
    protected final GameActionType type;

    /**
     * The username of the player performing the action.
     */
    protected final String username;

    /**
     * Constructs a new GameAction with the specified type and username.
     * 
     * @param type The type of the game action.
     * @param username The username of the player initiating this action.
     */
    public GameAction(GameActionType type, String username) {
        this.type = type;
        this.username = username;
    }

    /**
     * Returns the type of this action.
     * 
     * @return The game action type.
     */
    public GameActionType getType() {
        return this.type;
    }

    /**
     * Returns the username of the player associated with this action.
     * 
     * @return The username of the player.
     */
    public String getUsername() {
        return this.username;
    }
}

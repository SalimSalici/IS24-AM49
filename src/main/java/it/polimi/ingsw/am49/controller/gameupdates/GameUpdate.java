package it.polimi.ingsw.am49.controller.gameupdates;

import java.io.Serializable;

/**
 * Represents a generic game update in the application.
 * This interface extends {@link Serializable}.
 */
public interface GameUpdate extends Serializable {

    /**
     * @return the type of the game update
     */
    GameUpdateType getType();
}

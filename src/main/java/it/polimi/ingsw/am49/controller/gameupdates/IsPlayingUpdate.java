package it.polimi.ingsw.am49.controller.gameupdates;

import java.util.List;

/**
 * Represents an update indicating whether a player is currently playing or not.
 * This record holds the username of the player and their playing status.
 *
 * @param username the username of the player
 * @param status the playing status of the player; true if playing, false otherwise
 */
public record IsPlayingUpdate(String username, Boolean status) implements GameUpdate {
    /**
     * Returns the type of game update.
     * @return the game update type specific to player's playing status updates.
     */
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.IS_PLAYING_UPDATE;
    }
}
